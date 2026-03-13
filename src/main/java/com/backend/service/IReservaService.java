package com.backend.service;

import com.backend.dto.ReservaResponseDto;
import java.util.List;

public interface IReservaService {
    ReservaResponseDto crearReserva(Long usuarioId, Long horarioId);
    List<ReservaResponseDto> obtenerReservasUsuario(Long usuarioId);
    void cancelarReserva(Long reservaId);
    boolean hayCupoDisponible(Long horarioId);
}
