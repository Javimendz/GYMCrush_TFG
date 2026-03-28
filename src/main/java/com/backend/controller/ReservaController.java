package com.backend.controller;

import com.backend.dto.ReservaResponseDto;
import com.backend.security.dto.ApiResponseDto;
import com.backend.service.IReservaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para gestionar las reservas de horarios de actividades.
 * <p>
 * Este controlador proporciona endpoints para:
 * <ul>
 *   <li>Crear reservas de horarios para usuarios</li>
 *   <li>Listar las reservas de un usuario específico</li>
 *   <li>Cancelar reservas existentes</li>
 * </ul>
 * </p>
 *
 * @author Backend Team
 * @version 1.0
 * @since 2026
 */
@RestController
@RequestMapping("/api/v1/reservas")
@RequiredArgsConstructor
@Slf4j
@io.swagger.v3.oas.annotations.tags.Tag(name = "Reserva", description = "Operaciones relacionadas con las reservas de horarios")
public class ReservaController {

    private final IReservaService reservaService;

    /**
     * Crea una nueva reserva para un usuario en un horario específico.
     * <p>
     * Cualquier usuario autenticado (USER o ADMIN) puede realizar reservas.
     * </p>
     *
     * @param uId el ID del usuario que realiza la reserva
     * @param hId el ID del horario a reservar
     * @return ResponseEntity con la reserva creada y mensaje de confirmación
     */
    @PostMapping("/usuario/{uId}/horario/{hId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @io.swagger.v3.oas.annotations.Operation(summary = "Reservar horario", description = "Permite a un usuario reservar un horario disponible")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Reserva confirmada con éxito")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Solicitud inválida, posiblemente el horario ya está reservado")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<ApiResponseDto<ReservaResponseDto>> reservar(
            @PathVariable Long uId, 
            @PathVariable Long hId) {
        log.info("Creando reserva para usuario {} en horario {}", uId, hId);
        ReservaResponseDto nuevaReserva = reservaService.crearReserva(uId, hId);
        log.info("Reserva creada exitosamente con ID: {}", nuevaReserva.getId());

        return ResponseEntity.ok(ApiResponseDto.<ReservaResponseDto>builder()
                .mensaje("¡Reserva confirmada! Te esperamos en clase.")
                .success(true)
                .datos(nuevaReserva)
                .build());
    }

    /**
     * Obtiene el historial de reservas de un usuario específico.
     *
     * @param id el ID del usuario cuyas reservas se quieren consultar
     * @return ResponseEntity con la lista de reservas del usuario
     */
    @GetMapping("/usuario/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @io.swagger.v3.oas.annotations.Operation(summary = "Listar mis reservas", description = "Recupera el historial de reservas de un usuario")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lista de reservas recuperada con éxito")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<ApiResponseDto<List<ReservaResponseDto>>> listarMisReservas(@PathVariable Long id) {
        log.info("Listando reservas para usuario ID: {}", id);
        List<ReservaResponseDto> misReservas = reservaService.obtenerReservasUsuario(id);
        log.debug("Total de reservas encontradas para usuario {}: {}", id, misReservas.size());

        return ResponseEntity.ok(ApiResponseDto.<List<ReservaResponseDto>>builder()
                .mensaje("Listado de reservas recuperado")
                .success(true)
                .datos(misReservas)
                .build());
    }

    /**
     * Cancela una reserva existente liberando el hueco en la clase.
     *
     * @param id el ID de la reserva a cancelar
     * @return ResponseEntity con confirmación de cancelación
     */
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