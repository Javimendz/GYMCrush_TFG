package com.backend.controller;

import com.backend.dto.SugerenciaDietaReponseDto;
import com.backend.service.ISugerenciaDietaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sugerencias_dieta")
@RequiredArgsConstructor
public class SugerenciaDietaController {

    private final ISugerenciaDietaService sugerenciaService;

    @GetMapping("/usuario/{usuarioId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")  
    public ResponseEntity<List<SugerenciaDietaReponseDto>> obtenerHistorial(@PathVariable Long usuarioId) {
        List<SugerenciaDietaReponseDto> historial = sugerenciaService.obtenerHistorial(usuarioId);
        return ResponseEntity.ok(historial);
    }
}