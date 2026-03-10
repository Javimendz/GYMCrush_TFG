package com.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.*;

@Data
public class SaludResponseDto {
    private Long id;

    private BigDecimal peso; 

    private BigDecimal estatura;

    private String nivelActividad;

    private String comentario;

    private LocalDateTime fechaMedicion; 
}
