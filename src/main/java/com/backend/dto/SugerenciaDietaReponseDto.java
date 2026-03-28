package com.backend.dto;

import java.time.LocalDateTime;

import lombok.*;

@Data
public class SugerenciaDietaReponseDto {
    private Long id;
    private String nombreDieta;
    private Integer caloriasObjetivo;
    private String detalles; // Mapeado desde macronutrientesJson
    private String tipoDieta;
    private LocalDateTime fechaGeneracion;
    private Long usuarioId;
    private Long dietaId;
}
