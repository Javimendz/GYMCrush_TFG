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
import lombok.extern.slf4j.Slf4j;

/**
 * Controlador WebSocket para gestionar el chat en tiempo real.
 * <p>
 * Este controlador maneja la mensajería WebSocket para comunicación
 * instantánea entre usuarios del gimnasio.
 * </p>
 *
 * @author Backend Team
 * @version 1.0
 * @since 2026
 */
@Controller
@RequiredArgsConstructor
@Slf4j
@io.swagger.v3.oas.annotations.tags.Tag(name = "Chat", description = "Operaciones relacionadas con el chat en tiempo real")
public class WSChatController {
    
    private final MensajeChatRepository mensajeRepository;
    private final ChatMapper chatMapper;

    /**
     * Procesa un mensaje de chat entrante y lo difunde a todos los usuarios conectados.
     * <p>
     * El mensaje se guarda en la base de datos con el nombre del usuario emisor
     * y se envía a todos los suscriptores del canal /topic/canal1.
     * </p>
     *
     * @param request el DTO con el contenido y tipo del mensaje
     * @param principal el usuario autenticado (puede ser null si es anónimo)
     * @return ChatResponseDto el mensaje procesado para difusión
     */
    @MessageMapping("/chat1")
    @SendTo("/topic/canal1")
    @io.swagger.v3.oas.annotations.Operation(summary = "Procesar mensaje de chat", description = "Permite a un usuario enviar un mensaje al chat y lo envía a todos los usuarios conectados")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Mensaje procesado con éxito")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Solicitud inválida")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")  
    public ChatResponseDto processMessage(ChatRequestDto request, Principal principal) {
        String username = (principal != null) ? principal.getName() : "Anónimo";
        log.info("Mensaje recibido de {}: {}", username, request.contenido());
        
        MensajeChat entidad = MensajeChat.builder()
                .contenido(request.contenido())
                .emisor(username)
                .fechaEnvio(LocalDateTime.now())
                .tipo(request.tipo())
                .build();
        
        MensajeChat guardado = mensajeRepository.save(entidad);
        log.debug("Mensaje guardado con ID: {}", guardado.getId());
        
        return chatMapper.toDto(guardado);
    }

}
