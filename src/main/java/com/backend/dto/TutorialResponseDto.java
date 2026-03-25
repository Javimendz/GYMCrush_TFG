package com.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TutorialResponseDto {
    private Long id;
    private String titulo;
    private String descripcion;
    private String urlVideo;
    private Integer duracionMin;
    private Long categoriaId;
    private String nombreCategoria;
}
