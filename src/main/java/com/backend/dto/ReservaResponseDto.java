package com.backend.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import com.backend.domain.enums.EnumEstado;

import lombok.*;

@Data
public class ReservaResponseDto {

    private Long id;

    private LocalDate fecha;

    private EnumEstado estado;

    // CAMPOS NUEVOS PARA ANDROID
    private String actividadNombre; 
    private LocalTime horaInicio;
}
