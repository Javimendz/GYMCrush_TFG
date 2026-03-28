package com.backend.controller;

import com.backend.dto.SalaRequestDto;
import com.backend.dto.SalaResponseDto;
import com.backend.service.ISalaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de salas del gimnasio.
 * * @author Backend Team
 * @version 1.0
 */
@RestController
@RequestMapping("/api/v1/salas")
@RequiredArgsConstructor
@Tag(name = "Salas", description = "Endpoints para la gestión de espacios físicos e instalaciones")
public class SalaController {

    private final ISalaService salaService;

    @GetMapping
    @Operation(summary = "Listar todas las salas", description = "Retorna el listado completo incluyendo inactivas")
    @ApiResponse(responseCode = "200", description = "Operación exitosa")
    @ApiResponse(responseCode = "400", description = "Petición inválida")
    @ApiResponse(responseCode = "500", description = "Error del servidor")
    public ResponseEntity<List<SalaResponseDto>> listarTodas() {
        return ResponseEntity.ok(salaService.listarTodas());
    }

    @GetMapping("/activas")
    @Operation(summary = "Listar salas operativas", description = "Retorna solo las salas disponibles para reservar clases")
    @ApiResponse(responseCode = "200", description = "Operación exitosa")
    @ApiResponse(responseCode = "400", description = "Petición inválida")
    @ApiResponse(responseCode = "500", description = "Error del servidor")
    public ResponseEntity<List<SalaResponseDto>> listarActivas() {
        return ResponseEntity.ok(salaService.listarActivas());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener detalle de una sala")
    @ApiResponse(responseCode = "200", description = "Operación exitosa")
    @ApiResponse(responseCode = "400", description = "Petición inválida")
    @ApiResponse(responseCode = "500", description = "Error del servidor")
    public ResponseEntity<SalaResponseDto> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(salaService.obtenerPorId(id));
    }

    @PostMapping
    @Operation(summary = "Registrar una nueva sala", description = "El nombre de la sala debe ser único")
    @ApiResponse(responseCode = "201", description = "Operación exitosa")
    @ApiResponse(responseCode = "400", description = "Petición inválida")
    @ApiResponse(responseCode = "500", description = "Error del servidor")
    public ResponseEntity<SalaResponseDto> crear(@Valid @RequestBody SalaRequestDto dto) {
        return new ResponseEntity<>(salaService.crear(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar datos de una sala")
    @ApiResponse(responseCode = "200", description = "Operación exitosa")
    @ApiResponse(responseCode = "400", description = "Petición inválida")
    @ApiResponse(responseCode = "500", description = "Error del servidor")
    public ResponseEntity<SalaResponseDto> actualizar(@PathVariable Long id, @Valid @RequestBody SalaRequestDto dto) {
        return ResponseEntity.ok(salaService.actualizar(id, dto));
    }

    @PatchMapping("/{id}/estado")
    @Operation(summary = "Cambiar estado de la sala", description = "Activa o desactiva la sala (ej: por mantenimiento)")
    @ApiResponse(responseCode = "200", description = "Operación exitosa")
    @ApiResponse(responseCode = "400", description = "Petición inválida")
    @ApiResponse(responseCode = "500", description = "Error del servidor")
    public ResponseEntity<Void> cambiarEstado(@PathVariable Long id, @RequestParam Boolean activa) {
        salaService.cambiarEstado(id, activa);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar una sala físicamente", description = "Solo permitido si la sala no tiene horarios asignados")
    @ApiResponse(responseCode = "204", description = "Operación exitosa")
    @ApiResponse(responseCode = "400", description = "Petición inválida")
    @ApiResponse(responseCode = "500", description = "Error del servidor")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        salaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}