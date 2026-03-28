package com.backend.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import com.backend.domain.Actividad;
import com.backend.dto.ActividadRequestDto;
import com.backend.dto.ActividadResponseDto;

/**
 * Mapper para la entidad Actividad.
 * unmappedTargetPolicy = IGNORE: Evita avisos si la Entidad tiene más campos que el DTO (como la lista de horarios).
 * componentModel = "spring": Permite inyectar este mapper en los Services usando @Autowired.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface ActividadMapper {

    // Para mostrar datos Entidad - ResponseDto
    ActividadResponseDto toResponseDto(Actividad actividad);

    List<ActividadResponseDto> toResponseDtoList(List<Actividad> actividades);

    // CREAR RequestDto - Entidad
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "horarios", ignore = true)
    Actividad toEntityCreate(ActividadRequestDto dto); //  el RequestDto

    // Para ACTUALIZAR RequestDto - Entidad existente
    @Mapping(target = "id", ignore = true)
    void updateActividadFromDto(ActividadRequestDto dto, @MappingTarget Actividad actividad);
}
