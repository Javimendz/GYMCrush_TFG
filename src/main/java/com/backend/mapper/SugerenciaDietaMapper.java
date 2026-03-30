package com.backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.backend.domain.SugerenciaDieta;
import com.backend.dto.SugerenciaDietaReponseDto;

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
    SugerenciaDietaReponseDto toResponseDto(SugerenciaDieta sugerencia);
}
