package com.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "DTO para la solicitud de creación de una categoría de tutorial")
public class CategoriaTutorialRequestDto {
    @Schema(description = "Nombre de la categoría de tutorial", example = "Deportes")
    @NotBlank(message = "El nombre de la categoría no puede estar vacío")
    @Size(max = 50, message = "El nombre es demasiado largo")
    private String nombre;

    @Size(max = 255, message = "La descripción no puede superar los 255 caracteres")
    @Schema(description = "Descripción detallada de la categoría de tutorial", example = "Categoría que contiene tutoriales sobre deportes")
    private String descripcion;
}