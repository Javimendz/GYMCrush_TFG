package com.backend.controller;

import com.backend.dto.ReservaResponseDto;
import com.backend.security.dto.ApiResponseDto;
import com.backend.service.IReservaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reservas")
@RequiredArgsConstructor
@io.swagger.v3.oas.annotations.tags.Tag(name = "Reserva", description = "Operaciones relacionadas con las reservas de horarios")
public class ReservaController {

    private final IReservaService reservaService;

    // RESERVAR: Cualquier usuario puede apuntarse a un horario
    @PostMapping("/usuario/{uId}/horario/{hId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @io.swagger.v3.oas.annotations.Operation(summary = "Reservar horario", description = "Permite a un usuario reservar un horario disponible")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Reserva confirmada con éxito")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Solicitud inválida, posiblemente el horario ya está reservado")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<ApiResponseDto<ReservaResponseDto>> reservar(
            @PathVariable Long uId, 
            @PathVariable Long hId) {
        
        ReservaResponseDto nuevaReserva = reservaService.crearReserva(uId, hId);

        return ResponseEntity.ok(ApiResponseDto.<ReservaResponseDto>builder()
                .mensaje("¡Reserva confirmada! Te esperamos en clase.")
                .success(true)
                .datos(nuevaReserva)
                .build());
    }

    //  MIS RESERVAS: El usuario consulta su historial
    @GetMapping("/usuario/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @io.swagger.v3.oas.annotations.Operation(summary = "Listar mis reservas", description = "Recupera el historial de reservas de un usuario")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lista de reservas recuperada con éxito")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<ApiResponseDto<List<ReservaResponseDto>>> listarMisReservas(@PathVariable Long id) {
        
        List<ReservaResponseDto> misReservas = reservaService.obtenerReservasUsuario(id);

        return ResponseEntity.ok(ApiResponseDto.<List<ReservaResponseDto>>builder()
                .mensaje("Listado de reservas recuperado")
                .success(true)
                .datos(misReservas)
                .build());
    }

    //  CANCELAR: Permite liberar el hueco en la clase
    @PatchMapping("/{id}/cancelar")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @io.swagger.v3.oas.annotations.Operation(summary = "Cancelar reserva", description = "Permite a un usuario cancelar una reserva previamente hecha")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Reserva cancelada con éxito")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Reserva no encontrada")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<ApiResponseDto<Void>> cancelar(@PathVariable Long id) {
        
        reservaService.cancelarReserva(id);

        return ResponseEntity.ok(ApiResponseDto.<Void>builder()
                .mensaje("Reserva cancelada correctamente")
                .success(true)
                .build());
    }
}