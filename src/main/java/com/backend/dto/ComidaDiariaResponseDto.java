package com.backend.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class ComidaDiariaResponseDto {
    private Long id;              // El ID único de esta fila en la DB
    private String momento;       // "Desayuno", "Almuerzo", etc.
    private String nombreAlimento;
    private Double cantidadGramos;
    private Double calorias;
    
    // Estos campos ayudan al Frontend a mostrar info nutricional por plato
    private String proteina;
    private String carbohidratos;
    private String grasas;
}