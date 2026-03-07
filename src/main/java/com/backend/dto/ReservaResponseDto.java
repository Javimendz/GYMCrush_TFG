package com.backend.dto;

import java.time.LocalDate;

import com.backend.domain.enums.EnumEstado;

import lombok.*;

@Data
public class ReservaResponseDto {

    private Long id;

    private LocalDate fecha;

    private EnumEstado estado;
}
