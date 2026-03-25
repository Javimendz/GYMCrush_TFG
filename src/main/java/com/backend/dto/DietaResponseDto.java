package com.backend.dto;

import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;

@Data

@Schema(description = "DTO para la respuesta de dieta")
public class DietaResponseDto {
    @Schema(description = "ID único de la dieta", example = "1")
    private Long id;
    @Schema(description = "Nombre de la dieta", example = "Dieta para perder peso")
    private String nombre;
    @Schema(description = "Tipo de dieta (diaria, semanal, etc.)", example = "Diaria")
    private String tipo;
    @Schema(description = "Cantidad de proteínas en la dieta", example = "50")
    private Integer cantidadProteinas;
    @Schema(description = "Cantidad de carbohidratos en la dieta", example = "200") 
    private Integer cantidadCarbohidratos; 
    @Schema(description = "Cantidad de grasas en la dieta", example = "50")
    private Integer cantidadGrasas; 
    @Schema(description = "Cantidad total de calorias en la dieta", example = "300")
    private Integer cantidadTotal;
    @Schema(description = "Categoría de la dieta (vegana, vegetariana, etc.)", example = "Vegana")
    private String categoria;
}
