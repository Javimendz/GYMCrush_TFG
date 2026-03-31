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
        log.info("Creando reserva para usuario {} en horario {}", usuarioId, horarioId);

        // Validar que el usuario no tenga reserva activa para el mismo día
        if (reservaRepository.existsByUsuarioIdAndHorarioIdAndFecha(usuarioId, horarioId, LocalDate.now())) {
            log.warn("El usuario {} ya tiene una reserva para esta clase hoy", usuarioId);
            throw new RuntimeException("Ya tienes una reserva para esta clase hoy.");
        }

        // Buscar horario y validar aforo
        Horario horario = horarioRepository.findById(horarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Horario no encontrado"));

        if (!hayCupoDisponible(horarioId)) {
            throw new RuntimeException("Clase completa. Límite: " + horario.getAforoMax());
        }

        // Buscar usuario y crear reserva
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        // Crear la reserva con estado CONFIRMADA
        Reserva reserva = Reserva.builder()
                .usuario(usuario)
                .horario(horario)
                .fecha(LocalDate.now())
                .estado(EnumEstado.CONFIRMADA)
                .build();

        // Guardar y retornar DTO
        return mapToDto(reservaRepository.save(reserva));
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
                .orElseThrow(() -> new ResourceNotFoundException("La reserva no existe"));

        reserva.setEstado(EnumEstado.CANCELADA);
        reservaRepository.save(reserva);
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

        reserva.setEstado(EnumEstado.ASISTIDA);
        // reserva.setConfirmado(true); // Asegúrate de tener este campo en la Entidad
        // Reserva
        reservaRepository.save(reserva);
    }

    /**
     * Obtiene la lista de personas apuntadas a una clase específica para el día de
     * hoy.
     * * @param horarioId ID del horario de la clase
     * 
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
     * * @param horarioId ID del horario
     * 
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
                .estado(reserva.getEstado().name()) // O reserva.getEstado().name()
                .diaSemana(reserva.getHorario() != null ? reserva.getHorario().getDiaSemana() : null)
                .horaInicio(reserva.getHorario() != null ? reserva.getHorario().getHoraInicio() : null)
                .nombreActividad(reserva.getHorario() != null && reserva.getHorario().getActividad() != null
                        ? reserva.getHorario().getActividad().getNombre()
                        : "Sin nombre")
                .nombreActividad(reserva.getHorario() != null && reserva.getHorario().getActividad() != null
                        ? reserva.getHorario().getActividad().getNombre()
                        : "Sin Actividad")
                .build();
    }
}