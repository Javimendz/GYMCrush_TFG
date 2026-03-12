package com.backend.dto;

import java.util.Set;

import com.backend.domain.Role;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL) // Para no incluir los valores NULL
@JsonPropertyOrder({ // Define el orden de los campos en la respuesta
        "id",
        "correo",
        "telefono",
        "rol",
})
public class UsuarioResponseDto {

    private Long id;
    private String username;
    private Set<Role> rol;
    private String correo;
    private String telefono;
    

}
