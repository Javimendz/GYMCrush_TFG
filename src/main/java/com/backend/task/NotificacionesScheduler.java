package com.backend.task;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.backend.domain.Reserva;
import com.backend.domain.enums.TipoNotificacion;
import com.backend.event.ReservaEvent;
import com.backend.repository.ReservaRepository;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificacionesScheduler {

    private final ReservaRepository reservaRepo;
    private final ApplicationEventPublisher eventPublisher;

    @Scheduled(cron = "0 */30 * * * *") // Cada 30 minutos
    public void recordatorioProximasClases() {
        LocalDate hoy = LocalDate.now();
        LocalTime ahora = LocalTime.now();
        LocalTime limite = ahora.plusHours(2);

        log.info("Ejecutando Job de recordatorios para clases entre {} y {}", ahora, limite);

        List<Reserva> proximas = reservaRepo.findReservasParaRecordatorio(hoy, ahora, limite);

        for (Reserva r : proximas) {
            eventPublisher.publishEvent(new ReservaEvent(
                r, 
                "¡Recordatorio! Tu clase de " + r.getHorario().getActividad().getNombre() + " empieza pronto.",
                "Próximo entrenamiento",
                TipoNotificacion.RECORDATORIO
            ));
            
            // Opcional: Aquí podrías marcar la reserva como "recordatorioEnviado = true" 
            // y hacer un reservaRepo.save(r) para no enviar el aviso dos veces.
        }
    }
}