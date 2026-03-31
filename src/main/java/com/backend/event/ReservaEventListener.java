package com.backend.event;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.backend.domain.Notificacion;
import com.backend.repository.NotificacionRepository;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Data
@RequiredArgsConstructor
@Slf4j
public class ReservaEventListener {

     private final NotificacionRepository notificacionRepo;
     private final SimpMessagingTemplate messagingTemplate; 
     

    @Async
    @EventListener
    public void handleReservaEvent(ReservaEvent event) {
        // 1. Guardar en Base de Datos
        Notificacion n = new Notificacion();
        n.setUsuario(event.reserva().getUsuario());
        n.setTitulo(event.titulo());
        n.setMensaje(event.mensaje());
        notificacionRepo.save(n);

        // 2. Enviar por WebSocket (In-App instantáneo)
        messagingTemplate.convertAndSendToUser(
            event.reserva().getUsuario().getUsername(), 
            "/queue/notificaciones", 
            n
        );

      
    }
}