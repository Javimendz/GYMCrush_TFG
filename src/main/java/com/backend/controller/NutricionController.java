package com.backend.controller;

import com.backend.dto.NutricionResponseDto;
import com.backend.dto.PerfilFisicoRequest;
import com.backend.dto.SugerenciaDietaReponseDto;
import com.backend.security.dto.ApiResponseDto; // Importante: tu wrapper de respuesta
import com.backend.service.INutricionService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/nutricion")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
public class NutricionController {

    private final INutricionService nutricionService;

    @PostMapping("/generar-plan")
    public ResponseEntity<ApiResponseDto<SugerenciaDietaReponseDto>> generarPlan(@RequestBody PerfilFisicoRequest request) {
        // Ejecuta el motor de cálculo (Mifflin-St Jeor) y persiste la sugerencia
        SugerenciaDietaReponseDto plan = nutricionService.generarPlanAutomatico(request);
        
        // Devolvemos la respuesta unificada
        return ResponseEntity.ok(
            ApiResponseDto.success("¡Tu plan nutricional personalizado ha sido generado con éxito!", plan)
        );
    }

    /**
     * GET /api/nutricion/usuario/{id}
     * Recupera el último plan generado para un usuario específico.
     */
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<NutricionResponseDto> obtenerUltimoPlan(@PathVariable Long usuarioId) {
        NutricionResponseDto response = nutricionService.obtenerUltimoPlanPorUsuario(usuarioId);
        
        if (response == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        
        return ResponseEntity.ok(response);
    }
}