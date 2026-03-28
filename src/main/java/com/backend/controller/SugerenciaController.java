package com.backend.controller;

import com.backend.dto.SugerenciaDietaReponseDto;
import com.backend.service.ISugerenciaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sugerencias")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
public class SugerenciaController {

    private final ISugerenciaService sugerenciaService;

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<SugerenciaDietaReponseDto>> obtenerHistorial(@PathVariable Long usuarioId) {
        List<SugerenciaDietaReponseDto> historial = sugerenciaService.obtenerHistorial(usuarioId);
        return ResponseEntity.ok(historial);
    }
}
