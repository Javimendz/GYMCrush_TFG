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
    @Mapping(target = "id", source = "usuario.perfil.id")
    // Datos de la entidad Perfil
    @Mapping(target = "nombre", source = "usuario.perfil.nombre")
    @Mapping(target = "apellidos", source = "usuario.perfil.apellidos")
    @Mapping(target = "telefono", source = "usuario.perfil.telefono")
    @Mapping(target = "dni", source = "usuario.perfil.dni")
    @Mapping(target = "foto", source = "usuario.perfil.foto")
    @Mapping(target = "genero", source = "usuario.perfil.genero")
    @Mapping(target = "fechaNacimiento", source = "usuario.perfil.fechaNacimiento")
    
    // Datos de la entidad Salud (Aquí está el truco)
   // @Mapping(target = "peso", source = "usuario.salud.peso")
    //@Mapping(target = "estatura", source = "usuario.salud.estatura")
    //@Mapping(target = "nivelActividad", source = "usuario.salud.nivelActividad")
    //@Mapping(target = "imc", source = "usuario.salud.imc")
    //temporales los datos de abjo
    @Mapping(target = "peso", ignore = true)
    @Mapping(target = "estatura", ignore = true)
    @Mapping(target = "nivelActividad", ignore = true)
    @Mapping(target = "imc", ignore = true)
    // Datos de la cuenta de Usuario
    @Mapping(target = "username", source = "usuario.username")
    @Mapping(target = "correo", source = "usuario.correo")
    @Mapping(target = "rol", source = "usuario.rol", qualifiedByName = "mapRolesToStrings")
    PerfilResponseDto toDto(Perfil perfil);

    //Mapeo para las listas para el perfil
    
    List<PerfilResponseDto> toDtoList(List<Perfil> perfiles);
    //Metodo para actualizar una Entidad existente
    //actualiza perfil
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "usuario", ignore = true)
    @Mapping(target = "fechaNacimiento", ignore = true)
    void updatePerfilFromDto(PerfilRequestDto dto, @MappingTarget Perfil perfil);

    @Named("mapRolesToStrings")
default List<String> mapRolesToStrings(Set<Role> roles) {
    if (roles == null) return Collections.emptyList();
    return roles.stream().map(Role::getName).collect(Collectors.toList());
}
}
