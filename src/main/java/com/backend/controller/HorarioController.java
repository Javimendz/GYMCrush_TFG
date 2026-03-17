package com.backend.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.backend.dto.HorarioResponseDto;
import com.backend.service.IHorarioService;

@RestController
@RequestMapping("/api/v1/horarios")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // Permite conexión desde Android
public class HorarioController {

    private final IHorarioService horarioService;

    // Obtener todos los horarios
    @GetMapping
    public ResponseEntity<List<HorarioResponseDto>> listarTodos() {
        return ResponseEntity.ok(horarioService.listarTodos());
    }

    // Obtener horarios por día (ej: /api/v1/horarios/dia/LUNES)
    @GetMapping("/dia/{dia}")
    public ResponseEntity<List<HorarioResponseDto>> listarPorDia(@PathVariable String dia) {
        return ResponseEntity.ok(horarioService.listarPorDia(dia));
    }

    // Obtener un horario por ID
    @GetMapping("/{id}")
    public ResponseEntity<HorarioResponseDto> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(horarioService.obtenerPorId(id));
    }

    // Crear un nuevo horario (Asignar actividad a un tiempo)
    @PostMapping
    public ResponseEntity<HorarioResponseDto> crear(@RequestBody HorarioResponseDto dto) {
        return new ResponseEntity<>(horarioService.crear(dto), HttpStatus.CREATED);
    }

    // Actualizar un horario
    @PutMapping("/{id}")
    public ResponseEntity<HorarioResponseDto> actualizar(@PathVariable Long id, @RequestBody HorarioResponseDto dto) {
        return ResponseEntity.ok(horarioService.actualizar(id, dto));
    }

    // Eliminar un horario
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        horarioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}