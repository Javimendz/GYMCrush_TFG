package com.backend.dto;

import lombok.Data;

@Data
public class ActividadResponseDto {
    
    private Long id;

    private String nombre;

    private String descripcion;

    private Integer duracion;

    private String lugar;

    private Integer precio;
    
}
