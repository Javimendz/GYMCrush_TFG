package com.backend.dto;

import lombok.*;

@Data
public class UsuarioResponseDto {
    

    private Long id;
    private String nombre;
    private String apellido;
    private String correo;
    private String password;
    private String telefono;
    private String direccion;
    private String ciudad;
    private String pais;
    private String codigoPostal;

}
