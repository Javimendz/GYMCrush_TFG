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
@io.swagger.v3.oas.annotations.tags.Tag(name = "Actividad", description = "Operaciones relacionadas con las actividades")
// Permite que Android se conecte sin bloqueos de CORS
public class ActividadController {

    private final IActividadService actividadService;

    // Obtener todas las actividades
    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @io.swagger.v3.oas.annotations.Operation(summary = "Listar todas las actividades", description = "Obtiene una lista de todas las actividades disponibles")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lista de actividades recuperada con éxito")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Acceso denegado. El usuario no tiene permisos para ver las actividades")
    public ResponseEntity<List<ActividadResponseDto>> listarTodas() {
        //El Service busca en la BD y devuelve una lista de ActividadResponseDto
        return ResponseEntity.ok(actividadService.listarTodos());
    }

    // Obtener detalle de una actividad
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @io.swagger.v3.oas.annotations.Operation(summary = "Obtener actividad por ID", description = "Obtiene los detalles de una actividad específica por su ID")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Actividad recuperada con éxito")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "No se encontró actividad con el ID especificado")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Acceso denegado. El usuario no tiene permisos para ver la actividad") 
    public ResponseEntity<ActividadResponseDto> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(actividadService.buscarPorId(id));
    }

    // Crear nueva actividad, Admin
   @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @io.swagger.v3.oas.annotations.Operation(summary = "Crear actividad", description = "Crea una nueva actividad")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Actividad creada con éxito")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Solicitud inválida. Verifique los datos proporcionados")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Acceso denegado. El usuario no tiene permisos para crear una actividad") 
    public ResponseEntity<ActividadResponseDto> crear(@Valid @RequestBody ActividadRequestDto dto) {
        return new ResponseEntity<>(actividadService.crear(dto), HttpStatus.CREATED);
    }
    //  Actualizar actividad, admin
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @io.swagger.v3.oas.annotations.Operation(summary = "Actualizar actividad", description = "Actualiza los detalles de una actividad existente por su ID")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Actividad actualizada con éxito")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Solicitud inválida. Verifique los datos proporcionados")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "No se encontró actividad con el ID especificado")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Acceso denegado. El usuario no tiene permisos para actualizar la actividad") 
    public ResponseEntity<ActividadResponseDto> actualizar(
            @PathVariable Long id, 
            @Valid @RequestBody ActividadRequestDto dto) {
        return ResponseEntity.ok(actividadService.actualizar(id, dto));
    }

    //  Eliminar actividad, Admin
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @io.swagger.v3.oas.annotations.Operation(summary = "Eliminar actividad", description = "Elimina una actividad existente por su ID")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Actividad eliminada con éxito")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "No se encontró actividad con el ID especificado")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Acceso denegado. El usuario no tiene permisos para eliminar la actividad") 
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        actividadService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    
    //BUSQUEDAS
    // Buscar por nombre: /api/v1/actividades/buscar?nombre=zumba
    @GetMapping("/buscar")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @io.swagger.v3.oas.annotations.Operation(summary = "Buscar actividad por nombre", description = "Busca actividades por su nombre")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Actividades recuperadas con éxito")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "No se encontraron actividades con el nombre especificado")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Acceso denegado. El usuario no tiene permisos para buscar actividades por nombre") 
    public ResponseEntity<List<ActividadResponseDto>> buscarPorNombre(@RequestParam String nombre) {
        return ResponseEntity.ok(actividadService.buscarPorNombre(nombre));
    }

    // Filtrar por día: /api/v1/actividades/dia/LUNES
    @GetMapping("/dia/{dia}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @io.swagger.v3.oas.annotations.Operation(summary = "Listar actividades por día", description = "Lista todas las actividades disponibles para un día específico")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Actividades recuperadas con éxito")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "No se encontraron actividades para el día especificado")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Acceso denegado. El usuario no tiene permisos para listar actividades por día") 
    public ResponseEntity<List<ActividadResponseDto>> listarPorDia(@PathVariable String dia) {
        return ResponseEntity.ok(actividadService.listarPorDia(dia));
    }

    // Filtrar por precio: /api/v1/actividades/precio-max?precio=15
    @GetMapping("/precio-max")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @io.swagger.v3.oas.annotations.Operation(summary = "Filtrar actividades por precio máximo", description = "Filtra todas las actividades disponibles por su precio máximo")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Actividades recuperadas con éxito")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "No se encontraron actividades con el precio máximo especificado")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Acceso denegado. El usuario no tiene permisos para filtrar actividades por precio máximo") 
    public ResponseEntity<List<ActividadResponseDto>> filtrarPorPrecio(@RequestParam Integer precio) {
        return ResponseEntity.ok(actividadService.filtrarPorPrecioMaximo(precio));
    }
}