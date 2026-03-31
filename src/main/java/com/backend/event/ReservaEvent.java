package com.backend.domain.enums;

import com.backend.domain.Reserva;

public record ReservaEvent(Reserva reserva, String mensaje, String titulo) {}