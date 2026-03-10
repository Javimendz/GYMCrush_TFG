package com.backend.dto;

import java.time.LocalDate;

import com.backend.domain.enums.EnumGenero;

import lombok.Data;

@Data
public class PerfilResponseDto {
    private Long id;
    private String nombre;
    private String apellidos;
    private String telefono;
    private String dni;
    private LocalDate fechaNacimiento;
    private EnumGenero genero;
    private String foto;
}
