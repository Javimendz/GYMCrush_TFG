package com.backend.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CategoriaTutorialResponseDto {
    private Long id;
    private String nombre;
    private String descripcion;
    private Integer cantidadVideos; // Calculado en el Mapper para la UI
}