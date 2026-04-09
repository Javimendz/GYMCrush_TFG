package com.backend.mapper;

import com.backend.domain.DetallePlan;
import com.backend.domain.PlanEntrenamiento;
import com.backend.dto.DetallePlanResponseDto;
import com.backend.dto.PlanRequestDto;
import com.backend.dto.PlanResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PlanMapper {

    PlanResponseDto toResponseDto(PlanEntrenamiento entity);

    @Mapping(target = "id", ignore = true) 
    @Mapping(target = "ejercicios", ignore = true)
    PlanEntrenamiento toEntity(PlanRequestDto dto);

    // Este es el método que usa el endpoint de ejercicios
    @Mapping(target = "entrenamientoId", source = "entrenamiento.id")
    @Mapping(target = "nombreEntrenamiento", source = "entrenamiento.nombre")
    @Mapping(target = "descripcionEntrenamiento", source = "entrenamiento.descripcion")
    DetallePlanResponseDto toDetalleResponseDto(DetallePlan entity);
}