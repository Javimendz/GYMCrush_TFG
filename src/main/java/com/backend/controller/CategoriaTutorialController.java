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
public class CategoriaTutorialController {

    private final ICategoriaTutorialService categoriaService;

    // LISTAR TODAS LAS CATEGORÍAS
    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
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
    public ResponseEntity<ApiResponseDto<Void>> eliminar(@PathVariable Long id) {
        categoriaService.eliminar(id);
        
        return ResponseEntity.ok(ApiResponseDto.<Void>builder()
                .mensaje("Categoría eliminada correctamente")
                .success(true)
                .build());
    }
}