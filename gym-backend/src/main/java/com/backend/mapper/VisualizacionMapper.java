package com.backend.mapper;

import com.backend.domain.Visualizacion;
import com.backend.dto.VisualizacionRequestDto;
import com.backend.dto.VisualizacionResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper de MapStruct para la entidad {@link Visualizacion}.
 * <p>
 * Gestiona la conversión entre la tabla intermedia de progreso y sus DTOs,
 * realizando el "aplanamiento" de datos del tutorial y el usuario.
 * </p>
 *
 * @author Backend Team
 * @version 1.0
 */
@Mapper(componentModel = "spring")
public interface VisualizacionMapper {

    /**
     * Convierte un DTO de solicitud a la entidad Visualizacion.
     * <p>
     * Ignoramos las entidades completas y el ID, ya que se gestionan 
     * en la lógica del Service tras buscar en la base de datos.
     * </p>
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "usuario", ignore = true)
    @Mapping(target = "tutorial", ignore = true)
    @Mapping(target = "fechaVisualizacion", ignore = true) // Se asigna vía @PrePersist
    Visualizacion toEntity(VisualizacionRequestDto requestDto);

    /**
     * Convierte la entidad a un DTO de respuesta detallado.
     * <p>
     * Extrae información clave del Tutorial (título, url, duración) 
     * para que la UI no necesite hacer más peticiones.
     * </p>
     */
    @Mapping(target = "usuarioId", source = "usuario.id")
    @Mapping(target = "tutorialId", source = "tutorial.id")
    @Mapping(target = "tituloTutorial", source = "tutorial.titulo")
    @Mapping(target = "urlVideo", source = "tutorial.urlVideo")
    @Mapping(target = "duracionTotalVideo", source = "tutorial.duracionMin")
    @Mapping(target = "nombreCategoria", source = "tutorial.categoria.nombre")
    VisualizacionResponseDto toResponseDto(Visualizacion visualizacion);
}