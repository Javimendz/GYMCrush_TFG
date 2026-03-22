package com.backend.service;

import java.util.List;

import com.backend.dto.ReservaResponseDto;

public interface IReservaService  {

    
     public List<ReservaResponseDto> obtenerReservasUsuario(Long usuarioId);

      public ReservaResponseDto crearReserva(Long usuarioId, Long horarioId);

      public void cancelarReserva(Long reservaId);

       public boolean hayCupoDisponible(Long horarioId);

       public void confirmarAsistencia(Long reservaId);

       
}
