package com.backend.controller;

import com.backend.dto.CategoriaDietaRequestDto;
import com.backend.dto.CategoriaDietaResponseDto;
import com.backend.security.dto.ApiResponseDto;
import com.backend.service.ICategoriaDietaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categorias")
@RequiredArgsConstructor
public class CategoriaDietaController {

    private final ICategoriaDietaService categoriaService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDto<CategoriaDietaResponseDto>> crear(@RequestBody CategoriaDietaRequestDto dto) {
        CategoriaDietaResponseDto nuevaCategoria = categoriaService.crear(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDto.success("Categoría '" + dto.getNombre() + "' creada con éxito", nuevaCategoria));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<ApiResponseDto<List<CategoriaDietaResponseDto>>> listar() {
        List<CategoriaDietaResponseDto> categorias = categoriaService.listarTodas();
        return ResponseEntity.ok(
                ApiResponseDto.success("Listado de categorías obtenido", categorias)
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<ApiResponseDto<CategoriaDietaResponseDto>> obtenerPorId(@PathVariable Long id) {
        CategoriaDietaResponseDto categoria = categoriaService.obtenerPorId(id);
        return ResponseEntity.ok(
                ApiResponseDto.success("Detalle de categoría encontrado", categoria)
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDto<Void>> eliminar(@PathVariable Long id) {
        categoriaService.eliminar(id);
        return ResponseEntity.ok(
                ApiResponseDto.success("La categoría ha sido eliminada correctamente", null)
        );
    }
}