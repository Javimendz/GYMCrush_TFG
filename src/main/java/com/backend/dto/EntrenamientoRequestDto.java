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

    // Campos que MapStruct no encontraba:
    @Schema(description = "ID del usuario propietario")
    private Long usuarioId;

    @Schema(description = "Nombre (username) del usuario")
    private String nombreUsuario; 

    @Schema(description = "Número de ejercicios en esta sesión")
    private Integer cantidadEjercicios;
}
