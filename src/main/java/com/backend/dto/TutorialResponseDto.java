package com.backend.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TutorialResponseDto {
    private Long id;
    private String titulo;
    private String descripcion;
    private String urlVideo;
    private Integer duracionMin;
    private Long categoriaId;
    private String nombreCategoria;
}
