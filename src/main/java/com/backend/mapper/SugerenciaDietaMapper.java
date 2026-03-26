package com.backend.mapper;

import org.mapstruct.Mapper;
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

    /**
     * Convierte una entidad SugerenciaDieta a su DTO de respuesta.
     *
     * @param sugerenciaDieta la entidad a convertir
     * @return el DTO de respuesta
     */
    SugerenciaDietaReponseDto sugerenciaDietaToSugerenciaDietaDTO(SugerenciaDieta sugerenciaDieta);
}
