package com.backend.service;

import java.util.List;

import com.backend.domain.enums.EnumEstadoTicket;
import com.backend.dto.TicketRequestDto;
import com.backend.dto.TicketResponseDto;

public interface ITicketService {

    // Para crear un ticket
    TicketResponseDto crearTicket(TicketRequestDto dto, Long usuarioId);

    // para obtener un ticker por usuario
    List<TicketResponseDto> obtenerPorUsuario(Long usuarioId);

    // para obtener todos los tickets
    List<TicketResponseDto> obtenerTodos();

    // para obtener un ticket por id
    TicketResponseDto obtenerPorId(Long ticketId);

    // para actualizar el estado de un ticket, si esta revisado o no
    TicketResponseDto actualizarEstado(Long ticketId, EnumEstadoTicket nuevoEstado);

    // para eliminar un ticket
    void eliminarTicket(Long ticketId);

}
