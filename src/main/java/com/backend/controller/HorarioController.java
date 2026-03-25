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
import com.backend.security.dto.ApiResponseDto;
import com.backend.service.IHorarioService;

@RestController
@RequestMapping("/api/v1/horarios")
@RequiredArgsConstructor
@io.swagger.v3.oas.annotations.tags.Tag(name = "Horario", description = "Operaciones relacionadas con los horarios")
public class HorarioController {

    private final IHorarioService horarioService;

    @GetMapping
    @io.swagger.v3.oas.annotations.Operation(summary = "Listar todos los horarios", description = "Obtiene una lista de todos los horarios disponibles")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Listado de horarios recuperado con éxito")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<List<HorarioResponseDto>> listarTodos() {
        return ResponseEntity.ok(horarioService.listarTodos());
    }

    @GetMapping("/dia/{dia}")
    @io.swagger.v3.oas.annotations.Operation(summary = "Listar horarios por día", description = "Obtiene una lista de horarios disponibles para un día específico")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Listado de horarios recuperado con éxito")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "No se encontraron horarios para el día especificado")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<List<HorarioResponseDto>> listarPorDia(@PathVariable String dia) {
        return ResponseEntity.ok(horarioService.listarPorDia(dia));
    }

    @GetMapping("/{id}")
    @io.swagger.v3.oas.annotations.Operation(summary = "Obtener horario por ID", description = "Obtiene los detalles de un horario por su ID")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Horario recuperado con éxito")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Horario no encontrado")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<HorarioResponseDto> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(horarioService.obtenerPorId(id));
    }

    //  Solo el ADMIN puede crear horarios
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @io.swagger.v3.oas.annotations.Operation(summary = "Crear horario", description = "Crea un nuevo horario")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Horario creado con éxito")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Solicitud inválida")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<HorarioResponseDto> crear(@Valid @RequestBody HorarioRequestDto dto) {
        return new ResponseEntity<>(horarioService.crear(dto), HttpStatus.CREATED);
    }

    // Solo el ADMIN puede actualizar horarios
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @io.swagger.v3.oas.annotations.Operation(summary = "Actualizar horario", description = "Actualiza los detalles de un horario existente")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Horario actualizado con éxito")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Solicitud inválida")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Horario no encontrado")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<HorarioResponseDto> actualizar(
            @PathVariable Long id, 
            @Valid @RequestBody HorarioRequestDto dto) {
        return ResponseEntity.ok(horarioService.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @io.swagger.v3.oas.annotations.Operation(summary = "Eliminar horario", description = "Elimina un horario existente por su ID")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Horario eliminado con éxito")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Horario no encontrado")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<ApiResponseDto<Void>> eliminar(@PathVariable Long id) {
        horarioService.eliminar(id);

        ApiResponseDto<Void> respuesta = ApiResponseDto.<Void>builder()
            .mensaje("El horario ha sido eliminado correctamente")
            .success(true)
            .datos(null)
            .build();

        return ResponseEntity.ok(respuesta);
    }
}