package com.backend.dto;

import java.util.Set;

import com.backend.domain.Role;

import lombok.*;

@Data
public class UsuarioResponseDto {
    

    private Long id;
    private String correo;
    private Set<Role> rol;
    private String nombre;
    private String apellidos;
    private String telefono;
    private String direccion;
    private String ciudad;
    private String pais;
    private String codigoPostal;

}
