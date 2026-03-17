package com.backend.dto;

import lombok.Data;
import java.time.LocalTime;

@Data
public class HorarioResponseDto {
    private Long id;
    private String diaSemana;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private Integer aforoMax;
    
    private Long actividadId;
    private String actividadNombre;
}
