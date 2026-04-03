package com.backend.controller;

import com.backend.dto.SugerenciaDietaReponseDto;
import com.backend.dto.SugerenciaDietaRequestDto;
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
    public ResponseEntity<List<SugerenciaDietaReponseDto>> obtenerHistorial(@PathVariable Long usuarioId) {
        List<SugerenciaDietaReponseDto> historial = sugerenciaService.obtenerHistorial(usuarioId);
        return ResponseEntity.ok(historial);
    }
    @PatchMapping("/{id}/favorito")
    public ResponseEntity<SugerenciaDietaReponseDto> marcarFavorita(@PathVariable Long id) {
        return ResponseEntity.ok(sugerenciaService.marcarComoFavorita(id));
    }
    // Nuevo método para obtener solo las que el usuario marcó con el corazón
    @GetMapping("/usuario/{usuarioId}/favoritas")
    public ResponseEntity<List<SugerenciaDietaReponseDto>> obtenerFavoritas(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(sugerenciaService.obtenerFavoritas(usuarioId));
    }
    @PostMapping
    public ResponseEntity<SugerenciaDietaReponseDto> crearSugerencia(@RequestBody SugerenciaDietaRequestDto dto) {
        return ResponseEntity.ok(sugerenciaService.guardarAsignacion(dto));
    }
}