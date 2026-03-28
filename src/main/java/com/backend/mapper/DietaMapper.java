package com.backend.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import com.backend.domain.Dieta;
import com.backend.dto.DietaRequestDto;
import com.backend.dto.DietaResponseDto;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DietaMapper {

    // Entidad -> ResponseDto (Para listar dietas)
    DietaResponseDto toResponseDto(Dieta dieta);

    List<DietaResponseDto> toResponseDtoList(List<Dieta> dietas);

    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "sugerencias", ignore = true)
    Dieta toEntityCreate(DietaRequestDto dto);

    // Actualizar entidad existente
    @Mapping(target = "id", ignore = true)
    void updateDietaFromDto(DietaRequestDto dto, @MappingTarget Dieta dieta);
}
