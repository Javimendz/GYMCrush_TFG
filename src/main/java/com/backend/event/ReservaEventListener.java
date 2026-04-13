package com.backend.event;


import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.backend.domain.Notificacion;
import com.backend.domain.Reserva;
import com.backend.mapper.NotificacionMapper;
import com.backend.repository.NotificacionRepository;
import com.backend.repository.ReservaRepository;

import jakarta.transaction.Transactional;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Data
@RequiredArgsConstructor
@Slf4j
public class ReservaEventListener {

   private final NotificacionRepository notificacionRepo;
    private final ReservaRepository reservaRepo;
    private final SimpMessagingTemplate messagingTemplate; 
    private final NotificacionMapper notificacionMapper; // 1. Inyecta tu mapper

    @Async
    @EventListener
    @Transactional
    public void handleReservaEvent(ReservaEvent event) {
        log.info("Procesando notificación para Reserva ID: {}", event.reservaId());

        Reserva reserva = reservaRepo.findById(event.reservaId())
            .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));

        // 2. Crear y guardar la entidad
        Notificacion n = new Notificacion();
        n.setUsuario(reserva.getUsuario());
        n.setTitulo(event.titulo());
        n.setMensaje(event.mensaje());
        n.setTipo(event.tipo());
        n = notificacionRepo.save(n); // Guardamos para tener el ID y la fecha

        // 3. ¡ESTA ES LA CLAVE! Convertir a DTO antes de enviar
        // El DTO no tiene referencias circulares, es un objeto plano y seguro.
        var notificacionDto = notificacionMapper.toResponseDto(n);

        log.info("Enviando DTO por WebSocket a usuario: {}", reserva.getUsuario().getUsername());

        // 4. Enviar el DTO, NO la entidad 'n'
        messagingTemplate.convertAndSendToUser(
            reserva.getUsuario().getUsername(), 
            "/queue/notificaciones", 
            notificacionDto 
        );
    }
}