package com.backend.dto;

import java.time.LocalDate;

import com.backend.domain.enums.EnumGenero;
import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PerfilRequestDto {
    
    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 80, message = "El nombre debe tener entre 2 y 80 caracteres")
    private String nombre;

    @NotBlank(message = "Los apellidos son obligatorios")
    @Size(min = 2, max = 150, message = "Los apellidos deben tener entre 2 y 150 caracteres")
    private String apellidos;

    @NotBlank(message = "El teléfono es obligatorio")
    @Pattern(regexp = "^[+]*[(]{0,1}[0-9]{1,4}[)]{0,1}[-\\s\\./0-9]*$", message = "Formato de teléfono no válido")
    private String telefono;

    @NotBlank(message = "El DNI/NIE es obligatorio")
    @Size(min = 9, max = 20, message = "El DNI debe tener un formato válido")
    private String dni;

    @NotNull(message = "La fecha de nacimiento es obligatoria")
    @Past(message = "La fecha de nacimiento debe ser una fecha pasada")
    private LocalDate fechaNacimiento;

    @NotNull(message = "El género es obligatorio")
    private EnumGenero genero;

    private String foto; // Opcional
}
