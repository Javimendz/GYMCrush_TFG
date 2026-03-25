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
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/v1/tickets")
@RequiredArgsConstructor
@io.swagger.v3.oas.annotations.tags.Tag(name = "Ticket", description = "Operaciones relacionadas con los tickets de soporte")
public class TicketController {
    

    private final ITicketService ticketService;

    //Endpoint para crear ticket
    @PostMapping("/usuario/{usuarioId}")
    @PreAuthorize("hasAnyRole('USUARIO', 'ADMIN')")
    @io.swagger.v3.oas.annotations.Operation(summary = "Crear ticket", description = "Permite a un usuario crear un nuevo ticket de soporte")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Ticket creado exitosamente")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Solicitud inválida")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
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
    @PreAuthorize("hasAnyRole('USUARIO', 'ADMIN')")
    @io.swagger.v3.oas.annotations.Operation(summary = "Ver mis tickets", description = "Permite a un usuario ver su historial de tickets de soporte")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Historial de tickets recuperado con éxito")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "No se encontró historial para el usuario")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<ApiResponseDto<List<TicketResponseDto>>> verMisTickets(
        @PathVariable Long usuarioId
    ){

        List<TicketResponseDto> lista = ticketService.obtenerPorUsuario(usuarioId);

        return ResponseEntity.ok(ApiResponseDto.<List<TicketResponseDto>>builder()
                .mensaje("Hisotrial de tickets recuperado")
                .success(true)
                .datos(lista)
                .build()
            );

    }

    //Admin puede ver todos los tickets
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @io.swagger.v3.oas.annotations.Operation(summary = "Ver todos los tickets", description = "Permite al administrador ver todos los tickets de soporte")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Todos los tickets recuperados con éxito")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
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
    @PreAuthorize("hasRole('ADMIN')")
    @io.swagger.v3.oas.annotations.Operation(summary = "Resolver ticket", description = "Permite al administrador resolver un ticket de soporte")       
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Ticket resuelto exitosamente")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Solicitud inválida")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
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
