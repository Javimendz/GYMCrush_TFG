package com.backend.mapper;

import com.backend.domain.Rutina;
import com.backend.dto.RutinaRequestDto;
import com.backend.dto.RutinaResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RutinaMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "usuario", ignore = true)
    @Mapping(target = "entrenamiento", ignore = true)
    Rutina toEntity(RutinaRequestDto dto);

    // Mapeo de salida: Entidad Rutina -> RutinaResponseDto
    @Mapping(target = "entrenamientoId", source = "entrenamiento.id")
    @Mapping(target = "nombreEntrenamiento", source = "entrenamiento.nombre")
    @Mapping(target = "intensidad", source = "entrenamiento.intensidad")
    @Mapping(target = "duracion", source = "entrenamiento.duracion")
    RutinaResponseDto toResponseDto(Rutina rutina);
}