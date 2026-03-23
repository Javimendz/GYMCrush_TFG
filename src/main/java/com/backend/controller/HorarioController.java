package com.backend.controller;

import java.util.List;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.backend.dto.HorarioRequestDto; // Nuevo DTO de entrada
import com.backend.dto.HorarioResponseDto;
import com.backend.service.IHorarioService;

@RestController
@RequestMapping("/api/v1/horarios")
@RequiredArgsConstructor
@CrossOrigin(origins = "${app.cors.origins}")
public class HorarioController {

    private final IHorarioService horarioService;

    @GetMapping
    public ResponseEntity<List<HorarioResponseDto>> listarTodos() {
        return ResponseEntity.ok(horarioService.listarTodos());
    }

    @GetMapping("/dia/{dia}")
    public ResponseEntity<List<HorarioResponseDto>> listarPorDia(@PathVariable String dia) {
        return ResponseEntity.ok(horarioService.listarPorDia(dia));
    }

    @GetMapping("/{id}")
    public ResponseEntity<HorarioResponseDto> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(horarioService.obtenerPorId(id));
    }

    //  Solo el ADMIN puede crear horarios
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<HorarioResponseDto> crear(@Valid @RequestBody HorarioRequestDto dto) {
        return new ResponseEntity<>(horarioService.crear(dto), HttpStatus.CREATED);
    }

    // Solo el ADMIN puede actualizar horarios
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<HorarioResponseDto> actualizar(
            @PathVariable Long id, 
            @Valid @RequestBody HorarioRequestDto dto) {
        return ResponseEntity.ok(horarioService.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        horarioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}