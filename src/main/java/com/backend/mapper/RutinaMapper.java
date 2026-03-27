package com.backend.mapper;

import com.backend.domain.Rutina;
import com.backend.dto.RutinaRequestDto;
import com.backend.dto.RutinaResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RutinaMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "usuario", ignore = true)     // Se asigna en el Service
    @Mapping(target = "entrenamiento", ignore = true) // Se asigna en el Service
    Rutina toEntity(RutinaRequestDto requestDto);

    @Mapping(target = "usuarioId", source = "usuario.id")
    @Mapping(target = "username", source = "usuario.username")
    @Mapping(target = "entrenamientoId", source = "entrenamiento.id")
    @Mapping(target = "intensidadEntrenamiento", source = "entrenamiento.intensidad")
    @Mapping(target = "duracion", source = "entrenamiento.duracion")
    RutinaResponseDto toResponseDto(Rutina rutina);
}