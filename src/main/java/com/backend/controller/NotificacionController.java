package com.backend.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller    
public class NotificacionController {
    
    //aplicacon del servidor
    //app/notificaciones
    @MessageMapping("/sendMessage") // Ruta a la que los clientes enviarán mensajes
    @SendTo("/topic/notificacion") // Ruta a la que se enviarán las notificaciones a los clientes suscritos
    public String sendMessage(String message){

        System.out.println("Mensaje recibido: " + message);
        // Aquí podrías agregar lógica para procesar el mensaje o enviarlo a otros clientes a través de WebSocket
        return "Mensaje procesado: " + message;

    }

}
