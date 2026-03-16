package com.backend.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.backend.dto.ActividadResponseDto;
import com.backend.service.IActividadService;

@RestController
@RequestMapping("/api/v1/actividades")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // Permite que Android se conecte sin bloqueos de CORS
public class ActividadController {

    private final IActividadService actividadService;

    // 1. Obtener todas las actividades (Catálogo principal)
    @GetMapping
    public ResponseEntity<List<ActividadResponseDto>> listarTodas() {
        //El Service busca en la BD y devuelve una lista de ActividadResponseDto
        return ResponseEntity.ok(actividadService.listarTodos());
    }

    // 2. Obtener detalle de una actividad
    @GetMapping("/{id}")
    public ResponseEntity<ActividadResponseDto> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(actividadService.buscarPorId(id));
    }

    // 3. Crear nueva actividad (Admin)
    @PostMapping
    public ResponseEntity<ActividadResponseDto> crear(@RequestBody ActividadResponseDto dto) {
        return new ResponseEntity<>(actividadService.crear(dto), HttpStatus.CREATED);
    }

    // 4. Actualizar actividad (Admin)
    @PutMapping("/{id}")
    public ResponseEntity<ActividadResponseDto> actualizar(@PathVariable Long id, @RequestBody ActividadResponseDto dto) {
        return ResponseEntity.ok(actividadService.actualizar(id, dto));
    }

    // 5. Eliminar actividad (Admin)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        actividadService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // --- ENDPOINTS DE BÚSQUEDA Y FILTRADO ---

    // Buscar por nombre: /api/v1/actividades/buscar?nombre=zumba
    @GetMapping("/buscar")
    public ResponseEntity<List<ActividadResponseDto>> buscarPorNombre(@RequestParam String nombre) {
        return ResponseEntity.ok(actividadService.buscarPorNombre(nombre));
    }

    // Filtrar por día: /api/v1/actividades/dia/LUNES
    @GetMapping("/dia/{dia}")
    public ResponseEntity<List<ActividadResponseDto>> listarPorDia(@PathVariable String dia) {
        return ResponseEntity.ok(actividadService.listarPorDia(dia));
    }

    // Filtrar por precio: /api/v1/actividades/precio-max?precio=15
    @GetMapping("/precio-max")
    public ResponseEntity<List<ActividadResponseDto>> filtrarPorPrecio(@RequestParam Integer precio) {
        return ResponseEntity.ok(actividadService.filtrarPorPrecioMaximo(precio));
    }
}
