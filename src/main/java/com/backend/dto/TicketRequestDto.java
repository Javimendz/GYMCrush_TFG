package com.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketRequestDto {

    @NotBlank(message = "El asunto no puede estar vacio")
    @Size(min = 5, max = 100, message = "El asunto debe tener entre 5 y 100 caracteres")
    private String asunto;

    @NotBlank(message = "El mensaje es obligatorio")
    @Size(min = 10, max = 2000, message = "El mensaje debe tener al menos 10 caracteres")
    private String mensaje;

}
