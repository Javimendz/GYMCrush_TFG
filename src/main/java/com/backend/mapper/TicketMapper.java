package com.backend.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.backend.domain.Ticket;
import com.backend.dto.TicketResponseDto;

@Mapper(componentModel = "spring")
public interface TicketMapper {


    //De entidad a dto, para mostrar datos al usuario y el admin
    @Mapping(target = "usuarioId", source= "usuario.id")
    @Mapping(target= "usuarioEmail", source="usuario.correo")
    @Mapping(target= "nombreCompletoUsuario", 
            expression = "java(ticket.getUsuario().getNombre() + \"\" + ticket.getUsuario().getApellidos())")
    TicketResponseDto toDto(Ticket ticket);
    
    // de requestDto a Entidad, para cuando el usuario crea un ticket
    //ignoro los campos que el sistema genera automaticamente
    @Mapping(target="id", ignore=true)
    @Mapping(target="estado", ignore=true)
    @Mapping(target="fechaCreacion", ignore=true)
    @Mapping(target="fechaResolucion", ignore=true)
    @Mapping(target="usuario", ignore=true)
    Ticket toEntity(TicketResponseDto dto);

    //Mapeo de listas, resulta util para el historial y el panel de admin
    List<TicketResponseDto> toDtoList(List<Ticket> tickets);
}
