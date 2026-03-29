package com.backend.mapper;

import com.backend.domain.SugerenciaDieta;
import com.backend.dto.SugerenciaDietaReponseDto;
import com.backend.dto.SugerenciaRequestDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;

@Mapper(componentModel = "spring")
public interface SugerenciaMapper {
    @Mapping(target = "id", source = "id")
    @Mapping(target = "detalles", source = "macronutrientesJson")
    @Mapping(target = "usuarioId", source = "usuario.id") 
    @Mapping(target = "dietaId", source = "dieta.id")
    SugerenciaDietaReponseDto toResponseDto(SugerenciaDieta sugerencia);

    List<SugerenciaDietaReponseDto> toResponseDtoList(List<SugerenciaDieta> sugerencias);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fechaGeneracion", ignore = true)
    @Mapping(target = "usuario.id", source = "usuarioId")
    @Mapping(target = "dieta.id", source = "dietaId")
    SugerenciaDieta toEntity(SugerenciaRequestDto requestDto);
}
