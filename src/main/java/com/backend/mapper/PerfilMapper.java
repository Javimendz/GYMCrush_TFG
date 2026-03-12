package com.backend.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.backend.domain.Perfil;
import com.backend.dto.PerfilRequestDto;
import com.backend.dto.PerfilResponseDto;

@Mapper(componentModel = "spring")
public interface PerfilMapper {


    //Mapeo para la salida, de Entidad a DTO
    
    PerfilResponseDto toDto(Perfil perfil);

    //Mapeo para las listas para el perfil
    List<PerfilResponseDto> toDtoList(List<Perfil> perfiles);
    //Metodo para actualizar una Entidad existente
    //actualiza perfil
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "usuario", ignore = true)
    @Mapping(target = "fechaNacimiento", ignore = true)
    void updatePerfilFromDto(PerfilRequestDto dto, @MappingTarget Perfil perfil);
}
