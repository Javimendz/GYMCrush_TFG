package com.backend.dto;

import java.time.LocalDateTime;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * Data Transfer Object (DTO) para respuestas de sugerencias de dieta.
 */
@Data 
@Schema(description = "DTO para representar la respuesta de una sugerencia de dieta")
public class SugerenciaDietaReponseDto {
    
    @Schema(description = "Identificador único de la sugerencia de dieta")
    private Long id;
    
    @Schema(description = "Nombre de la dieta sugerida")
    private String nombreDieta;
    
    @Schema(description = "ID del usuario al que pertenece la sugerencia")
    private Long usuarioId;

    @Schema(description = "ID de la dieta base utilizada para esta sugerencia")
    private Long dietaId;

    @Schema(description = "Calorías objetivo para la dieta sugerida")
    private Integer caloriasObjetivo;
    
    @Schema(description = "Macronutrientes recomendados para la dieta en formato JSON")
    private String macronutrientesJson;
    
    @Schema(description = "Tipo de dieta sugerida (e.g., vegetariana, vegana, etc.)")
    private String tipoDieta;

    @Schema(description = "JSON con el desglose de macronutrientes")
    private String detalles;

    @Schema(description = "Fecha en la que se generó la sugerencia de dieta")
    private LocalDateTime fechaGeneracion;

    private boolean esFavorita;
}
