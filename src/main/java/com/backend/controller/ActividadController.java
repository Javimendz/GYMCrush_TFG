package com.backend.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.backend.dto.ActividadRequestDto;
import com.backend.dto.ActividadResponseDto;
import com.backend.service.IActividadService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/actividades")
@RequiredArgsConstructor
// Permite que Android se conecte sin bloqueos de CORS
public class ActividadController {

    private final IActividadService actividadService;

    // Obtener todas las actividades
    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<ActividadResponseDto>> listarTodas() {
        //El Service busca en la BD y devuelve una lista de ActividadResponseDto
        return ResponseEntity.ok(actividadService.listarTodos());
    }

    // Obtener detalle de una actividad
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ActividadResponseDto> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(actividadService.buscarPorId(id));
    }

    // Crear nueva actividad, Admin
   @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ActividadResponseDto> crear(@Valid @RequestBody ActividadRequestDto dto) {
        return new ResponseEntity<>(actividadService.crear(dto), HttpStatus.CREATED);
    }
    //  Actualizar actividad, admin
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ActividadResponseDto> actualizar(
            @PathVariable Long id, 
            @Valid @RequestBody ActividadRequestDto dto) {
        return ResponseEntity.ok(actividadService.actualizar(id, dto));
    }

    //  Eliminar actividad, Admin
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        actividadService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    
    //BUSQUEDAS
    // Buscar por nombre: /api/v1/actividades/buscar?nombre=zumba
    @GetMapping("/buscar")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<ActividadResponseDto>> buscarPorNombre(@RequestParam String nombre) {
        return ResponseEntity.ok(actividadService.buscarPorNombre(nombre));
    }

    // Filtrar por día: /api/v1/actividades/dia/LUNES
    @GetMapping("/dia/{dia}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<ActividadResponseDto>> listarPorDia(@PathVariable String dia) {
        return ResponseEntity.ok(actividadService.listarPorDia(dia));
    }

    // Filtrar por precio: /api/v1/actividades/precio-max?precio=15
    @GetMapping("/precio-max")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<ActividadResponseDto>> filtrarPorPrecio(@RequestParam Integer precio) {
        return ResponseEntity.ok(actividadService.filtrarPorPrecioMaximo(precio));
    }
}
