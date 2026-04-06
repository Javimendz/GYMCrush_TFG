package com.backend.service;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import lombok.Data;

@Service
@Data
public class NotificacionService {

    private SimpMessagingTemplate messagingTemplate;

    public void enviarNotificacionAUsuario(Long usuarioId, String mensaje) {
        // Envía el mensaje al tópico al que el usuario 2 está suscrito
        messagingTemplate.convertAndSend("/topic/notificacion/" + usuarioId, mensaje);
    }
}