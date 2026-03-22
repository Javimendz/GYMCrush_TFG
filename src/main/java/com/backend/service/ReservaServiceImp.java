package com.backend.service;

import com.backend.domain.*;
import com.backend.domain.enums.EnumEstado;
import com.backend.dto.ReservaResponseDto;
import com.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservaServiceImp implements IReservaService {

    private final ReservaRepository reservaRepository;
    private final UsuarioRepository usuarioRepository;
    private final HorarioRepository horarioRepository;

    /**
     * Crea una reserva validando duplicados y aforo real del horario.
     */
    @Override
    @Transactional
    public ReservaResponseDto crearReserva(Long usuarioId, Long horarioId) {
        // 1. Validar si el usuario ya reservó para este horario hoy
        if (reservaRepository.existsByUsuarioIdAndHorarioIdAndFecha(usuarioId, horarioId, LocalDate.now())) {
            throw new RuntimeException("Ya tienes una reserva para esta clase hoy.");
        }

        // 2. Buscar el horario para obtener su aforo máximo real
        Horario horario = horarioRepository.findById(horarioId)
                .orElseThrow(() -> new RuntimeException("Horario no encontrado"));

        // 3. Validar aforo dinámico
        if (!hayCupoDisponible(horarioId)) {
            throw new RuntimeException("Clase completa. No quedan plazas disponibles (Límite: " + horario.getAforoMax() + ")");
        }

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // 4. Construir la reserva
        Reserva nuevaReserva = Reserva.builder()
                .usuario(usuario)
                .horario(horario)
                .fecha(LocalDate.now())
                .estado(EnumEstado.CONFIRMADA)
                .confirmado(false) // Será true al escanear el QR
                .build();

        return mapToDto(reservaRepository.save(nuevaReserva));
    }

    /**
     * Obtiene las reservas de un usuario, excluyendo las canceladas.
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
     * Cancela una reserva cambiando su estado (libera el aforo automáticamente).
     */
    @Override
    @Transactional
    public void cancelarReserva(Long reservaId) {
        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new RuntimeException("La reserva no existe"));
        
        LocalTime horaClase = reserva.getHorario().getHoraInicio();
        LocalTime ahora = LocalTime.now();
        LocalDate fechaClase = reserva.getFecha();
        LocalDate hoy = LocalDate.now();
        if(fechaClase.isEqual(hoy)) {
            if (ahora.isAfter(horaClase.minusHours(1))) {
                throw new RuntimeException("No se puede cancelar la reserva con menos de 2 horas de antelación");
            } else if (fechaClase.isBefore(hoy)) {
                throw new RuntimeException("No se puede cancelar la reserva de una clase pasada");
            }
        }
        reserva.setEstado(EnumEstado.CANCELADA);
        reservaRepository.save(reserva);
    }

    /**
     * Verifica si hay cupo comparando inscritos activos vs aforo máximo del horario.
     */
    @Override
    @Transactional(readOnly = true)
    public boolean hayCupoDisponible(Long horarioId) {
        Horario horario = horarioRepository.findById(horarioId)
                .orElseThrow(() -> new RuntimeException("Horario no encontrado"));

        List<EnumEstado> estadosActivos = List.of(EnumEstado.CONFIRMADA, EnumEstado.ASISTIDA);
        long inscritos = reservaRepository.countByHorarioIdAndFechaAndEstadoIn(horarioId, LocalDate.now(), estadosActivos);
        
        return inscritos < horario.getAforoMax();
    }

    /**
     * Lógica para el QR: Confirma que el socio ha llegado al gimnasio.
     */
    @Override
    @Transactional
    public void confirmarAsistencia(Long reservaId) {
        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));
        
        reserva.setEstado(EnumEstado.ASISTIDA);
        reserva.setConfirmado(true);
        reservaRepository.save(reserva);
    }

    /**
     * Mapeo manual para asegurar que Android reciba toda la información visual necesaria.
     */
    private ReservaResponseDto mapToDto(Reserva reserva) {
        ReservaResponseDto dto = new ReservaResponseDto();
        dto.setId(reserva.getId());
        dto.setFecha(reserva.getFecha());
        dto.setEstado(reserva.getEstado());
        
        // Extraemos información de las relaciones para la App móvil
        if (reserva.getHorario() != null) {
            dto.setHoraInicio(reserva.getHorario().getHoraInicio());
            if (reserva.getHorario().getActividad() != null) {
                dto.setActividadNombre(reserva.getHorario().getActividad().getNombre());
            }
        }
        return dto;
    }
}