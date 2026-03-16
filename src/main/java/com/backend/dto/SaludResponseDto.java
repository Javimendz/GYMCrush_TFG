package com.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.*;

@Data
public class SaludResponseDto {
    private Long id;

    private BigDecimal peso; 

    private BigDecimal estatura;

    private Double imc;

    private String nivelActividad;

    private String comentario;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") // Formateo limpio para el JSON
    private LocalDateTime fechaMedicion;

    private String username;
}
