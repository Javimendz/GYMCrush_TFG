package com.backend.dto;

import lombok.Data;

@Data
public class SugerenciaRequestDto {
    private String nombreDieta;
    private Integer caloriasObjetivo;
    private String macronutrientesJson; // Aquí va el texto o JSON de la dieta
    private String tipoDieta;
    private Long usuarioId; // Se mapea a usuario.id
    private Long dietaId;   // Se mapea a dieta.id
}