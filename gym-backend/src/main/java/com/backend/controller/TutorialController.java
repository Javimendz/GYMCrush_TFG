package com.backend.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.backend.dto.TutorialResponseDto;
import com.backend.dto.TutorialRequestDto;
import com.backend.dto.CategoriaTutorialResponseDto;
import com.backend.dto.ExerciseApiRequestDto; // Importado 1 sola vez
import com.backend.security.dto.ApiResponseDto;
import com.backend.service.ITutorialService;
import com.backend.service.IExerciseProxyService; // CORRECCIÓN: Importamos la Interfaz real

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Controlador REST para gestionar los tutoriales de ejercicios.
 * Centraliza la búsqueda en biblioteca externa y el catálogo local.
 */
@RestController
@RequestMapping("/api/v1/tutoriales")
@RequiredArgsConstructor
@Slf4j
@io.swagger.v3.oas.annotations.tags.Tag(name = "Tutorial", description = "Operaciones relacionadas con los tutoriales")
public class TutorialController {

    private final ITutorialService tutorialService;
    
    // CORRECCIÓN: Ahora sí inyectamos la interfaz
    private final IExerciseProxyService exerciseProxyService; 

    // =========================================================================
    // CONSULTA DE CATÁLOGO (USUARIOS Y ADMIN)
    // =========================================================================

    @GetMapping
    @PreAuthorize("hasAnyRole('USUARIO', 'ADMIN')")
    @io.swagger.v3.oas.annotations.Operation(summary = "Listar todos los tutoriales", description = "Recupera el catálogo completo de tutoriales guardados en el sistema")
    public ResponseEntity<ApiResponseDto<List<TutorialResponseDto>>> listarTodos() {
        log.info("Solicitando catálogo completo de tutoriales");
        List<TutorialResponseDto> data = tutorialService.obtenerTodos();
        
        return ResponseEntity.ok(ApiResponseDto.<List<TutorialResponseDto>>builder()
                .mensaje("Catálogo completo de tutoriales recuperado")
                .datos(data)
                .success(true)
                .build());
    }

    @GetMapping("/categoria/{catId}")
    @PreAuthorize("hasAnyRole('USUARIO', 'ADMIN')")
    @io.swagger.v3.oas.annotations.Operation(summary = "Listar por categoría", description = "Filtra los tutoriales según el ID de la categoría")
    public ResponseEntity<ApiResponseDto<List<TutorialResponseDto>>> listarPorCategoria(@PathVariable("catId") Long catId) {
        log.info("Filtrando tutoriales por categoría ID: {}", catId);
        List<TutorialResponseDto> data = tutorialService.obtenerPorCategoria(catId);
        
        return ResponseEntity.ok(ApiResponseDto.<List<TutorialResponseDto>>builder()
                .mensaje("Tutoriales de la categoría recuperados con éxito")
                .datos(data)
                .success(true)
                .build());
    }

    @GetMapping("/categorias")
    @PreAuthorize("hasAnyRole('USUARIO', 'ADMIN')")
    @io.swagger.v3.oas.annotations.Operation(summary = "Listar categorías", description = "Lista las categorías disponibles para clasificar tutoriales")
    public ResponseEntity<ApiResponseDto<List<CategoriaTutorialResponseDto>>> listarCategorias() {
        log.info("Recuperando listado de categorías");
        List<CategoriaTutorialResponseDto> data = tutorialService.listarCategorias();
        
        return ResponseEntity.ok(ApiResponseDto.<List<CategoriaTutorialResponseDto>>builder()
                .mensaje("Listado de categorías recuperado")
                .datos(data)
                .success(true)
                .build());
    }

    @GetMapping("/buscar")
    @PreAuthorize("hasAnyRole('USUARIO', 'ADMIN')")
    @io.swagger.v3.oas.annotations.Operation(
        summary = "Buscar tutoriales locales", 
        description = "Busca tutoriales en tu base de datos por título"
    )
    public ResponseEntity<ApiResponseDto<List<TutorialResponseDto>>> buscarLocal(@RequestParam String query) {
        log.info("Buscando tutoriales locales con filtro: {}", query);
        List<TutorialResponseDto> data = tutorialService.buscarPorTitulo(query);
        
        return ResponseEntity.ok(ApiResponseDto.<List<TutorialResponseDto>>builder()
                .mensaje("Resultados de búsqueda local recuperados")
                .datos(data)
                .success(true)
                .build());
    }

    // =========================================================================
    // ADMINISTRACIÓN Y BIBLIOTECA EXTERNA (SOLO ADMIN)
    // =========================================================================

    @GetMapping("/biblioteca/buscar")
    @PreAuthorize("hasRole('ADMIN')")
    @io.swagger.v3.oas.annotations.Operation(summary = "Buscar en ExerciseDB", description = "Busca ejercicios en la API externa para crear nuevos tutoriales")
    public ResponseEntity<ApiResponseDto<List<ExerciseApiRequestDto>>> buscarEnBiblioteca(@RequestParam String nombre) {
        log.info("Admin buscando en biblioteca externa: {}", nombre);
        List<ExerciseApiRequestDto> data = exerciseProxyService.buscarPorNombre(nombre);
        
        return ResponseEntity.ok(ApiResponseDto.<List<ExerciseApiRequestDto>>builder()
                .mensaje("Resultados de la biblioteca externa recuperados")
                .datos(data)
                .success(true)
                .build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @io.swagger.v3.oas.annotations.Operation(summary = "Crear tutorial", description = "Guarda un nuevo tutorial en la base de datos local")
    public ResponseEntity<ApiResponseDto<TutorialResponseDto>> crear(@Valid @RequestBody TutorialRequestDto dto) {
        log.info("Admin creando nuevo tutorial: {}", dto.getTitulo());
        TutorialResponseDto nuevo = tutorialService.crear(dto);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponseDto.<TutorialResponseDto>builder()
                .mensaje("Tutorial subido correctamente")
                .datos(nuevo)
                .success(true)
                .build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @io.swagger.v3.oas.annotations.Operation(summary = "Eliminar tutorial", description = "Borra un tutorial del sistema")
    public ResponseEntity<ApiResponseDto<Void>> eliminar(@PathVariable("id") Long id) {
        log.info("Eliminando tutorial con ID: {}", id);
        tutorialService.eliminar(id);
        
        return ResponseEntity.ok(ApiResponseDto.<Void>builder()
                .mensaje("Tutorial eliminado correctamente")
                .success(true)
                .build());
    }
}