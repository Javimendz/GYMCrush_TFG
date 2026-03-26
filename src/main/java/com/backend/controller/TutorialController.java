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
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.backend.service.ITutorialService;

/**
 * Controlador REST para gestionar los tutoriales de ejercicios.
 * <p>
 * Este controlador proporciona endpoints para:
 * <ul>
 *   <li>Listar todos los tutoriales</li>
 *   <li>Filtrar tutoriales por categoría</li>
 *   <li>Crear nuevos tutoriales (ADMIN)</li>
 *   <li>Eliminar tutoriales (ADMIN)</li>
 *   <li>Listar categorías disponibles</li>
 * </ul>
 * </p>
 *
 * @author Backend Team
 * @version 1.0
 * @since 2026
 */
@RestController
@RequestMapping("/api/v1/tutoriales")
@RequiredArgsConstructor
@Slf4j
@io.swagger.v3.oas.annotations.tags.Tag(name = "Tutorial", description = "Operaciones relacionadas con los tutoriales")
public class TutorialController {

    private final ITutorialService tutorialService;

    /**
     * Lista todos los tutoriales disponibles en el sistema.
     *
     * @return ResponseEntity con la lista completa de tutoriales
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @io.swagger.v3.oas.annotations.Operation(summary = "Listar todos los tutoriales", description = "Permite a un usuario o administrador listar todos los tutoriales disponibles")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Catálogo de tutoriales recuperado con éxito")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<ApiResponseDto<List<TutorialResponseDto>>> listarTodos() {
        log.info("Listando todos los tutoriales");
        List<TutorialResponseDto> data = tutorialService.obtenerTodos();
        log.debug("Total de tutoriales recuperados: {}", data.size());
        
        return ResponseEntity.ok(ApiResponseDto.<List<TutorialResponseDto>>builder()
                .mensaje("Catálogo completo de tutoriales recuperado")
                .datos(data)
                .success(true)
                .build());
    }

    /**
     * Lista los tutoriales filtrados por una categoría específica.
     *
     * @param catId el ID de la categoría para filtrar
     * @return ResponseEntity con la lista de tutoriales de esa categoría
     */
    @GetMapping("/categoria/{catId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @io.swagger.v3.oas.annotations.Operation(summary = "Listar tutoriales por categoría", description = "Permite a un usuario o administrador listar tutoriales por su categoría")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Tutoriales de la categoría recuperados con éxito")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Categoría no encontrada")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")  
    public ResponseEntity<ApiResponseDto<List<TutorialResponseDto>>> listarPorCategoria(@PathVariable Long catId) {
        log.info("Listando tutoriales por categoría ID: {}", catId);
        List<TutorialResponseDto> data = tutorialService.obtenerPorCategoria(catId);
        log.debug("Tutoriales encontrados para categoría {}: {}", catId, data.size());
        
        return ResponseEntity.ok(ApiResponseDto.<List<TutorialResponseDto>>builder()
                .mensaje("Tutoriales de la categoría recuperados con éxito")
                .datos(data)
                .success(true)
                .build());
    }

    /**
     * Crea un nuevo tutorial en el sistema.
     * <p>
     * Solo los usuarios con rol ADMIN pueden crear tutoriales.
     * </p>
     *
     * @param dto los datos del tutorial a crear
     * @return ResponseEntity con el tutorial creado
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @io.swagger.v3.oas.annotations.Operation(summary = "Subir tutorial", description = "Permite al administrador subir un nuevo tutorial")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Tutorial subido correctamente")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Solicitud inválida")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")  
    public ResponseEntity<ApiResponseDto<TutorialResponseDto>> crear(@Valid @RequestBody TutorialRequestDto dto) {
        log.info("Creando nuevo tutorial: {}", dto.getTitulo());
        TutorialResponseDto nuevo = tutorialService.crear(dto);
        log.info("Tutorial creado exitosamente con ID: {}", nuevo.getId());
        
        return ResponseEntity.ok(ApiResponseDto.<TutorialResponseDto>builder()
                .mensaje("Tutorial subido correctamente")
                .datos(nuevo)
                .success(true)
                .build());
    }

    /**
     * Lista todas las categorías de tutoriales disponibles.
     *
     * @return ResponseEntity con la lista de categorías
     */
    @GetMapping("/categorias")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @io.swagger.v3.oas.annotations.Operation(summary = "Listar categorías de tutoriales", description = "Permite a un usuario o administrador listar todas las categorías disponibles para los tutoriales")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Listado de categorías recuperado con éxito")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")  
    public ResponseEntity<ApiResponseDto<List<CategoriaTutorialResponseDto>>> listarCategorias() {
        log.info("Listando categorías de tutoriales");
        List<CategoriaTutorialResponseDto> data = tutorialService.listarCategorias();
        log.debug("Total de categorías recuperadas: {}", data.size());
        
        return ResponseEntity.ok(ApiResponseDto.<List<CategoriaTutorialResponseDto>>builder()
                .mensaje("Listado de categorías recuperado")
                .datos(data)
                .success(true)
                .build());
    }

    /**
     * Elimina un tutorial del sistema.
     * <p>
     * Solo los usuarios con rol ADMIN pueden eliminar tutoriales.
     * </p>
     *
     * @param id el ID del tutorial a eliminar
     * @return ResponseEntity con confirmación de eliminación
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @io.swagger.v3.oas.annotations.Operation(summary = "Eliminar tutorial", description = "Permite al administrador eliminar un tutorial existente")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Tutorial eliminado correctamente")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Tutorial no encontrado")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")  
    public ResponseEntity<ApiResponseDto<Void>> eliminar(@PathVariable Long id) {
        log.info("Eliminando tutorial con ID: {}", id);
        tutorialService.eliminar(id);
        log.info("Tutorial {} eliminado exitosamente", id);
        
        return ResponseEntity.ok(ApiResponseDto.<Void>builder()
                .mensaje("Tutorial eliminado correctamente")
                .success(true)
                .build());
    }
}