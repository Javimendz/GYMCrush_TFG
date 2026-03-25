package com.backend.dto;


import com.backend.domain.enums.EnumMessageType;

// Esto es lo único que el usuario envía desde Android
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO para la solicitud de chat")
public record ChatRequestDto(
    @Schema(description = "Contenido del mensaje", example = "Hola, ¿cómo estás?")
    String contenido,
    @Schema(description = "Tipo de mensaje (texto, imagen, video, etc.)", example = "TEXTO")
    EnumMessageType tipo
) {}