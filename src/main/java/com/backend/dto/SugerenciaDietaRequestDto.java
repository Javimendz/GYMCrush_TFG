package com.backend.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para recibir la petición de crear una nueva Sugerencia de Dieta.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SugerenciaDietaRequestDto {

    @NotNull(message = "El ID del usuario es obligatorio")
    @Positive(message = "El ID del usuario debe ser un número positivo")
    private Long usuarioId;

    @NotNull(message = "El ID de la dieta recomendada es obligatorio")
    @Positive(message = "El ID de la dieta debe ser un número positivo")
    private Long dietaId;

   //opcioinal
    private String notas;
}