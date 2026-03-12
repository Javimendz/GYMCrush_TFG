package com.backend.dto;

import java.util.Set;

import com.backend.domain.Role;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL) //Para no incluir los valores NULL
@JsonPropertyOrder({ //Define el orden de los campos en la respuesta
    "id", 
    "nombre", 
    "apellidos", 
    "correo", 
    "telefono", 
    "rol", 
    "direccion", 
    "ciudad", 
    "codigoPostal", 
    "pais" 
})
public class UsuarioResponseDto {
    
    
    private Long id;
    
    private Set<Role> rol;
    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "Debe proporcionar un formato de correo válido (ejemplo@dominio.com)")
    private String correo;

    @Size(min = 3, max = 50, message = "El nombre debe tener entre 3 y 50 caracteres")
    private String nombre;

    @Size(min = 2, max = 100, message = "Los apellidos deben tener entre 2 y 100 caracteres")
    private String apellidos;

    @Pattern(regexp = "^[+]*[(]{0,1}[0-9]{1,4}[)]{0,1}[-\\s\\./0-9]*$", message = "El formato del teléfono no es válido")
    private String telefono;

    @Size(max = 255, message = "La dirección es demasiado larga")
    private String direccion;

    @Size(max = 100, message = "La ciudad es demasiado larga")
    private String ciudad;

    @Size(max = 100, message = "El país es demasiado largo")
    private String pais;

    @Pattern(regexp = "^[0-9]{5}$", message = "El código postal debe tener 5 dígitos (ejemplo: 28001)")
    private String codigoPostal;

 
    
  

}
