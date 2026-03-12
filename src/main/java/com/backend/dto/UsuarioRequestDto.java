//Paquete
package com.backend.dto;

//Imports
import lombok.Data;
import jakarta.validation.constraints.*; // Importante para validaciones

import java.time.LocalDate;
import java.util.Set;

import com.backend.domain.enums.EnumGenero;

@Data // Anotación de Lombok para generar automáticamente getters, setters, toString,
      // hashCode y equals
public class UsuarioRequestDto {
    @NotBlank(message = "El nombre no puede estar vacío")
    @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres")
    private String nombre;
    @NotBlank(message = "Los apellidos no pueden estar vacíos")
    @Size(min = 2, max = 150, message = "Los apellidos deben tener entre 2 y 150 caracteres")
    private String apellidos;

    @NotBlank(message = "El DNI no puede estar vacío")
    @Size(min = 9, max = 20, message = "El DNI debe tener un formato válido")
    private String dni;

    // Dto con validaciones para la entidad Usuario
    @NotBlank(message = "El username no puede estar vacío")
    @Size(min = 3, max = 50, message = "El username debe tener entre 3 y 50 caracteres")
    private String username;
    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "Debe proporcionar un formato de correo válido (ejemplo@dominio.com)")
    private String correo;
    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres para mayor seguridad")
    private String password;
    @Size(max = 20, message = "El telefono no puede superar los 20 caracteres")
    @Pattern(regexp = "^[+]*[(]{0,1}[0-9]{1,4}[)]{0,1}[-\\s\\./0-9]*$", message = "El formato del teléfono no es válido")
    private String telefono;
    @NotBlank(message = "La dirección es obligatoria")
    @Size(max = 255, message = "La dirección es demasiado larga")
    private String direccion;
    @NotBlank(message = "La Ciudad es obligatoria")
    @Size(max = 100, message = "La ciudad es demasiado larga")
    private String ciudad;
    @NotBlank(message = "El Pais es obligatorio")
    @Size(max = 100, message = "El país es demasiado largo")
    private String pais;
    @NotBlank(message = "El codigo postal es obligatorio")
    @Pattern(regexp = "^[0-9]{5}$", message = "El código postal debe tener 5 dígitos (ejemplo: 28001)")
    private String codigoPostal;
    @NotNull(message = "La fecha de nacimiento es obligatoria")
    private LocalDate fechaNacimiento;
    @NotNull(message = "El género es obligatorio")
    private EnumGenero genero;
    private Set<String> rol;

}
