package com.backend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class NutricionResponseDto {
    private Long id;
    private String nombreDieta;
    private String tipoDieta;
    private Integer caloriasObjetivo;

    // Aquí convertimos el JSON raro en un objeto real para el Frontend
@JsonRawValue // <-- Esto hace la magia
private String macronutrientes; 
    // ESTO ES LO QUE TE FALTABA: La lista de platos reales
   @JsonProperty("dietas")
    private List<ComidaDiariaResponseDto> comidas;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fechaGeneracion;
    private boolean success;
    private String mensaje;
}