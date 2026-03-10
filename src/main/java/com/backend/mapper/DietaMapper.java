//Paquete
package com.backend.mapper;
//Imports
import java.util.List;
import com.backend.domain.Dieta;
import com.backend.dto.DietaResponseDto;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DietaMapper {

    List<DietaResponseDto> dietasToDietaDTOs(List<Dieta> dietas);
} 
