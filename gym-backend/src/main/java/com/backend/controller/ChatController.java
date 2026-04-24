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
        private Long usuarioId;
        
    }

    @Data
    public static class PeticionBorrado {
        private String id;
        private Long usuarioId;
    }

    // GUARDAR Y ENVIAR MENSAJE
    @MessageMapping("/chat.enviar")
    @SendTo("/topic/sala-publica")
    public ChatMessageDto procesarMensaje(@Payload ChatMessageDto mensajeDto) {

        // Creamos la ENTIDAD
        MensajeChat entidad = MensajeChat.builder()
                .emisor(mensajeDto.getRemitente())
                .usuarioId(mensajeDto.getUsuarioId())
                .contenido(mensajeDto.getContenido())
                .fechaEnvio(LocalDateTime.now())
                .tipo(EnumMessageType.CHAT)
                .build();

        // Guardamos
        MensajeChat guardado = mensajeRepository.save(entidad);

        // Mapeamos de vuelta al DTO para el Frontend
        mensajeDto.setId(String.valueOf(guardado.getId()));
        mensajeDto.setHora(guardado.getFechaEnvio().format(DateTimeFormatter.ofPattern("HH:mm")));

        System.out.println("Mensaje guardado en BD con ID: " + guardado.getId());

        return mensajeDto;
    }

    // BORRAR MENSAJE
  @MessageMapping("/chat.borrar")
    @SendTo("/topic/chat.borrados")
    public PeticionBorrado borrarMensaje(@Payload PeticionBorrado peticion) {
        try {
            Long idMensaje = Long.parseLong(peticion.getId());
            
            //  Buscamos el mensaje en la base de datos
            mensajeRepository.findById(idMensaje).ifPresent(mensajeEnBD -> {
                
                // Solo borramos si el usuarioId del mensaje coincide con el que pide borrar
                if (mensajeEnBD.getUsuarioId() != null && 
                    mensajeEnBD.getUsuarioId().equals(peticion.getUsuarioId())) {
                    
                    mensajeRepository.deleteById(idMensaje);
                    System.out.println("Mensaje borrado con éxito, ID: " + idMensaje);
                } else {
                    System.err.println("¡Intento de borrado no autorizado!");
                }
            });

        } catch (Exception e) {
            System.err.println("Error al borrar mensaje de BD: " + e.getMessage());
        }
        return peticion;
    }
}
