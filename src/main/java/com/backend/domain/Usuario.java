package com.backend.domain;

import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@Table(name = "usuarios")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "nombre", nullable = false, length = 50)
    private String nombre;
    @Column(name = "apellido", nullable = false, length = 50)
    private String apellido;
    @Column(name = "correo", nullable = false, length = 100, unique = true)
    private String correo;
    @Column(name = "password", nullable = false, length = 255) 
    private String password;
    @Column(name = "telefono", nullable = false, length = 50)
    private String telefono;
    @Column(name = "direccion", nullable = false, length = 50)
    private String direccion;
    @Column(name = "ciudad", nullable = false, length = 50)
    private String ciudad;
    @Column(name = "pais", nullable = false, length = 50)
    private String pais;
    @Column(name = "codigo_postal", nullable = false, length = 50)
    private String codigoPostal;
}
