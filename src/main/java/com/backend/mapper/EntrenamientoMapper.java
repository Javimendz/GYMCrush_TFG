package com.backend.mapper;

import com.backend.domain.Entrenamiento;
import com.backend.dto.EntrenamientoRequestDto;
import com.backend.dto.EntrenamientoResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Mapper(componentModel = "spring")
public interface EntrenamientoMapper {

    /**
     * De DTO a Entidad
     * Ignoramos 'usuario' y 'rutinas' porque se gestionan en la lógica del Service
     */
    @Mapping(target = "usuario", ignore = true)
    @Mapping(target = "id", ignore = true)
     @Mapping(target= "rutinas", ignore = true)
    Entrenamiento toEntity(EntrenamientoRequestDto dto);

    @Mapping(target = "usuarioId", source = "usuario.id")
    @Mapping(target = "nombreUsuario", source = "usuario.username") // Sacamos el username de la entidad Usuario
    @Mapping(target = "cantidadEjercicios", expression = "java(entrenamiento.getRutinas() != null ? entrenamiento.getRutinas().size() : 0)")
    EntrenamientoResponseDto toResponseDto(Entrenamiento entrenamiento);

    /**
     * Método de utilidad para convertir LocalDateTime a LocalTime 
     * (Evita el error de compilación si el DTO enviara un LocalDateTime en el campo hora)
     */
    default LocalTime map(LocalDateTime value) {
        return value != null ? value.toLocalTime() : null;
    }
}