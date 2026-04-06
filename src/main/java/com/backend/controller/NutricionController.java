package com.backend.controller;

import com.backend.dto.NutricionResponseDto;
import com.backend.dto.PerfilFisicoRequest;
import com.backend.dto.SugerenciaDietaReponseDto;
import com.backend.security.dto.ApiResponseDto;
import com.backend.service.INutricionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/nutricion")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ROLE_USUARIO', 'ROLE_ADMIN')") // ← prefijo ROLE_ correcto
public class NutricionController {

    private final INutricionService nutricionService;
 
    // -----------------------------------------------------------------------
    // POST /api/v1/nutricion/generar-plan
    // Genera un plan nutricional personalizado basado en los datos físicos
    // -----------------------------------------------------------------------
    @PostMapping("/generar-plan")
    public ResponseEntity<ApiResponseDto<SugerenciaDietaReponseDto>> generarPlan(
            @RequestBody PerfilFisicoRequest request) {
        SugerenciaDietaReponseDto plan = nutricionService.generarPlanAutomatico(request);
        return ResponseEntity.ok(
                ApiResponseDto.success("Plan nutricional personalizado generado con éxito", plan));
    }

    // -----------------------------------------------------------------------
    // GET /api/v1/nutricion/ultimo-plan/{usuarioId}
    // Recupera el último plan generado para un usuario
    // -----------------------------------------------------------------------
    @GetMapping("/ultimo-plan/{usuarioId}")
    public ResponseEntity<ApiResponseDto<NutricionResponseDto>> obtenerUltimoPlan(
            @PathVariable Long usuarioId) {
        NutricionResponseDto response = nutricionService.obtenerUltimoPlanPorUsuario(usuarioId);
        return ResponseEntity.ok(
                ApiResponseDto.success("Plan recuperado correctamente", response));
    }

    // -----------------------------------------------------------------------
    // GET /api/v1/nutricion/historial/{usuarioId}
    // Recupera el historial completo de planes de un usuario
    // -----------------------------------------------------------------------
    @GetMapping("/historial/{usuarioId}")
    public ResponseEntity<ApiResponseDto<List<SugerenciaDietaReponseDto>>> obtenerHistorial(
            @PathVariable Long usuarioId) {
        List<SugerenciaDietaReponseDto> historial = nutricionService.obtenerHistorial(usuarioId);
        return ResponseEntity.ok(
                ApiResponseDto.success("Historial de planes recuperado correctamente", historial));
    }
}
