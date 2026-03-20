package com.backend.dto;

import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HorarioResponseDto {

    private Long id;
    private String diaSemana;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private Integer aforoMax;

    // Campos "aplanados" de la relación con Actividad
    // Esto es vital para que el RecyclerView en Android muestre el nombre directamente
    private Long actividadId;
    private String nombreActividad; 
}