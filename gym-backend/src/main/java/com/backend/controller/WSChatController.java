package com.backend.controller;

import java.security.Principal;
import java.time.LocalDateTime;

import org.springframework.messaging.handler.annotation.DestinationVariable;
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
public class WSChatController {
    
    private final MensajeChatRepository mensajeRepository;
    private final ChatMapper chatMapper;
    private final org.springframework.messaging.simp.SimpMessagingTemplate messagingTemplate;
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

    @MessageMapping("/chat.ticket.{ticketId}")
    public void procesarMensajeTicket(@DestinationVariable String ticketId, 
                                      ChatRequestDto request, 
                                      Principal principal) {
        
        String username = (principal != null) ? principal.getName() : "Anónimo";
        log.info("Mensaje para Ticket #{}: de {}: {}", ticketId, username, request.contenido());

        // 1. Guardar en BD (Asegúrate de que tu entidad MensajeChat tenga un campo ticketId)
        MensajeChat entidad = MensajeChat.builder()
                .contenido(request.contenido())
                .emisor(username)
                .fechaEnvio(LocalDateTime.now())
                .tipo(request.tipo())
                // .ticketId(ticketId) // <-- Sería ideal guardar a qué ticket pertenece
                .build();
        
        MensajeChat guardado = mensajeRepository.save(entidad);
        ChatResponseDto response = chatMapper.toDto(guardado);

        // 2. Enviar el mensaje solo a los suscritos a este ticket específico
        // Esto equivale a un @SendTo dinámico
        messagingTemplate.convertAndSend("/topic/tickets." + ticketId, response);
    }

}
