//Paquete
package com.backend.mapper;
//Imports
import org.mapstruct.Mapper;

import com.backend.domain.Entrenamiento;
import com.backend.dto.EntrenamientoResponseDto;

@Mapper(componentModel = "spring")
public interface EntrenamientoMapper {

    EntrenamientoResponseDto entrenamientoToEntrenamientoDTO(Entrenamiento entrenamiento);
    
} 
