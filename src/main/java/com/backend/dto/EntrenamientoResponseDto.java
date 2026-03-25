package com.backend.dto;

import java.time.LocalDateTime;

import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;



@Data
@Schema(description = "DTO para la respuesta de entrenamiento")
public class EntrenamientoResponseDto {
    @Schema(description = "ID único del entrenamiento", example = "1")
    private Long id;
    @Schema(description = "Fecha del entrenamiento", example = "2023-12-31")
    private LocalDateTime fecha;
    @Schema(description = "Hora del entrenamiento", example = "18:00")
    private LocalDateTime hora;
    @Schema(description = "Duración del entrenamiento", example = "60 minutos")
    private LocalDateTime duracion;
    @Schema(description = "Intensidad del entrenamiento (baja, media, alta)", example = "Media")
    private String intensidad;
}
