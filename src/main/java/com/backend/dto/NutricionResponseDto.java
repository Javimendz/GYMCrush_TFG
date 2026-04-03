package com.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class NutricionResponseDto {
    private Long id;
    private String nombreDieta;
    private String tipoDieta;
    private Integer caloriasObjetivo;
    
    // Aquí convertimos el JSON raro en un objeto real para el Frontend
    private String macronutrientes;

    // ESTO ES LO QUE TE FALTABA: La lista de platos reales
    @JsonProperty("dietas") 
    private List<ComidaDiariaResponseDto> comidas;

    private String fechaGeneracion;
    private boolean success;
    private String mensaje;
}