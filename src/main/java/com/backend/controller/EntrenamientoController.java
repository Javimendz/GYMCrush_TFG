package com.backend.controller;

import com.backend.dto.EntrenamientoRequestDto;
import com.backend.dto.EntrenamientoResponseDto;
import com.backend.security.dto.ApiResponseDto;
import com.backend.service.IEntrenamientoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/entrenamientos")
@RequiredArgsConstructor
@Slf4j //  Habilitamos los logs
@Tag(name = "Entrenamientos", description = "Gestión de sesiones de ejercicio programadas")
public class EntrenamientoController {

    private final IEntrenamientoService entrenamientoService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USUARIO')")
    @Operation(summary = "Crear una nueva sesión de entrenamiento")
    public ResponseEntity<ApiResponseDto<EntrenamientoResponseDto>> crear(@Valid @RequestBody EntrenamientoRequestDto dto) {
        log.info("Solicitud para crear nueva sesión de entrenamiento");
        EntrenamientoResponseDto nuevoEntrenamiento = entrenamientoService.crear(dto);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponseDto.<EntrenamientoResponseDto>builder()
                        .mensaje("Sesión de entrenamiento creada con éxito")
                        .success(true)
                        .datos(nuevoEntrenamiento)
                        .build()
        );
    }

    @GetMapping("/usuario/{usuarioId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USUARIO')")
    @Operation(summary = "Listar todos los entrenamientos de un usuario específico")
    public ResponseEntity<ApiResponseDto<List<EntrenamientoResponseDto>>> listarPorUsuario(
            @PathVariable("usuarioId") Long usuarioId) { 
            
        log.info("Solicitud para listar entrenamientos del usuario con ID: {}", usuarioId);
        List<EntrenamientoResponseDto> entrenamientos = entrenamientoService.listarPorUsuario(usuarioId);
        
        return ResponseEntity.ok(
                ApiResponseDto.<List<EntrenamientoResponseDto>>builder()
                        .mensaje("Entrenamientos recuperados con éxito")
                        .success(true)
                        .datos(entrenamientos)
                        .build()
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USUARIO')")
    @Operation(summary = "Obtener detalle de una sesión")
    public ResponseEntity<ApiResponseDto<EntrenamientoResponseDto>> obtenerPorId(
            @PathVariable("id") Long id) { 
            
        log.info("Solicitud para obtener entrenamiento con ID: {}", id);
        EntrenamientoResponseDto entrenamiento = entrenamientoService.obtenerPorId(id);
        
        return ResponseEntity.ok(
                ApiResponseDto.<EntrenamientoResponseDto>builder()
                        .mensaje("Detalle del entrenamiento recuperado")
                        .success(true)
                        .datos(entrenamiento)
                        .build()
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USUARIO')")
    @Operation(summary = "Actualizar datos de la sesión")
    public ResponseEntity<ApiResponseDto<EntrenamientoResponseDto>> actualizar(
            @PathVariable("id") Long id, 
            @Valid @RequestBody EntrenamientoRequestDto dto) {
            
        log.info("Solicitud para actualizar entrenamiento con ID: {}", id);
        EntrenamientoResponseDto entrenamientoActualizado = entrenamientoService.actualizar(id, dto);
        
        return ResponseEntity.ok(
                ApiResponseDto.<EntrenamientoResponseDto>builder()
                        .mensaje("Entrenamiento actualizado con éxito")
                        .success(true)
                        .datos(entrenamientoActualizado)
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USUARIO')")
    @Operation(summary = "Eliminar una sesión")
    public ResponseEntity<ApiResponseDto<Void>> eliminar(
            @PathVariable("id") Long id) { 
            
        log.info("Solicitud para eliminar entrenamiento con ID: {}", id);
        entrenamientoService.eliminar(id);
        
        return ResponseEntity.ok(
                ApiResponseDto.<Void>builder()
                        .mensaje("Sesión de entrenamiento eliminada correctamente")
                        .success(true)
                        .build()
        );
    }
}