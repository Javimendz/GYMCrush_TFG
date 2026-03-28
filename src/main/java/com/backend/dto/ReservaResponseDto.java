package com.backend.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservaResponseDto {

    private Long id;
    private LocalDate fecha;
    private String estado;

    // Datos del Usuario
    private String username;
      // CAMPOS NUEVOS PARA ANDROID
    private String nombreActividad;
    private String diaSemana;
    private LocalTime horaInicio;
}
