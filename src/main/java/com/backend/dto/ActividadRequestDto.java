package com.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO para la solicitud de creación de una actividad")
public class ActividadRequestDto {

    @NotBlank(message = "El nombre de la actividad es obligatorio")
    @Size(min = 3, max = 50, message = "El nombre debe tener entre 3 y 50 caracteres")
    @Schema(description = "Nombre de la actividad", example = "Clase de Fútbol")
    private String nombre;

    @Size(max = 500, message = "La descripción no puede superar los 500 caracteres")
    @Schema(description = "Descripción detallada de la actividad", example = "Clase de fútbol en la sala A")
    private String descripcion;

    @NotNull(message = "La duración es obligatoria")
    @Min(value = 1, message = "La duración mínima debe ser de 1 minuto")
    @Schema(description = "Duración de la actividad en minutos", example = "60")
    private Integer duracion;

    @NotBlank(message = "El lugar es obligatorio (ej: Sala A, Piscina, Exterior)")
    @Schema(description = "Lugar donde se realizará la actividad", example = "Sala A")
    private String lugar;

    @NotNull(message = "El precio es obligatorio")
    @Min(value = 0, message = "El precio no puede ser negativo")
    @Schema(description = "Precio de la actividad", example = "50.00")
    private Integer precio; 
}