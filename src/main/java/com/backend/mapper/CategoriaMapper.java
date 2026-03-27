package com.backend.mapper;

import com.backend.domain.Categoria;
import com.backend.dto.CategoriaRequestDto;
import com.backend.dto.CategoriaResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CategoriaMapper {

    // 1. Cuando haces POST: Conviertes el Request en una Entidad para guardarla en BD
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "actividades", ignore = true)
    Categoria toEntity(CategoriaRequestDto requestDTO);

    @Mapping(target = "id", source = "id")
    CategoriaResponseDto toResponse(Categoria categoria);
}