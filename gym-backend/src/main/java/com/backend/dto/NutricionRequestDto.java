package com.backend.dto;

import lombok.Data;

@Data
public class NutricionRequestDto {
    private Long usuarioId;
    private Double pesoActual;   // Ej: 85.5
    private Double altura;       // Ej: 180
    private Integer edad;        // Ej: 25
    private String sexo;         // "HOMBRE", "MUJER"
    private String objetivo;     // "VOLUMEN", "DEFINICION", "MANTENIMIENTO"
    private String nivelActividad; // "SEDENTARIO", "MODERADO", "ATLETA"
    private String preferencia;  // "OMNIVORO", "VEGANO", "SIN_GLUTEN"
}