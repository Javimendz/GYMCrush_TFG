package com.backend.dto;

import java.time.LocalDateTime;
import java.time.LocalTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para crear un entrenamiento")
public class EntrenamientoRequestDto {
    
    
    private LocalDateTime fecha;
    private LocalTime hora;
    private Integer duracion;
    private String intensidad;

    private Long usuarioId;

    private String nombreUsuario; 

    private Integer cantidadEjercicios;

    
}
