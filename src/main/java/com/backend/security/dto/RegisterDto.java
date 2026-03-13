//Paquete

package com.backend.security.dto;

import java.time.LocalDate;
import java.util.Set;

import com.backend.domain.enums.EnumGenero;

import jakarta.validation.constraints.Email;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
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

    @Size(min = 8, message = "Mínimo 8 caracteres")

    private String password;

    @NotBlank(message = "El correo es obligatorio")

    @Email(message = "Formato de correo no válido")

    private String correo;

    @NotBlank(message = "El nombre es obligatorio")

    private String nombre;

    @NotBlank(message = "Los apellidos son obligatorios")

    private String apellidos;

    

    private String telefono;
    private String dni;
    private String direccion;
    private String ciudad;
    private String pais;
    private String codigoPostal;
    private LocalDate fechaNacimiento;
    private EnumGenero genero;

    @NotEmpty(message = "El rol es obligatorio")
    private Set<String> rol;
    

   

    

    

}

