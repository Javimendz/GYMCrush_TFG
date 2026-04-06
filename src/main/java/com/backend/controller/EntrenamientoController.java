package com.backend.controller;

import com.backend.dto.EntrenamientoRequestDto;
import com.backend.dto.EntrenamientoResponseDto;
import com.backend.service.IEntrenamientoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/entrenamientos")
@RequiredArgsConstructor
@Tag(name = "Entrenamientos", description = "Gestión de sesiones de ejercicio programadas")
public class EntrenamientoController {

    private final IEntrenamientoService entrenamientoService;

    @PostMapping
@PreAuthorize("hasAnyRole('ADMIN', 'USUARIO')")
    @Operation(summary = "Crear una nueva sesión de entrenamiento")
    public ResponseEntity<EntrenamientoResponseDto> crear(@Valid @RequestBody EntrenamientoRequestDto dto) {
        return new ResponseEntity<>(entrenamientoService.crear(dto), HttpStatus.CREATED);
    }

    @GetMapping("/usuario/{usuarioId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USUARIO')")
    @Operation(summary = "Listar todos los entrenamientos de un usuario específico")
    public ResponseEntity<List<EntrenamientoResponseDto>> listarPorUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(entrenamientoService.listarPorUsuario(usuarioId));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USUARIO')")
    @Operation(summary = "Obtener detalle de una sesión")
    public ResponseEntity<EntrenamientoResponseDto> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(entrenamientoService.obtenerPorId(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USUARIO')")
    @Operation(summary = "Actualizar datos de la sesión")
    public ResponseEntity<EntrenamientoResponseDto> actualizar(@PathVariable Long id, @Valid @RequestBody EntrenamientoRequestDto dto) {
        return ResponseEntity.ok(entrenamientoService.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
@PreAuthorize("hasAnyRole('ADMIN', 'USUARIO')")
    @Operation(summary = "Eliminar una sesión")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        entrenamientoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}