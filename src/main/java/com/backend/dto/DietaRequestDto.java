package com.backend.dto;

import lombok.*;

@Data
public class DietaRequestDto {
    private String nombre;
    private String tipo;
    private Integer cantidadProteinas;
    private Integer cantidadCarbohidratos;
    private Integer cantidadGrasas;
    private String categoria;
}
