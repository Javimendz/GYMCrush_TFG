//Paquete

package com.backend.security.dto;

import java.time.LocalDate;
import java.util.Set;

import com.backend.domain.enums.EnumGenero;

import jakarta.validation.constraints.Email;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

//Imports

import lombok.AllArgsConstructor;

import lombok.Data;

import lombok.NoArgsConstructor;



@Data

@AllArgsConstructor 

@NoArgsConstructor

public class RegisterDto{



    @NotBlank(message = "El username es obligatorio")
    @Size(min = 3, max = 20, message = "El username debe tener entre 3 y 20 caracteres")
    private String username;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    // Opcional: Podrías añadir un @Pattern para forzar mayúsculas/números
    private String password;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "Formato de correo no válido")
    private String correo;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "Los apellidos son obligatorios")
    private String apellidos;

    @Pattern(regexp = "^[0-9]{9}$", message = "El teléfono debe tener 9 dígitos")
    private String telefono;

    @Pattern(regexp = "^[0-9]{8}[TRWAGMYFPDXBNJZSQVHLCKE]$", message = "Formato de DNI no válido")
    private String dni;

    private String direccion;
    private String ciudad;
    private String pais;

    @Pattern(regexp = "^[0-9]{5}$", message = "El código postal debe tener 5 dígitos")
    private String codigoPostal;

    @NotNull(message = "La fecha de nacimiento es obligatoria")
    @Past(message = "La fecha de nacimiento debe ser una fecha pasada")
    private LocalDate fechaNacimiento;

    @NotNull(message = "El género es obligatorio")
    private EnumGenero genero;

    @NotEmpty(message = "Al menos un rol es obligatorio")
    private Set<String> rol;

}

