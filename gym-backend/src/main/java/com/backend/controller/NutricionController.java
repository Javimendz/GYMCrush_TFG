package com.backend.controller;

import com.backend.dto.NutricionResponseDto;
import com.backend.dto.PerfilFisicoRequest;
import com.backend.security.dto.ApiResponseDto;
import com.backend.service.INutricionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.backend.dto.NutricionRequestDto;
import java.util.List;
import com.backend.service.FatSecretService;

@RestController
@RequestMapping("/api/v1/nutricion")
@RequiredArgsConstructor
@Slf4j
@io.swagger.v3.oas.annotations.tags.Tag(name = "Nutrición", description = "Motor de generación de planes nutricionales automáticos")
public class NutricionController {

        private final INutricionService nutricionService;
        private final FatSecretService fatSecretService;

        // -----------------------------------------------------------------------
        // POST /api/v1/nutricion/generar-plan
        // Genera un plan nutricional personalizado basado en los datos físicos
        // -----------------------------------------------------------------------
        @PostMapping("/generar-plan")
        @PreAuthorize("hasRole('ADMIN') or hasRole('USUARIO')")
        @io.swagger.v3.oas.annotations.Operation(summary = "Generar plan automático", description = "Calcula y genera un plan nutricional usando la fórmula de Mifflin-St Jeor")
        public ResponseEntity<ApiResponseDto<NutricionResponseDto>> generarPlan(
                        @Valid @RequestBody NutricionRequestDto request) {

                log.info("Recibida petición para generar plan. Usuario ID: {}, Objetivo: {}",
                                request.getUsuarioId(), request.getObjetivo());

                NutricionResponseDto plan = nutricionService.generarPlanAutomatico(request);

                return ResponseEntity.status(HttpStatus.CREATED).body(
                                ApiResponseDto.<NutricionResponseDto>builder()
                                                .mensaje("Plan nutricional personalizado generado con éxito")
                                                .success(true)
                                                .datos(plan)
                                                .build());
        }

        // -----------------------------------------------------------------------
        // GET /api/v1/nutricion/ultimo-plan/{usuarioId}
        // Recupera el último plan generado para un usuario
        // -----------------------------------------------------------------------
        @GetMapping("/ultimo-plan/{usuarioId}")
        @PreAuthorize("hasAnyRole('USUARIO', 'ADMIN')")
        @io.swagger.v3.oas.annotations.Operation(summary = "Obtener último plan", description = "Recupera el plan nutricional más reciente activo del usuario")
        public ResponseEntity<ApiResponseDto<NutricionResponseDto>> obtenerUltimoPlan(
                        @PathVariable("usuarioId") Long usuarioId) {

                log.info("Consultando último plan nutricional del usuario ID: {}", usuarioId);
                NutricionResponseDto response = nutricionService.obtenerUltimoPlanPorUsuario(usuarioId);

                return ResponseEntity.ok(
                                ApiResponseDto.<NutricionResponseDto>builder()
                                                .mensaje("Plan recuperado correctamente")
                                                .success(true)
                                                .datos(response)
                                                .build());
        }

        // -----------------------------------------------------------------------
        // GET /api/v1/nutricion/historial/{usuarioId}
        // Recupera el historial completo de planes de un usuario
        // -----------------------------------------------------------------------
        @GetMapping("/historial/{usuarioId}")
        @PreAuthorize("hasAnyRole('USUARIO', 'ADMIN')")
        @io.swagger.v3.oas.annotations.Operation(summary = "Obtener historial de planes", description = "Recupera todos los planes generados históricamente para el usuario")
        public ResponseEntity<ApiResponseDto<List<NutricionResponseDto>>> obtenerHistorial(
                        @PathVariable("usuarioId") Long usuarioId) {

                log.info("Consultando historial nutricional del usuario ID: {}", usuarioId);

                List<NutricionResponseDto> historial = nutricionService.obtenerHistorial(usuarioId);

                return ResponseEntity.ok(
                                ApiResponseDto.<List<NutricionResponseDto>>builder()
                                                .mensaje("Historial de planes recuperado correctamente")
                                                .success(true)
                                                .datos(historial)
                                                .build());
        }

        @GetMapping("/buscar")
        @PreAuthorize("hasAnyRole('USUARIO', 'ADMIN')")
        @io.swagger.v3.oas.annotations.Operation(summary = "Buscar alimentos", description = "Busca recetas o alimentos por nombre usando la API de FatSecret")
        public ResponseEntity<ApiResponseDto<String>> buscarAlimentos(@RequestParam("query") String query) {

                log.info("Buscando alimentos con el término: {}", query);

                // Llamamos al servicio de FatSecret
                String resultado = fatSecretService.buscarAlimentosPorNombre(query);

                return ResponseEntity.ok(
                                ApiResponseDto.<String>builder()
                                                .mensaje("Resultados de búsqueda obtenidos")
                                                .success(true)
                                                .datos(resultado)
                                                .build());
        }
}
