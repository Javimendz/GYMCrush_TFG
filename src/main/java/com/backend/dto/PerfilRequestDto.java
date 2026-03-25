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
import io.swagger.v3.oas.annotations.media.Schema;


@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "DTO para la solicitud de perfil")
public class PerfilRequestDto {
    @Schema(description = "Nombre del usuario", example = "Juan")
    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 80, message = "El nombre debe tener entre 2 y 80 caracteres")
    private String nombre;
    
    @Schema(description = "Apellidos del usuario", example = "Pérez García")
    @NotBlank(message = "Los apellidos son obligatorios")
    @Size(min = 2, max = 150, message = "Los apellidos deben tener entre 2 y 150 caracteres")
    private String apellidos;

    @Schema(description = "Teléfono del usuario", example = "+34612345678")
    @NotBlank(message = "El teléfono es obligatorio")
    @Pattern(regexp = "^[+]*[(]{0,1}[0-9]{1,4}[)]{0,1}[-\\s\\./0-9]*$", message = "Formato de teléfono no válido")
    private String telefono;

    @Schema(description = "DNI o NIE del usuario", example = "12345678A")
    @NotBlank(message = "El DNI/NIE es obligatorio")
    @Size(min = 9, max = 20, message = "El DNI debe tener un formato válido")
    private String dni;

    @Schema(description = "Fecha de nacimiento del usuario", example = "1990-01-01")
    @NotNull(message = "La fecha de nacimiento es obligatoria")
    @Past(message = "La fecha de nacimiento debe ser una fecha pasada")
    private LocalDate fechaNacimiento;

    @Schema(description = "Dirección del usuario", example = "Calle Falsa 123")
    @NotBlank(message = "La dirección es obligatoria")
    private String direccion;

    @Schema(description = "Ciudad del usuario", example = "Madrid") 
    @NotBlank(message = "La ciudad es obligatoria")
    private String ciudad;

    @Schema(description = "País del usuario", example = "España")
    @NotBlank(message = "El país es obligatorio")
    private String pais;

    @Schema(description = "Código postal del usuario", example = "28001")
    @NotBlank(message = "El código postal es obligatorio")
    private String codigoPostal;

    @Schema(description = "Género del usuario", example = "MASCULINO")
    @NotNull(message = "El género es obligatorio")
    private EnumGenero genero;

    @Schema(description = "Biografía del usuario", example = "Soy un desarrollador backend")
    private String bio; 
    @Schema(description = "Foto del usuario", example = "base64encodedstring")
    private String foto; // Opcional
}
