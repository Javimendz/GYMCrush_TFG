package com.backend.dto;

import lombok.Builder;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Builder
@Schema(description = "DTO para la respuesta de una categoría de tutorial")
public class CategoriaTutorialResponseDto {
    @Schema(description = "ID único de la categoría de tutorial", example = "1")
    private Long id;
    @Schema(description = "Nombre de la categoría de tutorial", example = "Deportes")
    private String nombre;
    @Schema(description = "Descripción detallada de la categoría de tutorial", example = "Categoría que contiene tutoriales sobre deportes")
    private String descripcion;
    @Schema(description = "Cantidad de videos en la categoría de tutorial", example = "10")
    private Integer cantidadVideos; // Calculado en el Mapper para la UI
}