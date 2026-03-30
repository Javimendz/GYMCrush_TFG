package com.backend.controller;

import com.backend.dto.ComidaDiariaRequestDto;
import com.backend.dto.ComidaDiariaResponseDto;
import com.backend.service.IComidaDiariaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comidas")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") 
public class ComidaDiariaController {

    private final IComidaDiariaService comidaService;

    /**
     * POST /api/comidas
     * Añade un nuevo plato a un plan existente.
     */
    @PostMapping
    public ResponseEntity<ComidaDiariaResponseDto> añadirComida(@RequestBody ComidaDiariaRequestDto request) {
        ComidaDiariaResponseDto nuevaComida = comidaService.añadirComida(request);
        return new ResponseEntity<>(nuevaComida, HttpStatus.CREATED);
    }

    /**
     * DELETE /api/comidas/{id}
     * Elimina un plato específico de la base de datos.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarComida(@PathVariable Long id) {
        comidaService.eliminarComida(id);
        return ResponseEntity.noContent().build(); // Devuelve 204 No Content
    }

    /**
     * POST /api/comidas/batch/{planId}
     * Útil si quieres guardar una lista de platos de golpe (ej: tras llamar a FatSecret).
     */
    @PostMapping("/batch/{planId}")
    public ResponseEntity<String> guardarMuchasComidas(
            @RequestBody List<ComidaDiariaRequestDto> comidas, 
            @PathVariable Long planId) {
        
        comidaService.guardarComidas(comidas, planId);
        return ResponseEntity.ok("Platos guardados correctamente en el plan " + planId);
    }
}