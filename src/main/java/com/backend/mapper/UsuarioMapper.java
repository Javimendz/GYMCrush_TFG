
package com.backend.mapper;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

import com.backend.domain.Role;
import com.backend.domain.Usuario;
import com.backend.dto.PerfilSummaryDto;
import com.backend.dto.UsuarioRequestDto;
import com.backend.dto.UsuarioResponseDto;
import com.backend.exceptions.*;
import com.backend.repository.RoleRepository;
import com.backend.security.dto.RegisterDto;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public abstract class UsuarioMapper {

    @Autowired
    protected RoleRepository roleRepository;

    // REGISTRO: De DTO a Entidad (con Perfil anidado)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "rol", source = "rol", qualifiedByName = "mapStringsToRoles")
    @Mapping(target = "perfil.nombre", source = "nombre")
    @Mapping(target = "perfil.apellidos", source = "apellidos")
    @Mapping(target = "perfil.telefono", source = "telefono")
    @Mapping(target = "perfil.dni", source = "dni")
    @Mapping(target = "perfil.direccion", source = "direccion")
    @Mapping(target = "perfil.ciudad", source = "ciudad")
    @Mapping(target = "perfil.pais", source = "pais")
    @Mapping(target = "perfil.codigoPostal", source = "codigoPostal")
    @Mapping(target = "perfil.fechaNacimiento", source = "fechaNacimiento")
    @Mapping(target = "perfil.genero", source = "genero")
    public abstract Usuario registerDtoToUser(RegisterDto registerDto);

    // RESPUESTA INDIVIDUAL
    @Mapping(target = "nombre", source = "perfil.nombre")
    @Mapping(target = "apellidos", source = "perfil.apellidos")
    @Mapping(target = "dni", source = "perfil.dni")
    @Mapping(target = "telefono", source = "perfil.telefono")
    @Mapping(target = "foto", source = "perfil.foto")
    @Mapping(target = "bio", source = "perfil.bio")
    @Mapping(target = "direccion", source = "perfil.direccion")
@Mapping(target = "ciudad", source = "perfil.ciudad")
@Mapping(target = "pais", source = "perfil.pais")
@Mapping(target = "codigoPostal", source = "perfil.codigoPostal")
    public abstract UsuarioResponseDto toUsuarioResponseDto(Usuario usuario);

    // Solo lo que PerfilSummaryDto realmente contiene
    @Mapping(target = "username", source = "username")
    @Mapping(target = "nombre", source = "perfil.nombre")
    @Mapping(target = "apellidos", source = "perfil.apellidos")
    @Mapping(target = "foto", source = "perfil.foto")
    // No mapeamos correo/rol si no están en el DTO para evitar errores
    public abstract PerfilSummaryDto toSummaryDto(Usuario usuario);

    public abstract List<PerfilSummaryDto> toSummaryDtoList(List<Usuario> usuarios);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true) // La contraseña se suele gestionar aparte con un Encoder
    @Mapping(target = "rol", source = "rol", qualifiedByName = "mapStringsToRoles")
    @Mapping(target = "perfil.nombre", source = "nombre")
    @Mapping(target = "perfil.apellidos", source = "apellidos")
    @Mapping(target = "perfil.telefono", source = "telefono")
    @Mapping(target = "perfil.direccion", source = "direccion")
    @Mapping(target = "perfil.ciudad", source = "ciudad")
    @Mapping(target = "perfil.pais", source = "pais")
    @Mapping(target = "perfil.codigoPostal", source = "codigoPostal")
    public abstract Usuario toEntity(UsuarioRequestDto dto);

    @Mapping(target = "id", ignore = true) // No queremos cambiar el ID
    @Mapping(target = "password", ignore = true) // La contraseña se gestiona aparte
    @Mapping(target = "rol", source = "rol", qualifiedByName = "mapStringsToRoles")
    @Mapping(target = "perfil.nombre", source = "nombre")
    @Mapping(target = "perfil.apellidos", source = "apellidos")
    @Mapping(target = "perfil.telefono", source = "telefono")
    @Mapping(target = "perfil.direccion", source = "direccion")
    @Mapping(target = "perfil.ciudad", source = "ciudad")
    @Mapping(target = "perfil.pais", source = "pais")
    @Mapping(target = "perfil.codigoPostal", source = "codigoPostal")
    @Mapping(target = "perfil.fechaNacimiento", source = "fechaNacimiento")
@Mapping(target = "perfil.genero", source = "genero")
    public abstract void updateUsuarioFromDto(UsuarioRequestDto dto, @MappingTarget Usuario usuario);

    public abstract List<UsuarioResponseDto> toUsuarioResponseDtoList(List<Usuario> usuarios);

    // CONVERTIDORES DE ROLES
    @Named("mapStringsToRoles")
    public Set<Role> mapStringsToRoles(Set<String> roleNames) {
        if (roleNames == null || roleNames.isEmpty()) {
            return roleRepository.findByName("ROLE_USUARIO")
                    .map(Collections::singleton)
                    .orElseThrow(() -> new ResourceNotFoundException("Error: Rol por defecto no encontrado"));
        }
        return roleNames.stream()
                .map(name -> roleRepository.findByName(name)
                        .orElseThrow(() -> new ResourceNotFoundException("Role no encontrado: " + name)))
                .collect(Collectors.toSet());
    }
}