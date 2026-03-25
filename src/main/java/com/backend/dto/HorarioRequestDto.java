package com.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalTime;
import io.swagger.v3.oas.annotations.media.Schema;



@Data
@Schema(description = "DTO para la solicitud de horario")
public class HorarioRequestDto {
    @NotBlank(message = "El día de la semana es obligatorio")
    @Schema(description = "Día de la semana (lunes, martes, etc.)", example = "Lunes")
    private String diaSemana;

    @NotNull(message = "La hora de inicio es obligatoria")
    @Schema(description = "Hora de inicio del horario", example = "08:00")
    private LocalTime horaInicio;

    @NotNull(message = "La hora de fin es obligatoria")
    @Schema(description = "Hora de fin del horario", example = "12:00")
    private LocalTime horaFin;

    @NotNull(message = "El aforo máximo es obligatorio")
    @Schema(description = "Aforo máximo permitido en el horario", example = "30")
    private Integer aforoMax;

    @NotBlank(message = "La sala es obligatoria")
    @Schema(description = "Sala donde se impartirá el entrenamiento", example = "Sala 1")
    private String sala;

    @NotNull(message = "El ID de la actividad es obligatorio")
    @Schema(description = "ID de la actividad asociada al horario", example = "1")
    private Long actividadId; // Solo enviamos el ID
}