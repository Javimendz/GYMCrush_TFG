package com.backend.service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.domain.Horario;
import com.backend.domain.Reserva;
import com.backend.domain.Usuario;
import com.backend.domain.enums.EnumEstado;
import com.backend.domain.enums.TipoNotificacion;
import com.backend.dto.ReservaResponseDto;
import com.backend.event.ReservaEvent;
import com.backend.exceptions.ResourceNotFoundException;
import com.backend.repository.ReservaRepository;
import com.backend.repository.UsuarioRepository;
import com.backend.repository.HorarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementación del servicio para la gestión de reservas de clases.
 * <p>
 * Esta clase maneja la creación, consulta y cancelación de reservas,
 * incluyendo validación de aforo y conflictos de horarios.
 * </p>
 *
 * @author Backend Team
 * @version 1.0
 * @since 2026
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservaServiceImp implements IReservaService {

    private final ReservaRepository reservaRepository;

    private final UsuarioRepository usuarioRepository;

    private final HorarioRepository horarioRepository;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * Crea una nueva reserva para un usuario en un horario específico.
     * <p>
     * Valida que el usuario no tenga una reserva activa para el mismo día,
     * verifica disponibilidad de aforo y crea la reserva con estado CONFIRMADA.
     * </p>
     *
     * @param usuarioId ID del usuario que realiza la reserva
     * @param horarioId ID del horario a reservar
     * @return DTO con los datos de la reserva creada
     * @throws ResourceNotFoundException si el horario no existe
     * @throws RuntimeException          si el usuario ya tiene reserva activa o no
     *                                   hay cupo
     */
    @Override
    @Transactional
    public ReservaResponseDto crearReserva(Long usuarioId, Long horarioId) {
        log.info("Iniciando proceso de reserva: Usuario {}, Horario {}", usuarioId, horarioId);

        // 1. Obtener Horario y Usuario
        Horario horario = horarioRepository.findById(horarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Horario no encontrado"));
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        //  CALCULAR LA FECHA REAL DE LA CLASE (Basado en el día de la semana)
        // Esto evita que si hoy es Miércoles y reservas un Jueves, se guarde con fecha
        // de hoy.
        LocalDate fechaClase = calcularProximaFecha(horario.getDiaSemana());

        java.time.LocalDateTime ahora = java.time.LocalDateTime.now();
        java.time.LocalDateTime fechaHoraInicioClase = java.time.LocalDateTime.of(fechaClase, horario.getHoraInicio());

        // VALIDACIÓN TEMPORAL ¿La clase ya pasó o empieza en menos de 5 minutos?
        if (ahora.isAfter(fechaHoraInicioClase.minusMinutes(5))) {
            log.warn("Intento de reserva tardía para la clase de las {}", horario.getHoraInicio());
            throw new RuntimeException("No puedes reservar: La clase ya comenzó o falta muy poco para el inicio.");
        }

        // VALIDAR DUPLICADO: ¿El usuario ya tiene ESTA misma clase este día?
        if (reservaRepository.existsByUsuarioIdAndHorarioIdAndFecha(usuarioId, horarioId, fechaClase)) {
            throw new RuntimeException("Ya tienes una reserva confirmada para esta sesión el día " + fechaClase);
        }

        //  VALIDAR SOLAPAMIENTO: ¿El usuario ya tiene OTRA actividad a esta misma
        // hora?
        boolean estaOcupado = reservaRepository.existsByUsuarioIdAndFechaAndHorario_HoraInicioAndEstadoNot(
                usuarioId, fechaClase, horario.getHoraInicio(), EnumEstado.CANCELADA);
        if (estaOcupado) {
            throw new RuntimeException(
                    "Ya tienes otra actividad reservada a las " + horario.getHoraInicio() + " el día " + fechaClase);
        }

        //  VALIDAR CUPO (Aforo)
        List<EnumEstado> estadosActivos = List.of(EnumEstado.CONFIRMADA, EnumEstado.ASISTIDA);
        long inscritos = reservaRepository.countByHorarioIdAndFechaAndEstadoIn(horarioId, fechaClase, estadosActivos);

        if (inscritos >= horario.getAforoMax()) {
            throw new RuntimeException("Clase completa. El aforo máximo es de " + horario.getAforoMax());
        }

        //  CREAR Y GUARDAR LA RESERVA
        Reserva reserva = Reserva.builder()
                .usuario(usuario)
                .horario(horario)
                .fecha(fechaClase) // Usamos la fecha calculada
                .estado(EnumEstado.CONFIRMADA)
                .confirmado(false)
                .build();

        Reserva guardada = reservaRepository.save(reserva);

        eventPublisher.publishEvent(new ReservaEvent(
                guardada.getId(), //pasamos id reserva
                "Reserva Exitosa",
                "Plaza confirmada para " + horario.getActividad().getNombre() + " el " + fechaClase + " a las "
                        + horario.getHoraInicio(),
                TipoNotificacion.RECORDATORIO));

        return mapToDto(guardada);
    }

    /**
     * Calcula la fecha del próximo día de la semana indicado.
     * Si el día indicado es HOY y la hora aún no ha pasado, devuelve hoy.
     * Si no, devuelve la fecha del día la próxima semana.
     */
    private LocalDate calcularProximaFecha(String diaSemana) {
        java.time.DayOfWeek targetDay = switch (diaSemana.toUpperCase()) {
            case "LUNES" -> java.time.DayOfWeek.MONDAY;
            case "MARTES" -> java.time.DayOfWeek.TUESDAY;
            case "MIERCOLES", "MIÉRCOLES" -> java.time.DayOfWeek.WEDNESDAY;
            case "JUEVES" -> java.time.DayOfWeek.THURSDAY;
            case "VIERNES" -> java.time.DayOfWeek.FRIDAY;
            case "SABADO", "SÁBADO" -> java.time.DayOfWeek.SATURDAY;
            case "DOMINGO" -> java.time.DayOfWeek.SUNDAY;
            default -> throw new IllegalArgumentException("Día de la semana no válido: " + diaSemana);
        };

        return LocalDate.now().with(java.time.temporal.TemporalAdjusters.nextOrSame(targetDay));
    }

    /**
     * Obtiene las reservas de un usuario.
     * <p>
     * Retorna una lista de DTO con los datos de las reservas del usuario,
     * excluyendo las reservas canceladas.
     * </p>
     *
     * @param usuarioId ID del usuario
     * @return Lista de DTO con los datos de las reservas del usuario
     */
    @Override
    @Transactional(readOnly = true)
    public List<ReservaResponseDto> obtenerReservasUsuario(Long usuarioId) {
        return reservaRepository.findByUsuarioIdAndEstadoNot(usuarioId, EnumEstado.CANCELADA)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    /**
     * Cancela una reserva.
     * <p>
     * Cambia el estado de una reserva a CANCELADA.
     * </p>
     *
     * @param reservaId ID de la reserva a cancelar
     */
    @Override
    @Transactional
    public void cancelarReserva(Long reservaId) {
        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada"));

        java.time.LocalDateTime ahora = java.time.LocalDateTime.now();
        java.time.LocalDateTime fechaHoraClase = java.time.LocalDateTime.of(reserva.getFecha(),
                reserva.getHorario().getHoraInicio());

        // Si la clase ya terminó o empezó
        if (ahora.isAfter(fechaHoraClase)) {
            throw new RuntimeException("No puedes cancelar: Esta clase ya ha pasado o está en curso.");
        }

        // Si faltan menos de 2 horas (pero aún no ha empezado)
        if (ahora.isAfter(fechaHoraClase.minusHours(2))) {
            log.warn("Intento de cancelación tardía para reserva ID: {}", reservaId);
            throw new RuntimeException(
                    "Se ha pasado el límite permitido para cancelar (debe ser al menos 2 horas antes).");
        }

        reserva.setEstado(EnumEstado.CANCELADA);
        reservaRepository.save(reserva);

        eventPublisher.publishEvent(new ReservaEvent(
                reserva.getId(),
                "Reserva Cancelada",
                "Has cancelado tu clase de " + reserva.getHorario().getActividad().getNombre(),
                TipoNotificacion.CANCELACION));

        log.info("Reserva {} cancelada exitosamente por el usuario", reservaId);
    }

    /**
     * Verifica si hay cupo disponible para un horario.
     * <p>
     * Retorna true si hay cupo disponible, false en caso contrario.
     * </p>
     *
     * @param horarioId ID del horario
     * @return true si hay cupo disponible, false en caso contrario
     */
    @Override
    @Transactional(readOnly = true)
    public boolean hayCupoDisponible(Long horarioId) {
        Horario horario = horarioRepository.findById(horarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Horario no encontrado"));

        List<EnumEstado> estadosActivos = List.of(EnumEstado.CONFIRMADA, EnumEstado.ASISTIDA);
        long inscritos = reservaRepository.countByHorarioIdAndFechaAndEstadoIn(horarioId, LocalDate.now(),
                estadosActivos);

        return inscritos < horario.getAforoMax();
    }

    /**
     * Confirma la asistencia de un usuario a una reserva.
     * <p>
     * Cambia el estado de una reserva a ASISTIDA.
     * </p>
     *
     * @param reservaId ID de la reserva
     */
    @Override
    @Transactional
    public void confirmarAsistencia(Long reservaId) {
        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada"));

        // Validar que el check-in sea el día correcto
        if (!reserva.getFecha().equals(LocalDate.now())) {
            throw new RuntimeException("Solo puedes hacer check-in el día de la clase.");
        }

        // Ventana de Check-in: 15 min antes hasta 10 min después del inicio
        java.time.LocalTime ahora = java.time.LocalTime.now();
        java.time.LocalTime inicio = reserva.getHorario().getHoraInicio();

        if (ahora.isBefore(inicio.minusMinutes(15)) || ahora.isAfter(inicio.plusMinutes(10))) {
            throw new RuntimeException(
                    "El Check-in solo se permite desde 15 min antes hasta 10 min después del inicio.");
        }

        reserva.setEstado(EnumEstado.ASISTIDA);
        reservaRepository.save(reserva);
    }

    /**
     * Obtiene la lista de personas apuntadas a una clase específica para el día de
     * hoy.
     *
     * @param horarioId ID del horario de la clase
     * @return Lista de DTOs con la información de los alumnos inscritos
     */
    @Transactional(readOnly = true)
    public List<ReservaResponseDto> obtenerAsistentesPorHorario(Long horarioId) {
        log.info("Consultando lista de asistentes para el horario {}", horarioId);

        // Estados que consideramos como "apuntados" (excluimos cancelados)
        List<EnumEstado> estadosActivos = List.of(EnumEstado.CONFIRMADA, EnumEstado.ASISTIDA);

        return reservaRepository.findByHorarioIdAndFechaAndEstadoIn(horarioId, LocalDate.now(), estadosActivos)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene un resumen del aforo actual de una clase.
     *
     * @param horarioId ID del horario
     * @return String con formato "inscritos/total"
     */
    @Transactional(readOnly = true)
    public String obtenerResumenAforo(Long horarioId) {
        Horario horario = horarioRepository.findById(horarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Horario no encontrado"));

        List<EnumEstado> estadosActivos = List.of(EnumEstado.CONFIRMADA, EnumEstado.ASISTIDA);
        long inscritos = reservaRepository.countByHorarioIdAndFechaAndEstadoIn(horarioId, LocalDate.now(),
                estadosActivos);

        return inscritos + " / " + horario.getAforoMax();
    }

    /**
     * Mapea una reserva a un DTO.
     * <p>
     * Mapeo manual optimizado para la App de Android.
     * </p>
     *
     * @param reserva Reserva a mapear
     * @return DTO con los datos de la reserva
     */
    private ReservaResponseDto mapToDto(Reserva reserva) {
        // Mapeo manual optimizado para la App de Android
        return ReservaResponseDto.builder()
                .id(reserva.getId())
                .fecha(reserva.getFecha())
                .username(reserva.getUsuario() != null ? reserva.getUsuario().getUsername() : "Anónimo")
                .estado(reserva.getEstado().name())
                .diaSemana(reserva.getHorario() != null ? reserva.getHorario().getDiaSemana() : null)
                .horaInicio(reserva.getHorario() != null ? reserva.getHorario().getHoraInicio() : null)
                .nombreActividad(reserva.getHorario() != null && reserva.getHorario().getActividad() != null
                        ? reserva.getHorario().getActividad().getNombre()
                        : "Sin Actividad")
                .build();
    }

}