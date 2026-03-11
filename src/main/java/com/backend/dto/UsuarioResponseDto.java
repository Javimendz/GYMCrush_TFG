package com.backend.dto;

import lombok.*;

@Data
public class UsuarioResponseDto {
    

    private Long id;
<<<<<<< HEAD
    private String correo;
    private String rol;
    private String nombre;
    private String apellidos;
=======
    private String nombre;
    private String apellido;
    private String correo;
    private String password;
>>>>>>> f4570e441899ef30c12df4e8042f8e8f12d93eac
    private String telefono;
    private String direccion;
    private String ciudad;
    private String pais;
    private String codigoPostal;

}
