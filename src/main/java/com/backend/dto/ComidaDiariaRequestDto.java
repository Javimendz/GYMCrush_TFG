package com.backend.dto;

import lombok.Data;

@Data
public class ComidaDiariaRequestDto {
    private String momento;       // Ej: "DESAYUNO"
    private String nombreAlimento; // Ej: "Avena con leche"
    private Double cantidadGramos; // Ej: 100.0
    private Double calorias;      // Ej: 350.0
    private Long planNutricionalId; // Relación para saber a qué plan pertenece
    
    // Opcional: Macros si ya los calculaste antes de enviar
    private String proteina;
    private String carbohidratos;
    private String grasas;
}