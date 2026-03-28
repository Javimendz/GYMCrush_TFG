package com.backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.backend.domain.Ticket;
import com.backend.dto.TicketResponseDto;

@Mapper(componentModel = "spring")
public interface TicketMapper {

@Mapping(target = "usuarioId", source = "usuario.id") 
    @Mapping(target = "usuarioEmail", source = "usuario.correo")
    @Mapping(target = "username", source = "usuario.username")
    @Mapping(target = "nombreCompletoUsuario", source = "ticket")
    TicketResponseDto toDto(Ticket ticket);

    // Este método une el nombre y apellidos navegando Ticket -> Usuario -> Perfil
    default String mapNombreCompleto(Ticket ticket) {
        if (ticket.getUsuario() == null || ticket.getUsuario().getPerfil() == null) {
            return "Sin perfil configurado";
        }
        
        String nombre = ticket.getUsuario().getPerfil().getNombre();
        String apellidos = ticket.getUsuario().getPerfil().getApellidos();
        
        String resultado = (nombre != null ? nombre : "") + " " + (apellidos != null ? apellidos : "");
        return resultado.trim().isEmpty() ? "Nombre no definido" : resultado.trim();
    }
}
