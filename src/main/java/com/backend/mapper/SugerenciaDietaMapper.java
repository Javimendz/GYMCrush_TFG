package com.backend.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.backend.domain.SugerenciaDieta;
import com.backend.dto.SugerenciaDietaReponseDto;
import com.backend.dto.SugerenciaDietaRequestDto;

/**
 * Mapper de MapStruct para la entidad {@link SugerenciaDieta}.
 * <p>
 * Convierte entre la entidad SugerenciaDieta y sus DTOs de respuesta.
 * </p>
 *
 * @author Backend Team
 * @version 1.0
 * @since 2026
 */
@Mapper(componentModel = "spring")
public interface SugerenciaDietaMapper {
    @Mapping(target = "usuarioId", source = "usuario.id")
    @Mapping(target = "dietaId", source = "dieta.id")
    @Mapping(target = "esFavorita", source = "esFavorita")
    SugerenciaDietaReponseDto toResponseDto(SugerenciaDieta sugerencia);

    List<SugerenciaDietaReponseDto> toResponseDtoList(List<SugerenciaDieta> sugerencias);

    
    @Mapping(target = "usuario.id", source = "usuarioId")
    @Mapping(target = "dieta.id", source = "dietaId")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fechaGeneracion", ignore = true)
    @Mapping(target = "nombreDieta", ignore = true) 
    @Mapping(target = "tipoDieta", ignore = true)
    @Mapping(target = "caloriasObjetivo", ignore = true) 
    @Mapping(target = "macronutrientesJson", ignore = true) 
    // Es vital ignorarlo aquí para evitar errores si el DTO de petición no lo trae
    @Mapping(target = "esFavorita", ignore = true)
    SugerenciaDieta toEntity(SugerenciaDietaRequestDto dto);

}
