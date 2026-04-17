package com.backend.controller;

import com.backend.domain.MensajeChat;
import com.backend.domain.enums.EnumMessageType;
import com.backend.repository.MensajeChatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Controller
public class ChatController {

   @Autowired
    private MensajeChatRepository mensajeRepository;

    /**
     * DTO para la comunicación WebSocket. 
     */
    @Data
    public static class ChatMessageDto {
        private String id; 
        private String remitente;
        private String contenido;
        private String hora;
    }

    @Data
    public static class PeticionBorrado {
        private String id;
    }

    // GUARDAR Y ENVIAR MENSAJE
    @MessageMapping("/chat.enviar")
    @SendTo("/topic/sala-publica")
    public ChatMessageDto procesarMensaje(@Payload ChatMessageDto mensajeDto) {
        
        //  Creamos la ENTIDAD 
        MensajeChat entidad = MensajeChat.builder()
                .emisor(mensajeDto.getRemitente())
                .contenido(mensajeDto.getContenido())
                .fechaEnvio(LocalDateTime.now())
                .tipo(EnumMessageType.CHAT)
                .build();

        // Guardamos
        MensajeChat guardado = mensajeRepository.save(entidad);

        //  Mapeamos de vuelta al DTO para el Frontend
        mensajeDto.setId(String.valueOf(guardado.getId()));
        mensajeDto.setHora(guardado.getFechaEnvio().format(DateTimeFormatter.ofPattern("HH:mm")));
        
        System.out.println("Mensaje guardado en BD con ID: " + guardado.getId());
        
        return mensajeDto; 
    }

    //  BORRAR MENSAJE
    @MessageMapping("/chat.borrar")
    @SendTo("/topic/chat.borrados") 
    public PeticionBorrado borrarMensaje(@Payload PeticionBorrado peticion) {
        try {
            Long idMensaje = Long.parseLong(peticion.getId());
            mensajeRepository.deleteById(idMensaje);
            System.out.println("Mensaje borrado definitivamente de la BD, ID: " + idMensaje);
        } catch (Exception e) {
            System.err.println("Error al borrar mensaje de BD: " + e.getMessage());
        }
        return peticion; 
    }
}