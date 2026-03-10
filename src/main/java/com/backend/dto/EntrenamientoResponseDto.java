package com.backend.dto;

import java.time.LocalDateTime;

import lombok.*;

@Data
public class EntrenamientoResponseDto {

    private Long id;

    private LocalDateTime fecha;

    private LocalDateTime hora;

    private LocalDateTime duracion;

    private String intensidad;
}
