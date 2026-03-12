//Paquete
package com.backend.security.dto;
import java.util.Set;

import com.backend.domain.enums.EnumGenero;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
    @NotBlank(message = "El teléfono es obligatorio")
    private String telefono;

    @NotBlank(message = "El DNI es obligatorio")
    private String dni;

    @NotBlank(message = "La dirección es obligatoria")
    private String direccion;

    @NotBlank(message = "La ciudad es obligatoria")
    private String ciudad;

    @NotBlank(message = "El país es obligatorio")
    private String pais;

    @NotBlank(message = "El código postal es obligatorio")
    private String codigoPostal;

    @NotNull(message = "La fecha de nacimiento es obligatoria")
    private java.time.LocalDate fechaNacimiento;

    @NotNull(message = "El género es obligatorio")
    private EnumGenero genero;

    @NotBlank(message = "El rol es obligatorio")
    private Set<String> rol;
    
}
