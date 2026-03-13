package com.backend.service;

import com.backend.domain.*;
import com.backend.domain.enums.EnumEstado;
import com.backend.dto.ReservaResponseDto;
import com.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservaServiceImp implements IReservaService {

    private final ReservaRepository reservaRepository;
    private final UsuarioRepository usuarioRepository;
    private final HorarioRepository horarioRepository;

    private static final int CUPO_MAXIMO = 20;

    @Override
    @Transactional
    public ReservaResponseDto crearReserva(Long usuarioId, Long horarioId) {
        // 1. Validar si ya existe reserva para hoy
        if (reservaRepository.existsByUsuarioIdAndHorarioIdAndFecha(usuarioId, horarioId, LocalDate.now())) {
            throw new RuntimeException("Ya tienes una reserva para esta clase hoy.");
        }

        // 2. Validar aforo
        if (!hayCupoDisponible(horarioId)) {
            throw new RuntimeException("Clase completa. No quedan cupos disponibles.");
        }

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Horario horario = horarioRepository.findById(horarioId)
                .orElseThrow(() -> new RuntimeException("Horario no encontrado"));

        Reserva nuevaReserva = Reserva.builder()
                .usuario(usuario)
                .horario(horario)
                .estado(EnumEstado.CONFIRMADA)
                .build();

        return mapToDto(reservaRepository.save(nuevaReserva));
    }

    @Override
    public List<ReservaResponseDto> obtenerReservasUsuario(Long usuarioId) {
        return reservaRepository.findByUsuarioId(usuarioId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void cancelarReserva(Long reservaId) {
        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new RuntimeException("La reserva no existe"));
        reserva.setEstado(EnumEstado.CANCELADA);
        reservaRepository.save(reserva);
    }

    @Override
    public boolean hayCupoDisponible(Long horarioId) {
        long inscritos = reservaRepository.countByHorarioIdAndFecha(horarioId, LocalDate.now());
        return inscritos < CUPO_MAXIMO;
    }

    private ReservaResponseDto mapToDto(Reserva reserva) {
        ReservaResponseDto dto = new ReservaResponseDto();
        dto.setId(reserva.getId());
        dto.setFecha(reserva.getFecha());
        dto.setEstado(reserva.getEstado());
        return dto;
    }
}