package com.backend.mapper;

import com.backend.domain.Entrenamiento;
import com.backend.dto.EntrenamientoRequestDto;
import com.backend.dto.EntrenamientoResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.time.LocalDateTime;
import java.time.LocalTime;
import com.backend.domain.Tutorial;

@Mapper(componentModel = "spring")
public interface EntrenamientoMapper {

   /**
     * De DTO a Entidad (Para crear el Entrenamiento inicial)
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tutoriales", ignore = true) 
    Entrenamiento toEntity(EntrenamientoRequestDto dto);

    /**
     *  Combina Entrenamiento y Tutorial en una sola respuesta
     * Esto es lo que soluciona que la URL y Descripción lleguen al móvil.
     */
    @Mapping(target = "id", source = "entrenamiento.id")
    @Mapping(target = "nombre", source = "entrenamiento.nombre")
    @Mapping(target = "descripcion", source = "tutorial.descripcion") 
    @Mapping(target = "urlVideo", source = "tutorial.urlVideo")       
    EntrenamientoResponseDto toResponseDto(Entrenamiento entrenamiento, Tutorial tutorial);

    /**
     * Método de utilidad (Mantenido por si tu DTO usa fechas)
     */
    default LocalTime map(LocalDateTime value) {
        return value != null ? value.toLocalTime() : null;
    }
}