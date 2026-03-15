package com.backend.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import com.backend.domain.Actividad;
import com.backend.dto.ActividadResponseDto;

/**
 * Mapper para la entidad Actividad.
 * unmappedTargetPolicy = IGNORE: Evita avisos si la Entidad tiene más campos que el DTO (como la lista de horarios).
 * componentModel = "spring": Permite inyectar este mapper en los Services usando @Autowired.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface ActividadMapper {

    /**
     * Convierte un DTO en una Entidad.
     * Útil cuando recibimos datos del frontend para persistirlos en la base de datos.
     */
    Actividad toEntity(ActividadResponseDto actividadResponseDto);

    /**
     * Convierte una Entidad en un DTO.
     * Es el método principal para enviar la información de una actividad a la App de Android
     * de forma limpia y sin datos innecesarios.
     */
    ActividadResponseDto toResponseDto(Actividad actividad);

    /**
     * Convierte una lista de Entidades en una lista de DTOs.
     * MapStruct genera automáticamente el bucle (for) interno. 
     * Fundamental para mostrar el catálogo completo de clases en el RecyclerView de la App.
     */
    List<ActividadResponseDto> toResponseDtoList(List<Actividad> actividades);

    /**
     * Actualiza una instancia existente de Actividad con los datos de un DTO.
     * @MappingTarget indica que no cree un objeto nuevo, sino que modifique el que ya existe.
     * Ideal para el panel de administración cuando se cambia el nombre o lugar de una clase.
     */
    void updateActividadFromDto(ActividadResponseDto dto, @MappingTarget Actividad actividad);

    /**
     * Mapeo especial para la creación de nuevas actividades.
     * @Mapping(target = "id", ignore = true): Ignora el ID del DTO para que la base de datos 
     * genere uno nuevo (autoincremental) y no intentemos sobreescribir uno existente.
     * @Mapping(target = "horarios", ignore = true): Evita la recursividad infinita (bucles) 
     * al no intentar mapear los horarios vinculados en el momento de crear la actividad.
     */
    @Mapping(target = "id", ignore = true) 
    @Mapping(target = "horarios", ignore = true) 
    Actividad toEntityCreate(ActividadResponseDto dto);
}
