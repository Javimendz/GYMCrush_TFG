package com.backend.mapper;

import com.backend.domain.Horario;
import com.backend.dto.HorarioResponseDto;
import org.mapstruct.*;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface HorarioMapper {

    // Entidad -> DTO (Para enviar a Android)
    @Mapping(source = "actividad.id", target = "actividadId")
    @Mapping(source = "actividad.nombre", target = "actividadNombre")
    HorarioResponseDto toResponseDto(Horario horario);

    // DTO -> Entidad (Para recibir del Admin)
    @Mapping(target = "actividad", ignore = true)
    Horario toEntity(HorarioResponseDto dto);

    List<HorarioResponseDto> toResponseDtoList(List<Horario> horarios);

    // Para creación (Ignora ID autogenerado)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "actividad", ignore = true)
    Horario toEntityCreate(HorarioResponseDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "actividad", ignore = true)
    void updateHorarioFromDto(HorarioResponseDto dto, @MappingTarget Horario horario);
}