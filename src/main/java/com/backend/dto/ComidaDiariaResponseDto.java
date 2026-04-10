package com.backend.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class ComidaDiariaResponseDto {
    private Long id;              // El ID único de esta fila en la DB
    private String momento;       // "Desayuno", "Almuerzo", etc.
    private String nombreAlimento;
    private Double calorias;
    private String imagenUrl;
    // Estos campos ayudan al Frontend a mostrar info nutricional por plato
    private Double proteina;
    private Double carbohidratos;
    private Double grasas;
}