package com.backend.dto;

import lombok.Data;

@Data
public class ComidaDiariaRequestDto {
    private String momento;       // Ej: "DESAYUNO"
    private String nombreAlimento; // Ej: "Avena con leche"
    private Double calorias;      // Ej: 350.0
    private Long planNutricionalId; // Relación para saber a qué plan pertenece
    
    // Opcional: Macros si ya los calculaste antes de enviar
   
    private Double proteina;
    private Double carbohidratos;
    private Double grasas;
}