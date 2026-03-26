package com.backend.mapper;

import java.util.List;
import com.backend.domain.Dieta;
import com.backend.dto.DietaResponseDto;

import org.mapstruct.Mapper;

/**
 * Mapper de MapStruct para la entidad {@link Dieta}.
 * <p>
 * Convierte entre la entidad Dieta y sus DTOs de respuesta.
 * </p>
 *
 * @author Backend Team
 * @version 1.0
 * @since 2026
 */
@Mapper(componentModel = "spring")
public interface DietaMapper {

    /**
     * Convierte una lista de entidades Dieta a lista de DTOs de respuesta.
     *
     * @param dietas la lista de entidades a convertir
     * @return la lista de DTOs de respuesta
     */
    List<DietaResponseDto> dietasToDietaDTOs(List<Dieta> dietas);
}
