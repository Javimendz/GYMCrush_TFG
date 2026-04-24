package com.backend.dto;

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

    private String nombre;
    private String descripcion;
    private Integer duracion;
    private String intensidad;
    private Integer cantidadEjercicios;

}
