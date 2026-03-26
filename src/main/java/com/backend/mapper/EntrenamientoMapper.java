package com.backend.mapper;

import org.mapstruct.Mapper;
import com.backend.domain.Entrenamiento;
import com.backend.dto.EntrenamientoResponseDto;

/**
 * Mapper de MapStruct para la entidad {@link Entrenamiento}.
 * <p>
 * Convierte entre la entidad Entrenamiento y sus DTOs de respuesta.
 * </p>
 *
 * @author Backend Team
 * @version 1.0
 * @since 2026
 */
@Mapper(componentModel = "spring")
public interface EntrenamientoMapper {

    /**
     * Convierte una entidad Entrenamiento a su DTO de respuesta.
     *
     * @param entrenamiento la entidad a convertir
     * @return el DTO de respuesta
     */
    EntrenamientoResponseDto entrenamientoToEntrenamientoDTO(Entrenamiento entrenamiento);
}
