package com.backend.security.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonPropertyOrder({ "id", "nombre","username", "roles", "accessToken", "tokenType" }) 
public class JwtAuthResponseDto {
    
    private Long id;
    private List<String> roles;
    private String accessToken;
    private String tokenType;
    private String username;
    private String nombre;
    

    public JwtAuthResponseDto(String accessToken) {
        this.accessToken = accessToken;
        
    }

    
}
