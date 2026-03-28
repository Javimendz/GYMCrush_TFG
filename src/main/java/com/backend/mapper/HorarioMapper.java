package com.backend.mapper;

import com.backend.domain.Horario;
import com.backend.dto.HorarioRequestDto;
import com.backend.dto.HorarioResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface HorarioMapper {

    // 1. De Entidad a Response (Para la App de Android)
    @Mapping(target = "actividadId", source = "actividad.id")
    @Mapping(target = "nombreActividad", source = "actividad.nombre")
    HorarioResponseDto toResponseDto(Horario horario);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "actividad", ignore = true) // Importante: lo asignaremos en el Service
    Horario toEntity(HorarioRequestDto dto);

    // 3. Listas
    List<HorarioResponseDto> toResponseDtoList(List<Horario> horarios);
}