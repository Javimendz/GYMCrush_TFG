package com.backend.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL) // Para no incluir los valores NULL
@JsonPropertyOrder({
    "id",
    "username",
    "nombre",
    "apellidos",
    "dni",
    "correo",
    "telefono",
    "direccion",
    "ciudad",
    "codigoPostal",
    "pais",
    "rol"
})
public class UsuarioResponseDto {

    private Long id;
    private String username;
    private List<RoleDto> rol;
    private String correo;
    private String telefono;

    private String nombre;
    private String apellidos;
    private String dni;
    private String direccion;
    private String ciudad;
    private String pais;
    private String codigoPostal;
    private String foto;
    private String bio;

}
