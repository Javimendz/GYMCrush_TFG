package com.backend.event;

import com.backend.domain.Reserva;
import com.backend.domain.enums.TipoNotificacion;

public record ReservaEvent(
    Reserva reserva, 
    String mensaje, 
    String titulo, 
    TipoNotificacion tipo
) {}