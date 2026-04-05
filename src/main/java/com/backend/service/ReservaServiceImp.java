package com.backend.service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.domain.Horario;
import com.backend.domain.Reserva;
import com.backend.domain.Usuario;
import com.backend.domain.enums.EnumEstado;
import com.backend.dto.ReservaResponseDto;
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

    /** Repositorio para el acceso a datos de reservas */
    private final ReservaRepository reservaRepository;
    
    /** Repositorio para el acceso a datos de usuarios */
    private final UsuarioRepository usuarioRepository;
    
    /** Repositorio para el acceso a datos de horarios */
    private final HorarioRepository horarioRepository; 
    private final NotificacionService notificacionService;

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
     * @throws RuntimeException si el usuario ya tiene reserva activa o no hay cupo
     */
    @Override
    @Transactional
    public ReservaResponseDto crearReserva(Long usuarioId, Long horarioId) {
    log.info("Creando reserva para usuario {} en horario {}", usuarioId, horarioId);
    
    // 1. Validaciones existentes
    if (reservaRepository.existsByUsuarioIdAndHorarioIdAndFecha(usuarioId, horarioId, LocalDate.now())) {
        log.warn("El usuario {} ya tiene una reserva para esta clase hoy", usuarioId);
        throw new RuntimeException("Ya tienes una reserva para esta clase hoy.");
    }

    Horario horario = horarioRepository.findById(horarioId)
            .orElseThrow(() -> new ResourceNotFoundException("Horario no encontrado"));

    if (!hayCupoDisponible(horarioId)) {
        throw new RuntimeException("Clase completa. Límite: " + horario.getAforoMax());
    }

    Usuario usuario = usuarioRepository.findById(usuarioId)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
    
    // 2. Creación de la reserva
    Reserva reserva = Reserva.builder()
            .usuario(usuario)
            .horario(horario)
            .fecha(LocalDate.now())
            .estado(EnumEstado.CONFIRMADA)
            .build();

    Reserva guardada = reservaRepository.save(reserva);

    // 3. DISPARADOR DE NOTIFICACIÓN
    notificacionService.enviarNotificacionRapida(
        usuarioId, 
        "Reserva Confirmada", 
        "Has reservado plaza para " + horario.getActividad().getNombre() + " hoy a las " + horario.getHoraInicio(), 
        com.backend.domain.enums.TipoNotificacion.RECORDATORIO
    );

    return mapToDto(guardada);
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
    // 1. Buscar la reserva existente
    Reserva reserva = reservaRepository.findById(reservaId)
            .orElseThrow(() -> new ResourceNotFoundException("La reserva no existe"));

    // --- NUEVA REGLA DE NEGOCIO: POLÍTICA DE CANCELACIÓN (2 HORAS) ---
    // Combinamos la fecha de la reserva con la hora de inicio de la clase
    java.time.LocalDateTime ahora = java.time.LocalDateTime.now();
    java.time.LocalDateTime fechaHoraClase = java.time.LocalDateTime.of(
            reserva.getFecha(), 
            reserva.getHorario().getHoraInicio()
    );

    // Si la hora actual es posterior al límite (Hora clase - 2 horas), lanzamos error
    if (ahora.isAfter(fechaHoraClase.minusHours(2))) {
        log.warn("Intento de cancelación fuera de plazo para reserva ID: {}", reservaId);
        throw new RuntimeException("No puedes cancelar: El límite de tiempo es de 2 horas antes de la clase.");
    }
    // -----------------------------------------------------------------

    // 2. Cambiar el estado a CANCELADA
    reserva.setEstado(EnumEstado.CANCELADA);
    reservaRepository.save(reserva);

    // 3. DISPARADOR DE NOTIFICACIÓN
    String nombreActividad = (reserva.getHorario() != null && reserva.getHorario().getActividad() != null) 
                             ? reserva.getHorario().getActividad().getNombre() 
                             : "clase";

    notificacionService.enviarNotificacionRapida(
        reserva.getUsuario().getId(), 
        "Reserva Cancelada", 
        "Has cancelado tu reserva para la clase de " + nombreActividad + ".", 
        com.backend.domain.enums.TipoNotificacion.SISTEMA
    );
    
    log.info("Notificación de cancelación enviada al usuario {}", reserva.getUsuario().getId());
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
        long inscritos = reservaRepository.countByHorarioIdAndFechaAndEstadoIn(horarioId, LocalDate.now(), estadosActivos);
        
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
    
    // REGLA TFG: Solo permitir check-in si la clase es HOY
    if (!reserva.getFecha().equals(LocalDate.now())) {
        throw new RuntimeException("Solo puedes confirmar asistencia el día de la clase.");
    }

    // REGLA TFG: Ventana de tiempo (15 min antes hasta 10 min después)
    java.time.LocalTime ahora = java.time.LocalTime.now();
    java.time.LocalTime inicioClase = reserva.getHorario().getHoraInicio();

    if (ahora.isBefore(inicioClase.minusMinutes(15)) || ahora.isAfter(inicioClase.plusMinutes(10))) {
        throw new RuntimeException("El Check-in solo está disponible 15 minutos antes del inicio.");
    }

    reserva.setEstado(EnumEstado.ASISTIDA);
    reservaRepository.save(reserva);
    log.info("Asistencia confirmada para reserva ID: {}", reservaId);
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
                .estado(reserva.getEstado().name()) // O reserva.getEstado().name()
                .diaSemana(reserva.getHorario() != null ? reserva.getHorario().getDiaSemana() : null)
                .horaInicio(reserva.getHorario() != null ? reserva.getHorario().getHoraInicio() : null)               
                .nombreActividad(reserva.getHorario() != null && reserva.getHorario().getActividad() != null 
                                 ? reserva.getHorario().getActividad().getNombre() : "Sin nombre")
                
            .build();
    }
}