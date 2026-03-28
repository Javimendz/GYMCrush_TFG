package com.backend.mapper;

import com.backend.domain.SugerenciaDieta;
import com.backend.dto.SugerenciaDietaReponseDto;
import com.backend.dto.SugerenciaRequestDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;

@Mapper(componentModel = "spring")
public interface SugerenciaMapper {

    // Entidad -> DTO (Para enviar a Android)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "detalles", source = "macronutrientesJson")
   
    @Mapping(target = "usuarioId", source = "usuario.id") 
    @Mapping(target = "dietaId", source = "dieta.id")
    SugerenciaDietaReponseDto toResponseDto(SugerenciaDieta sugerencia);

    List<SugerenciaDietaReponseDto> toResponseDtoList(List<SugerenciaDieta> sugerencias);

    // DTO -> Entidad (Para guardar en BD)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fechaGeneracion", ignore = true)
    @Mapping(target = "usuario.id", source = "usuarioId")
    @Mapping(target = "dieta.id", source = "dietaId")
    @Mapping(target = "macronutrientesJson", source = "macronutrientesJson")
    SugerenciaDieta toEntityCreate(SugerenciaRequestDto dto);
}
