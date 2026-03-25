package com.backend.dto;

import java.time.LocalTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO para la respuesta de horario")
public class HorarioResponseDto {
    @Schema(description = "ID único del horario", example = "1")
    private Long id;
    @Schema(description = "Día de la semana (lunes, martes, etc.)", example = "Lunes")
    private String diaSemana;
    @Schema(description = "Hora de inicio del horario", example = "08:00")
    private LocalTime horaInicio;
    @Schema(description = "Hora de fin del horario", example = "12:00")
    private LocalTime horaFin;  
    @Schema(description = "Aforo máximo permitido en el horario", example = "30")
    private Integer aforoMax;
    @Schema(description = "Sala donde se impartirá el entrenamiento", example = "Sala 1")   
    private String sala;
    // Campos "aplanados" de la relación con Actividad
    // Esto es vital para que el RecyclerView en Android muestre el nombre
    // directamente
    @Schema(description = "ID de la actividad asociada al horario", example = "1")
    private Long actividadId;
    @Schema(description = "Nombre de la actividad asociada al horario", example = "Clase de Fútbol")
    private String nombreActividad;
}