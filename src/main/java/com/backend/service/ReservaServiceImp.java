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
import com.backend.exceptions.ResourceNotFoundException; // Usamos tu excepción personalizada
import com.backend.repository.ReservaRepository;
import com.backend.repository.UsuarioRepository;
import com.backend.repository.HorarioRepository; 

import lombok.RequiredArgsConstructor; 

@Service
@RequiredArgsConstructor 
public class ReservaServiceImp implements IReservaService {

    private final ReservaRepository reservaRepository;
    private final UsuarioRepository usuarioRepository;
    private final HorarioRepository horarioRepository; 

    @Override
    @Transactional
    public ReservaResponseDto crearReserva(Long usuarioId, Long horarioId) {
        
        if (reservaRepository.existsByUsuarioIdAndHorarioIdAndFecha(usuarioId, horarioId, LocalDate.now())) {
            throw new RuntimeException("Ya tienes una reserva para esta clase hoy.");
        }

        // Buscar horario y validar aforo
        Horario horario = horarioRepository.findById(horarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Horario no encontrado"));

        if (!hayCupoDisponible(horarioId)) {
            throw new RuntimeException("Clase completa. Límite: " + horario.getAforoMax());
        }

        //  Buscar usuario
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        // Creo con Builder 
        Reserva nuevaReserva = Reserva.builder()
                .usuario(usuario)
                .horario(horario)
                .fecha(LocalDate.now())
                .estado(EnumEstado.CONFIRMADA)
                .build();

        return mapToDto(reservaRepository.save(nuevaReserva));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservaResponseDto> obtenerReservasUsuario(Long usuarioId) {
        return reservaRepository.findByUsuarioIdAndEstadoNot(usuarioId, EnumEstado.CANCELADA)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void cancelarReserva(Long reservaId) {
        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new ResourceNotFoundException("La reserva no existe"));
        
        reserva.setEstado(EnumEstado.CANCELADA);
        reservaRepository.save(reserva);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hayCupoDisponible(Long horarioId) {
        Horario horario = horarioRepository.findById(horarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Horario no encontrado"));

        List<EnumEstado> estadosActivos = List.of(EnumEstado.CONFIRMADA, EnumEstado.ASISTIDA);
        long inscritos = reservaRepository.countByHorarioIdAndFechaAndEstadoIn(horarioId, LocalDate.now(), estadosActivos);
        
        return inscritos < horario.getAforoMax();
    }

    @Override
    @Transactional
    public void confirmarAsistencia(Long reservaId) {
        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada"));
        
        reserva.setEstado(EnumEstado.ASISTIDA);
        // reserva.setConfirmado(true); // Asegúrate de tener este campo en la Entidad Reserva
        reservaRepository.save(reserva);
    }

    private ReservaResponseDto mapToDto(Reserva reserva) {
        // Mapeo manual optimizado para tu App de Android
        return ReservaResponseDto.builder()
                .id(reserva.getId())
                .fecha(reserva.getFecha())
                .estado(reserva.getEstado().name()) // O reserva.getEstado().name()
                .horaInicio(reserva.getHorario() != null ? reserva.getHorario().getHoraInicio() : null)
                .actividadNombre(reserva.getHorario() != null && reserva.getHorario().getActividad() != null 
                                 ? reserva.getHorario().getActividad().getNombre() : "Sin nombre")
                .build();
    }
}