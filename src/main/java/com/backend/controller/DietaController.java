package com.backend.controller;

import com.backend.dto.DietaRequestDto;
import com.backend.dto.DietaResponseDto;
import com.backend.security.dto.ApiResponseDto;
import com.backend.service.IDietaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dietas")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DietaController {

    private final IDietaService dietaService;

    @PostMapping
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<ApiResponseDto<DietaResponseDto>> crear(@RequestBody DietaRequestDto dto) {
    DietaResponseDto nuevaDieta = dietaService.crearDieta(dto);
    return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponseDto.success("Dieta creada exitosamente en el catálogo", nuevaDieta));
}
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<ApiResponseDto<List<DietaResponseDto>>> listar() {
        List<DietaResponseDto> dietas = dietaService.listarTodas();
        return ResponseEntity.ok(
                ApiResponseDto.success("Listado de dietas obtenido correctamente", dietas)
        );
    }
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<ApiResponseDto<DietaResponseDto>> obtenerPorId(@PathVariable Long id) {
        DietaResponseDto dieta = dietaService.obtenerPorId(id);
        return ResponseEntity.ok(
                ApiResponseDto.success("Detalle de la dieta encontrado", dieta)
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDto<Void>> eliminar(@PathVariable Long id) {
        dietaService.eliminar(id);
        
        return ResponseEntity.ok(
                ApiResponseDto.success("La dieta ha sido eliminada del catálogo", null)
        );
    }
}