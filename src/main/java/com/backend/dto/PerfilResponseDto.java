package com.backend.dto;

import java.time.LocalDate;
import java.util.List;

import com.backend.domain.enums.EnumGenero;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;  

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO para representar la respuesta de un perfil de usuario")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PerfilResponseDto {
    @Schema(description = "ID único del perfil", example = "123")
    private Long id;
    @Schema(description = "Nombre del usuario", example = "Juan")
    private String nombre;
    @Schema(description = "Apellidos del usuario", example = "Pérez")
    private String apellidos;
    @Schema(description = "Teléfono del usuario", example = "600123456")
    private String telefono;
    @Schema(description = "DNI del usuario", example = "12345678A")
    private String dni;
    @Schema(description = "Fecha de nacimiento del usuario", example = "2000-01-01")
    private LocalDate fechaNacimiento;
    @Schema(description = "Género del usuario", example = "MASCULINO")
    private EnumGenero genero;
    @Schema(description = "Foto del usuario", example = "base64encodedstring")
    private String foto;
    @Schema(description = "Dirección del usuario", example = "Calle Falsa 123")
    private String direccion;
    @Schema(description = "Ciudad del usuario", example = "Madrid")
    private String ciudad;
    @Schema(description = "País del usuario", example = "España")
    private String pais;
    @Schema(description = "Código postal del usuario", example = "28001")
    private String codigoPostal;
    @Schema(description = "Biografía del usuario", example = "Soy un apasionado por la salud")
    private String bio;

    //Datos salud
    @Schema(description = "Peso del usuario", example = "70.5")
    private Double peso;
    @Schema(description = "Estatura del usuario", example = "1.75")
    private Double estatura;
    @Schema(description = "Nivel de actividad del usuario", example = "ALTA")
    private String nivelActividad; 
    @Schema(description = "Índice de masa corporal del usuario", example = "22.5")
    private Double imc;

    //Campos del usuario
    @Schema(description = "Nombre de usuario del usuario", example = "juanperez")
    private String username;
    @Schema(description = "Correo electrónico del usuario", example = "juanperez@example.com")
    private String correo;
    @Schema(description = "Roles asignados al usuario", example = "[\"ROLE_USER\", \"ROLE_ADMIN\"]")
    private List<String> rol;
}
