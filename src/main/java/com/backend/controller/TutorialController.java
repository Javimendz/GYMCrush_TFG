package com.backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import com.backend.dto.TutorialResponseDto;
import com.backend.dto.TutorialRequestDto;
import com.backend.dto.CategoriaTutorialResponseDto;
import com.backend.security.dto.ApiResponseDto;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.backend.service.ITutorialService;

@RestController
@RequestMapping("/api/v1/tutoriales")
@RequiredArgsConstructor
@io.swagger.v3.oas.annotations.tags.Tag(name = "Tutorial", description = "Operaciones relacionadas con los tutoriales")
public class TutorialController {

    private final ITutorialService tutorialService;

    
    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @io.swagger.v3.oas.annotations.Operation(summary = "Listar todos los tutoriales", description = "Permite a un usuario o administrador listar todos los tutoriales disponibles")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Catálogo de tutoriales recuperado con éxito")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<ApiResponseDto<List<TutorialResponseDto>>> listarTodos() {
        List<TutorialResponseDto> data = tutorialService.obtenerTodos();
        
        return ResponseEntity.ok(ApiResponseDto.<List<TutorialResponseDto>>builder()
                .mensaje("Catálogo completo de tutoriales recuperado")
                .datos(data)
                .success(true)
                .build());
    }

    // CUALQUIER USUARIO LOGUEADO PUEDE FILTRAR POR CATEGORÍA
    @GetMapping("/categoria/{catId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @io.swagger.v3.oas.annotations.Operation(summary = "Listar tutoriales por categoría", description = "Permite a un usuario o administrador listar tutoriales por su categoría")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Tutoriales de la categoría recuperados con éxito")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Categoría no encontrada")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")  
    public ResponseEntity<ApiResponseDto<List<TutorialResponseDto>>> listarPorCategoria(@PathVariable Long catId) {
        List<TutorialResponseDto> data = tutorialService.obtenerPorCategoria(catId);
        
        return ResponseEntity.ok(ApiResponseDto.<List<TutorialResponseDto>>builder()
                .mensaje("Tutoriales de la categoría recuperados con éxito")
                .datos(data)
                .success(true)
                .build());
    }

    // SOLO EL ADMIN PUEDE SUBIR VIDEOS
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @io.swagger.v3.oas.annotations.Operation(summary = "Subir tutorial", description = "Permite al administrador subir un nuevo tutorial")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Tutorial subido correctamente")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Solicitud inválida")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")  
    public ResponseEntity<ApiResponseDto<TutorialResponseDto>> crear(@Valid @RequestBody TutorialRequestDto dto) {
        TutorialResponseDto nuevo = tutorialService.crear(dto);
        
        return ResponseEntity.ok(ApiResponseDto.<TutorialResponseDto>builder()
                .mensaje("Tutorial subido correctamente")
                .datos(nuevo)
                .success(true)
                .build());
    }

    // CUALQUIER USUARIO LOGUEADO PUEDE VER LAS CATEGORÍAS (Para el menú de la App)
    @GetMapping("/categorias")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @io.swagger.v3.oas.annotations.Operation(summary = "Listar categorías de tutoriales", description = "Permite a un usuario o administrador listar todas las categorías disponibles para los tutoriales")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Listado de categorías recuperado con éxito")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")  
    public ResponseEntity<ApiResponseDto<List<CategoriaTutorialResponseDto>>> listarCategorias() {
        List<CategoriaTutorialResponseDto> data = tutorialService.listarCategorias();
        
        return ResponseEntity.ok(ApiResponseDto.<List<CategoriaTutorialResponseDto>>builder()
                .mensaje("Listado de categorías recuperado")
                .datos(data)
                .success(true)
                .build());
    }

    // SOLO EL ADMIN PUEDE ELIMINAR TUTORIALES
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @io.swagger.v3.oas.annotations.Operation(summary = "Eliminar tutorial", description = "Permite al administrador eliminar un tutorial existente")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Tutorial eliminado correctamente")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Tutorial no encontrado")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")  
    public ResponseEntity<ApiResponseDto<Void>> eliminar(@PathVariable Long id) {
        tutorialService.eliminar(id);
        
        return ResponseEntity.ok(ApiResponseDto.<Void>builder()
                .mensaje("Tutorial eliminado correctamente")
                .success(true)
                .build());
    }
}