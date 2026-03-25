package com.backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.backend.dto.CategoriaTutorialRequestDto;
import com.backend.dto.CategoriaTutorialResponseDto;
import com.backend.security.dto.ApiResponseDto;
import com.backend.service.ICategoriaTutorialService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/categorias-tutorial")
@RequiredArgsConstructor
@io.swagger.v3.oas.annotations.tags.Tag(name = "CategoriaTutorial", description = "Operaciones relacionadas con las categorías de tutoriales")
public class CategoriaTutorialController {

    private final ICategoriaTutorialService categoriaService;

    // LISTAR TODAS LAS CATEGORÍAS
    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @io.swagger.v3.oas.annotations.Operation(summary = "Listar todas las categorías de tutoriales", description = "Recupera una lista de todas las categorías de tutoriales disponibles")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Listado de categorías recuperado con éxito")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Acceso denegado. El usuario no tiene permisos para listar categorías de tutoriales") 
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<ApiResponseDto<List<CategoriaTutorialResponseDto>>> listarTodas() {
        List<CategoriaTutorialResponseDto> data = categoriaService.listarTodas();
        
        return ResponseEntity.ok(ApiResponseDto.<List<CategoriaTutorialResponseDto>>builder()
                .mensaje("Listado de categorías de entrenamiento recuperado")
                .datos(data)
                .success(true)
                .build());
    }

    // OBTENER DETALLE DE UNA CATEGORÍA POR ID
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @io.swagger.v3.oas.annotations.Operation(summary = "Obtener categoría por ID", description = "Recupera los detalles de una categoría de tutorial por su ID")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Detalles de la categoría recuperados con éxito")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Acceso denegado. El usuario no tiene permisos para obtener detalles de categorías de tutoriales") 
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Categoría no encontrada")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<ApiResponseDto<CategoriaTutorialResponseDto>> obtenerPorId(@PathVariable Long id) {
        CategoriaTutorialResponseDto data = categoriaService.buscarPorId(id);
        
        return ResponseEntity.ok(ApiResponseDto.<CategoriaTutorialResponseDto>builder()
                .mensaje("Detalle de la categoría recuperado")
                .datos(data)
                .success(true)
                .build());
    }

    // CREAR NUEVA CATEGORÍA (Solo ADMIN)
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @io.swagger.v3.oas.annotations.Operation(summary = "Crear nueva categoría", description = "Crea una nueva categoría de tutorial")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Categoría creada con éxito")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Solicitud inválida. Verifique los datos proporcionados")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Acceso denegado. El usuario no tiene permisos para crear categorías de tutoriales") 
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<ApiResponseDto<CategoriaTutorialResponseDto>> crear(@Valid @RequestBody CategoriaTutorialRequestDto dto) {
        CategoriaTutorialResponseDto nueva = categoriaService.crear(dto);
        
        return ResponseEntity.ok(ApiResponseDto.<CategoriaTutorialResponseDto>builder()
                .mensaje("Categoría '" + nueva.getNombre() + "' creada con éxito")
                .datos(nueva)
                .success(true)
                .build());
    }

    // ELIMINAR CATEGORÍA (Solo ADMIN)
    // Nota: El Service debe manejar si hay tutoriales asociados antes de borrar
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @io.swagger.v3.oas.annotations.Operation(summary = "Eliminar categoría", description = "Elimina una categoría de tutorial por su ID")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Categoría eliminada con éxito")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Acceso denegado. El usuario no tiene permisos para eliminar categorías de tutoriales") 
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Categoría no encontrada")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<ApiResponseDto<Void>> eliminar(@PathVariable Long id) {
        categoriaService.eliminar(id);
        
        return ResponseEntity.ok(ApiResponseDto.<Void>builder()
                .mensaje("Categoría eliminada correctamente")
                .success(true)
                .build());
    }
}