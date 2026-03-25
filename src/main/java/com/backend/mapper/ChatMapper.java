package com.backend.mapper;

import org.springframework.stereotype.Component;
import com.backend.domain.MensajeChat;
import com.backend.dto.ChatResponseDto;

@Component
public class ChatMapper {

    public ChatResponseDto toDto(MensajeChat entidad) {
        if (entidad == null) return null;

        return ChatResponseDto.builder()
                .id(entidad.getId())
                .contenido(entidad.getContenido())
                .emisor(entidad.getEmisor())
                .fechaEnvio(entidad.getFechaEnvio())
                .tipo(entidad.getTipo())
                .build();
    }
}