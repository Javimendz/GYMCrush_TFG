package com.backend.dto;

import java.time.LocalDateTime;

import lombok.*;

@Data
public class SugerenciaDietaReponseDto {
    private Long id;

    private String nombreDieta;

    private Integer caloriasObjetivo;

    private String macronutrientesJson; 

    private String tipoDieta;

    private LocalDateTime fechaGeneracion;
}
