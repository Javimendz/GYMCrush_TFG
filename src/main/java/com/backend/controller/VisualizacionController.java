package com.backend.controller;

import com.backend.dto.TutorialResponseDto;
import com.backend.dto.VisualizacionRequestDto;
import com.backend.dto.VisualizacionResponseDto;
import com.backend.service.IVisualizacionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/visualizaciones")
@RequiredArgsConstructor
@Tag(name = "Visualizaciones", description = "Seguimiento de progreso en videotutoriales")
public class VisualizacionController {

    private final IVisualizacionService visualizacionService;

    @PostMapping("/progreso")
    @Operation(summary = "Guardar progreso del video", description = "Crea o actualiza el segundo exacto donde se quedó el usuario")
    public ResponseEntity<VisualizacionResponseDto> guardar(@Valid @RequestBody VisualizacionRequestDto dto) {
        return ResponseEntity.ok(visualizacionService.guardarProgreso(dto));
    }

    @GetMapping("/usuario/{usuarioId}/historial")
    @Operation(summary = "Videos terminados", description = "Lista de tutoriales marcados como completados")
    public ResponseEntity<List<VisualizacionResponseDto>> historial(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(visualizacionService.obtenerHistorial(usuarioId));
    }

    @GetMapping("/usuario/{usuarioId}/pendientes")
    @Operation(summary = "Sección 'Continuar viendo'", description = "Videos empezados pero no terminados")
    public ResponseEntity<List<VisualizacionResponseDto>> pendientes(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(visualizacionService.obtenerContinuarViendo(usuarioId));
    }

    @GetMapping("/populares")
    @Operation(summary = "Top 3 vídeos más populares", description = "Retorna los 3 tutoriales con mayor número de visualizaciones en el gimnasio")
    public ResponseEntity<List<TutorialResponseDto>> obtenerPopulares() {
        return ResponseEntity.ok(visualizacionService.obtenerPopulares());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        visualizacionService.borrarDeHistorial(id);
        return ResponseEntity.noContent().build();
    }
}