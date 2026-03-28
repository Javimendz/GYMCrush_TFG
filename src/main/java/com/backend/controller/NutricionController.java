package com.backend.controller;

import com.backend.dto.PerfilFisicoRequest;
import com.backend.dto.SugerenciaDietaReponseDto;
import com.backend.service.INutricionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/nutricion")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
public class NutricionController {

    private final INutricionService nutricionService;

    @PostMapping("/generar-plan")
    public ResponseEntity<SugerenciaDietaReponseDto> generarPlan(@RequestBody PerfilFisicoRequest request) {
        // Este método ejecuta Harris-Benedict y asigna la dieta automáticamente
        SugerenciaDietaReponseDto plan = nutricionService.generarPlanAutomatico(request);
        return ResponseEntity.ok(plan);
    }
}
