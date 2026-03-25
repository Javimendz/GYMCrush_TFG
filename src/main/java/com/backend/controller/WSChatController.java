package com.backend.controller;

import java.security.Principal;
import java.time.LocalDateTime;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import com.backend.domain.MensajeChat;
import com.backend.dto.ChatRequestDto;
import com.backend.dto.ChatResponseDto;
import com.backend.mapper.ChatMapper;
import com.backend.repository.MensajeChatRepository;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@io.swagger.v3.oas.annotations.tags.Tag(name = "Chat", description = "Operaciones relacionadas con el chat en tiempo real")
public class WSChatController {
    
    private final MensajeChatRepository mensajeRepository;
    private final ChatMapper chatMapper;

    @MessageMapping("/chat1")
    @SendTo("/topic/canal1")
    @io.swagger.v3.oas.annotations.Operation(summary = "Procesar mensaje de chat", description = "Permite a un usuario enviar un mensaje al chat y lo envía a todos los usuarios conectados")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Mensaje procesado con éxito")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Solicitud inválida")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")  
    public ChatResponseDto processMessage(ChatRequestDto request, Principal principal) {
        String username = (principal != null) ? principal.getName() : "Anónimo";
        
        MensajeChat entidad = MensajeChat.builder()
                .contenido(request.contenido())
                .emisor(username)
                .fechaEnvio(LocalDateTime.now())
                .tipo(request.tipo())
                .build();
        
        MensajeChat guardado = mensajeRepository.save(entidad);

        
        return chatMapper.toDto(guardado);
    }

}
