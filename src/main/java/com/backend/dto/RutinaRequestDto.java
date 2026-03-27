package com.backend.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO de solicitud para una rutina de ejercicio")
public class RutinaRequestDto {

    @Schema(description = "Fecha de asignación de la rutina")
    @NotNull(message = "La fecha de asignación es obligatoria")
    private LocalDate fechaAsignacion;

    @Schema(description = "Orden de la rutina")
    @Positive(message = "El orden debe ser un número positivo")
    private Integer orden;

    @Schema(description = "Indica si la rutina está completada")
    private Boolean completado;

    @Schema(description = "ID del usuario asignado a la rutina")
    @NotNull(message = "El ID de usuario es obligatorio")
    private Long usuarioId;

    @Schema(description = "ID del entrenamiento asignado a la rutina")
    @NotNull(message = "El ID de entrenamiento es obligatorio")
    private Long entrenamientoId;
}