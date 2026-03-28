package com.backend.dto;

import java.time.LocalDateTime;

import lombok.*;

@Data
@Builder
public class TicketResponseDto {

    private Long id;

    private String asunto;

    private String mensaje;

    private String estado;

    private LocalDateTime fechaResolucion;
    
    private LocalDateTime fechaCreacion;
    private String username;
    private Long usuarioId;
    private String usuarioEmail;
    private String nombreCompletoUsuario;

}
