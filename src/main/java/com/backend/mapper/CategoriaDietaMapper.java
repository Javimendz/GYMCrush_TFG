package com.backend.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.backend.domain.CategoriaDieta;
import com.backend.dto.CategoriaDietaRequestDto;
import com.backend.dto.CategoriaDietaResponseDto;

@Mapper(componentModel = "spring")
public interface CategoriaDietaMapper {
    CategoriaDietaResponseDto toResponseDto(CategoriaDieta entidad);
    List<CategoriaDietaResponseDto> toResponseDtoList(List<CategoriaDieta> entidades);
    CategoriaDieta toEntity(CategoriaDietaRequestDto dto);
}
