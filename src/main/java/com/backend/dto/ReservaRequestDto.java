package com.backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservaRequestDto {

    @NotNull(message = "El ID del usuario es obligatorio")
    private Long usuarioId;

    @NotNull(message = "El ID del horario es obligatorio")
    private Long horarioId;

    /**
     * Opcional: Si permites reservar para días futuros.
     * Si siempre es para el día actual, puedes omitirlo y usar LocalDate.now() en el Service.
     */
    private LocalDate fecha;
}