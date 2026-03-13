package com.backend.controller;

import com.backend.dto.PerfilResponseDto;
import com.backend.service.IPerfilService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/perfil")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PerfilController {

    private final IPerfilService perfilService;

    // Acción: Mostrar perfil al pulsar el botón
    @GetMapping("/usuario/{id}")
    public ResponseEntity<PerfilResponseDto> getPerfil(@PathVariable Long id) {
        return ResponseEntity.ok(perfilService.obtenerPerfilPorUsuario(id));
    }

    // Acción: Guardar cambios 
    @PutMapping("/usuario/{id}")
    public ResponseEntity<PerfilResponseDto> updatePerfil(
            @PathVariable Long id, 
            @RequestBody PerfilResponseDto updateDto) {
        return ResponseEntity.ok(perfilService.actualizarPerfil(id, updateDto));
    }
}