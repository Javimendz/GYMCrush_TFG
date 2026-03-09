//Paquete
package com.backend.dto;
import java.time.LocalDateTime;

import org.springframework.cglib.core.Local;

import com.backend.domain.enums.EnumRol;

//Imports
import lombok.Data;

@Data // Anotación de Lombok para generar automáticamente getters, setters, toString, hashCode y equals
public class UsuarioRequestDto {
    
     private Long id;
    private String nombre;
    private String apellidos;
    private String correo;
    private String password;
    private String telefono;
    private String direccion;
    private String ciudad;
    private String pais;
    private String codigoPostal;
    private EnumRol rol; 
    private String qrToken; 
    private LocalDateTime tokenExpiraEn;

}
