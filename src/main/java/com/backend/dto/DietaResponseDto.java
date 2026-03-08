package com.backend.dto;

import lombok.*;

@Data
public class DietaResponseDto {
    
    private Long id;

    private String nombre;

    private String tipo;

    private Integer cantidadProteinas;

    private Integer cantidadCarbohidratos; 

    private Integer cantidadGrasas; 

    private Integer cantidadTotal;

    private String categoria;
}
