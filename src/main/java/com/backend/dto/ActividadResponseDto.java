package com.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "DTO para la respuesta de una actividad")
public class ActividadResponseDto {
    @Schema(description = "ID único de la actividad", example = "1")
    private Long id;

    @Schema(description = "Nombre de la actividad", example = "Clase de Fútbol")
    private String nombre;

    @Schema(description = "Descripción detallada de la actividad", example = "Clase de fútbol en la sala A")
    private String descripcion;

    @Schema(description = "Duración de la actividad en minutos", example = "60")
    private Integer duracion;

    @Schema(description = "Lugar donde se realizará la actividad", example = "Sala A")
    private String lugar;

    @Schema(description = "Precio de la actividad", example = "50.00")
    private Integer precio;
    
}
