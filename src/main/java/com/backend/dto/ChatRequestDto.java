package com.backend.dto;


import com.backend.domain.enums.EnumMessageType;

// Esto es lo único que el usuario envía desde Android
public record ChatRequestDto(
    String contenido,
    EnumMessageType tipo
) {}