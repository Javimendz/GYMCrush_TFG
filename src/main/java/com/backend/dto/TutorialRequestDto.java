package com.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.hibernate.validator.constraints.URL;
import lombok.Data;

@Data
public class TutorialRequestDto {

    @NotBlank(message = "El título es obligatorio")
    private String titulo;

    @NotBlank(message = "La descripción técnica es obligatoria")
    private String descripcion;

    @NotBlank(message = "La URL del video es obligatoria")
    @URL(message = "Debe ser una URL válida (Youtube, Vimeo, etc.)")
    private String urlVideo;

    @NotNull(message = "La duración es obligatoria")
    @Positive(message = "La duración debe ser mayor a 0")
    private Integer duracionMin;

    @NotNull(message = "Debes asignar una categoría al tutorial")
    private Long categoriaId;
}