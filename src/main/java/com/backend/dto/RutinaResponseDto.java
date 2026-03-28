package com.backend.dto;

import lombok.*;
import java.time.LocalDate;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO de respuesta para una rutina de ejercicio")
public class RutinaResponseDto {
    @Schema(description = "ID de la rutina")
    private Long id;
    @Schema(description = "Fecha de asignación de la rutina")
    private LocalDate fechaAsignacion;
    @Schema(description = "Orden de la rutina")
    private Integer orden;
    @Schema(description = "Indica si la rutina está completada")
    private Boolean completado;

    @Schema(description = "ID del usuario asignado a la rutina")
    private Long usuarioId;
    @Schema(description = "Nombre de usuario asignado a la rutina")
    private String username;

    // Información del Entrenamiento
    @Schema(description = "ID del entrenamiento asignado a la rutina")
    private Long entrenamientoId;
    @Schema(description = "Intensidad del entrenamiento")
    @Size(max = 50)
    private String intensidadEntrenamiento;
    @Schema(description = "Duración del entrenamiento")
    private Integer duracion;
}