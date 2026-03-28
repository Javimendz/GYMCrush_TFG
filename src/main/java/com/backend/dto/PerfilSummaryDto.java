package com.backend.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PerfilSummaryDto {
    
    private Long id;
    private String username;
    private String nombre;
    private String apellidos;
    private String foto;
    private String location;
    
    

}
