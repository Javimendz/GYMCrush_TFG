package com.backend.dto;

import java.time.LocalDateTime;
import com.backend.domain.enums.EnumMessageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatResponseDto {
    private Long id; 
    private String contenido; // Antes era 'mensaje'
    private String emisor;    // Faltaba este campo
    private LocalDateTime fechaEnvio; // Antes era 'fechaHora' y LocalDate
    private EnumMessageType tipo;    // Faltaba este campo
}