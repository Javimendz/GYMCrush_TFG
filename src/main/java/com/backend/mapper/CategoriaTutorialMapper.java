package com.backend.mapper;

import com.backend.domain.CategoriaTutorial;
import com.backend.dto.CategoriaTutorialRequestDto;
import com.backend.dto.CategoriaTutorialResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CategoriaTutorialMapper {

    // Convertimos la entidad a DTO para enviar a Android
    // Ignoramos la lista de tutoriales aquí para evitar cargas pesadas (Lazy Loading)
    @Mapping(target = "cantidadVideos", expression = "java(entity.getTutoriales() != null ? entity.getTutoriales().size() : 0)")
    CategoriaTutorialResponseDto toDto(CategoriaTutorial entity);

    // Convertimos lo que viene de la App a Entidad para guardar
    @Mapping(target = "tutoriales", ignore = true)
    CategoriaTutorial toEntity(CategoriaTutorialRequestDto dto);
}