package com.backend.service;

import com.backend.dto.ReservaResponseDto;
import java.util.List;

public interface IReservaService {
    // 1. Crear (Valida aforo y duplicados)
    ReservaResponseDto crearReserva(Long usuarioId, Long horarioId);//En pantalla Actividades boton reservar
    
    // 2. Listar (Usa el filtro de "EstadoNot" para no ver canceladas)
    List<ReservaResponseDto> obtenerReservasUsuario(Long usuarioId);
    
    // 3. Cancelar (Cambia a estado CANCELADA y libera el hueco)
    void cancelarReserva(Long reservaId);//En pantalla Mis Reservas
    
    // 4. Consultar (Para que la App sepa si mostrar el botón de reservar)
    boolean hayCupoDisponible(Long horarioId);//En pantalla Actividades

    // 5. LO QUE FALTA: Confirmar Asistencia (El "Check-in" del QR)
    //void confirmarAsistencia(Long reservaId);
}
