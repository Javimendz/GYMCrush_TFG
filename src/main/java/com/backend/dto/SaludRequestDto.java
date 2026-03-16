package com.backend.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class SaludRequestDto {

    @NotNull(message = "El peso es obligatorio")
    @Positive(message = "El peso debe ser mayor a 0")
    private Double peso;

    @NotNull(message = "La estatura es obligatoria")
    @Positive(message = "La estatura debe ser mayor a 0")
    private Double estatura;

    @Size(max = 50)
    private String nivelActividad;

    private String comentario;
}