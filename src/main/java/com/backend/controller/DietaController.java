package com.backend.controller;

import com.backend.dto.DietaRequestDto;
import com.backend.dto.DietaResponseDto;
import com.backend.service.IDietaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dietas")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DietaController {

    private final IDietaService dietaService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')") // Solo el admin gestiona el catálogo
    public ResponseEntity<DietaResponseDto> crear(@RequestBody DietaRequestDto dto) {
        return new ResponseEntity<>(dietaService.crearDieta(dto), HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')") // Todos pueden ver las opciones
    public ResponseEntity<List<DietaResponseDto>> listar() {
        return ResponseEntity.ok(dietaService.listarTodas());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        dietaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
