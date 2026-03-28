package com.backend.dto;

import lombok.Data;

@Data
public class NutricionResponseDto {
    private Long id;
    private String nombreDieta;
    private Integer caloriasObjetivo;
    private String tipoDieta;
    private String detalles; // Mapeado del JSON
    private String fechaGeneracion;
}
