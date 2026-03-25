package com.backend.dto;

import java.time.LocalDateTime;
import com.backend.domain.enums.EnumMessageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO para la respuesta de chat")
public class ChatResponseDto {
    @Schema(description = "ID único del mensaje de chat", example = "1")
    private Long id; 
    @Schema(description = "Contenido del mensaje", example = "Hola, ¿cómo estás?")
    private String contenido; // Antes era 'mensaje'
    @Schema(description = "Emisor del mensaje", example = "usuario123")
    private String emisor;    // Faltaba este campo
    @Schema(description = "Fecha y hora de envío del mensaje", example = "2023-10-15T12:30:00")
    private LocalDateTime fechaEnvio; // Antes era 'fechaHora' y LocalDate
    @Schema(description = "Tipo de mensaje (texto, imagen, video, etc.)", example = "TEXTO")
    private EnumMessageType tipo;    // Faltaba este campo
}