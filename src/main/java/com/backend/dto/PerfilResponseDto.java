package com.backend.dto;

import java.time.LocalDate;
import java.util.List;

import com.backend.domain.enums.EnumGenero;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PerfilResponseDto {
    private Long id;
    private String nombre;
    private String apellidos;
    private String telefono;
    private String dni;
    private LocalDate fechaNacimiento;
    private EnumGenero genero;
    private String foto;
    private String direccion;
    private String ciudad;
    private String pais;
    private String codigoPostal;
    private String bio;

    //Datos salud
    private Double peso;
    private Double estatura;
    private String nivelActividad; 
    private Double imc;

    //Campos del usuario
    private String username;
    private String correo;
    private List<String> roles;
}
