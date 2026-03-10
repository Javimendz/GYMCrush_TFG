package com.backend.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class ChatResponseDto {
    private Long id; 

    private String mensaje;

    private LocalDate fechaHora; 

    private Boolean leido; 
}
