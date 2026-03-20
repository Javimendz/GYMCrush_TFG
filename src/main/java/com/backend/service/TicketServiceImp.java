package com.backend.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.backend.domain.enums.EnumEstadoTicket;
import com.backend.dto.TicketRequestDto;
import com.backend.dto.TicketResponseDto;
import com.backend.exceptions.ResourceNotFoundException;
import com.backend.repository.TicketRepository;
import com.backend.repository.UsuarioRepository;
import com.backend.domain.Ticket;
import com.backend.domain.Usuario;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Service
@Data
@Builder
public class TicketServiceImp implements ITicketService {

    private final TicketRepository ticketRepository;
    private final UsuarioRepository usuarioRepository;

    @Override
    public TicketResponseDto crearTicket(TicketRequestDto dto, Long usuarioId) {

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + usuarioId));

        Ticket ticket = Ticket.builder()
                .asunto(dto.getAsunto())
                .mensaje(dto.getMensaje())
                .usuario(usuario)
                .estado(EnumEstadoTicket.ABIERTO)
                .build();

        return mapToDto(ticketRepository.save(ticket));

    }

    @Override
    public List<TicketResponseDto> obtenerPorUsuario(Long usuarioId) {

        return ticketRepository.findByUsuarioOrderByFechaCreacionDesc(usuarioId)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<TicketResponseDto> obtenerTodos() {

        return ticketRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public TicketResponseDto obtenerPorId(Long ticketId) {

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket no encontrado con ID: " + ticketId));
        return mapToDto(ticket);

    }

    @Override
    public TicketResponseDto actualizarEstado(Long ticketId, EnumEstadoTicket nuevoEstado) {

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket no encontrado con ID: " + ticketId));

        ticket.setEstado(nuevoEstado);

        if (nuevoEstado == EnumEstadoTicket.CERRADO)
            ticket.setFechaResolucion(LocalDateTime.now());
        else
            ticket.setFechaResolucion(null);

        return mapToDto(ticketRepository.save(ticket));

    }

    @Override
    public void eliminarTicket(Long ticketId) {

        if (!ticketRepository.existsById(ticketId))
            throw new ResourceNotFoundException("No se puede eliminar el ticket, no existe");

        ticketRepository.deleteById(ticketId);

    }

    // Metodo privado para mapear a dto
    private TicketResponseDto mapToDto(Ticket t) {

        return TicketResponseDto.builder()
                .id(t.getId())
                .asunto(t.getAsunto())
                .mensaje(t.getMensaje())
                .estado(t.getEstado().name())
                .fechaCreacion(t.getFechaCreacion())
                .fechaResolucion(t.getFechaResolucion())
                .username(t.getUsuario().getUsername())
                .build();

    }

}
