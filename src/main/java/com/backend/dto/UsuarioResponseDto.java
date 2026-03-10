package com.backend.dto;

import lombok.*;

@Data
public class UsuarioResponseDto {
    

    private Long id;
    private String correo;
    private String rol;
    private String nombre;
    private String apellidos;
    private String telefono;
    private String direccion;
    private String ciudad;
    private String pais;
    private String codigoPostal;

}
