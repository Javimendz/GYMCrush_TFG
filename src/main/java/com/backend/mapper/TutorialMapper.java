package com.backend.mapper;

import com.backend.domain.Tutorial;
import com.backend.dto.TutorialRequestDto;
import com.backend.dto.TutorialResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TutorialMapper {

    @Mapping(target = "categoriaId", source = "categoria.id")
    @Mapping(target = "nombreCategoria", source = "categoria.nombre")
    TutorialResponseDto toDto(Tutorial entity);

    @Mapping(target = "categoria", ignore = true) // Se busca el objeto Categoria en el Service
    Tutorial toEntity(TutorialRequestDto dto);
}