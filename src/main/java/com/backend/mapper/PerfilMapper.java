package com.backend.mapper;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import com.backend.domain.Perfil;
import com.backend.domain.Role;
import com.backend.dto.PerfilRequestDto;
import com.backend.dto.PerfilResponseDto;

@Mapper(componentModel = "spring")
public interface PerfilMapper {


    //Mapeo para la salida, de Entidad a DTO
    @Mapping(target = "username", source = "usuario.username")
    @Mapping(target = "correo", source = "usuario.correo")
    @Mapping(target = "rol", source = "usuario.rol", qualifiedByName = "mapRolesToStrings")
    @Mapping(target = "peso", ignore = true)
    @Mapping(target = "estatura", ignore = true)
    @Mapping(target = "imc", ignore = true)
    @Mapping(target = "nivelActividad", ignore = true)
    PerfilResponseDto toDto(Perfil perfil);

    //Mapeo para las listas para el perfil
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "usuario", ignore = true)
    void updatePerfilFromDto(PerfilRequestDto dto, @MappingTarget Perfil perfil);

    List<PerfilResponseDto> toDtoList(List<Perfil> perfiles);

    @Named("mapRolesToStrings")
    default List<String> mapRolesToStrings(Set<Role> roles) {
        if (roles == null) return Collections.emptyList();
        return roles.stream().map(Role::getName).collect(Collectors.toList());
    }

    
}
