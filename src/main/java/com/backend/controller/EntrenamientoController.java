package com.backend.controller;

import com.backend.dto.EntrenamientoResponseDto;
import com.backend.service.IEntrenamientoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/entrenamiento") // Unificado con la ruta de usuarios
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class EntrenamientoController {

    private final IEntrenamientoService entrenamientoService;

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')") // Solo el admin ve todo el histórico global
    public ResponseEntity<List<EntrenamientoResponseDto>> listarTodos() {
        return ResponseEntity.ok(entrenamientoService.listarTodos());
    }

    @PostMapping("/guardar")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USUARIO')") // Ambos pueden registrar actividad
    public ResponseEntity<EntrenamientoResponseDto> guardar(@RequestBody EntrenamientoResponseDto dto) {
        return new ResponseEntity<>(entrenamientoService.guardar(dto), HttpStatus.CREATED);
    }
}