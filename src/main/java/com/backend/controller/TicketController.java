package com.backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.backend.domain.enums.EnumEstadoTicket;
import com.backend.dto.TicketRequestDto;
import com.backend.dto.TicketResponseDto;
import com.backend.security.dto.ApiResponseDto;
import com.backend.service.ITicketService;

import jakarta.validation.Valid;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/v1/tickets")
@RequiredArgsConstructor
@Data
public class TicketController {
    

    private final ITicketService ticketService;

    //Endpoint para crear ticket
    @PostMapping("/usuario/{id}")
    @PreAuthorize("hasAnyRole('ROLE_USUARIO, 'ROLE_ADMIN')")
    public ResponseEntity<ApiResponseDto<TicketResponseDto>> crear(
        @PathVariable Long usuarioId,
        @Valid @RequestBody TicketRequestDto dto
    ){

        TicketResponseDto nuevoTicket = ticketService.crearTicket(dto, usuarioId);

        return ResponseEntity.ok(ApiResponseDto.<TicketResponseDto>builder()
                .mensaje("Ticket creado exitosamente. Revisaremos tu mensaje pronto.")
                .success(true)
                .datos(nuevoTicket) 
                .build());
    
        
    }

    //Para el usuario ver su historial de tickets
    @GetMapping("/usuario/{usuarioId}")
    @PreAuthorize("hasAnyRole('ROLE_USUARIO,  'ROLE_ADMIN)")
    public ResponseEntity<ApiResponseDto<List<TicketResponseDto>>> verMisTickets(
        @PathVariable Long usuarioIdLong
    ){

        List<TicketResponseDto> lista = ticketService.obtenerPorUsuario(usuarioIdLong);

        return ResponseEntity.ok(ApiResponseDto.<List<TicketResponseDto>>builder()
                .mensaje("Hisotrial de tickets recuperado")
                .success(true)
                .datos(lista)
                .build()
            );

    }

    //Admin puede ver todos los tickets
    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponseDto<List<TicketResponseDto>>> verTodos() {
        
        List<TicketResponseDto> todos = ticketService.obtenerTodos();

        return ResponseEntity.ok(ApiResponseDto.<List<TicketResponseDto>>builder()
                .mensaje("Panel de administración: Todos los tickets")
                .success(true)
                .datos(todos)
                .build());
    }

    //para que el admin resuelva el estado del ticket
    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponseDto<TicketResponseDto>> resolverTicket(
        @PathVariable Long id,
        @RequestParam EnumEstadoTicket nuevoEstado
    ){
        TicketResponseDto actualizado = ticketService.actualizarEstado(id, nuevoEstado);

        return ResponseEntity.ok(ApiResponseDto.<TicketResponseDto>builder()
            .mensaje("Estado del ticket actualizado a: " + nuevoEstado)
            .success(true)
            .datos(actualizado)
            .build()
    );

    }
}
