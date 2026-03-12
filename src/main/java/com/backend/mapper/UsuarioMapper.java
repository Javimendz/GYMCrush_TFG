package com.backend.mapper;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;

import com.backend.domain.Role;
import com.backend.domain.Usuario;
import com.backend.dto.PerfilSummaryDto;
import com.backend.dto.RoleDto;
import com.backend.dto.UsuarioRequestDto;
import com.backend.dto.UsuarioResponseDto;
import com.backend.exceptions.ResourceNotFoundException;
import com.backend.repository.RoleRepository;
import com.backend.security.dto.RegisterDto;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class UsuarioMapper {

    @Autowired
    protected RoleRepository roleRepository;

    // De DTO plano a Entidad con Perfil anidado, Registro
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

    // Actualizacion, De DTO plano a Entidad existente
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "rol", source = "rol", qualifiedByName = "mapStringsToRoles")
    @Mapping(target = "perfil.nombre", source = "nombre")
    @Mapping(target = "perfil.apellidos", source = "apellidos")
    @Mapping(target = "perfil.telefono", source = "telefono")
    @Mapping(target = "perfil.direccion", source = "direccion")
    @Mapping(target = "perfil.ciudad", source = "ciudad")
    @Mapping(target = "perfil.pais", source = "pais")
    @Mapping(target = "perfil.codigoPostal", source = "codigoPostal")
    public abstract void updateUsuarioFromDto(UsuarioRequestDto dto, @MappingTarget Usuario usuario);

    // Respuesta Individual: De Entidad (con Perfil) a DTO plano
    @Mapping(target = "rol", source = "rol") 
    @Mapping(target = "nombre", source = "perfil.nombre")
    @Mapping(target = "apellidos", source = "perfil.apellidos")
    @Mapping(target = "dni", source = "perfil.dni") 
    @Mapping(target = "telefono", source = "perfil.telefono")
    @Mapping(target = "direccion", source = "perfil.direccion")
    @Mapping(target = "ciudad", source = "perfil.ciudad")
    @Mapping(target = "pais", source = "perfil.pais")
    @Mapping(target = "codigoPostal", source = "perfil.codigoPostal")
    @Mapping(target = "foto", source = "perfil.foto") 
    @Mapping(target = "bio", source = "perfil.bio")   
    public abstract UsuarioResponseDto toUsuarioResponseDto(Usuario usuario);

    public abstract List<UsuarioResponseDto> toUsuarioResponseDtoList(List<Usuario> usuarios);

    @Mapping(target = "rol", source = "rol", qualifiedByName = "mapStringsToRoles")
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "id", ignore = true)
    public abstract Usuario toEntity(UsuarioRequestDto dto);

    // Mapeo para el resumen (Summary)
    @Mapping(target = "nombre", source = "perfil.nombre")
    @Mapping(target = "apellidos", source = "perfil.apellidos")
    @Mapping(target = "foto", source = "perfil.foto")
    public abstract PerfilSummaryDto toSummaryDto(Usuario usuario);

    // Metodo para convertir listas de usuarios a resumenes, para el admin sobre todos
    public abstract List<PerfilSummaryDto> toSummaryDtoList(List<Usuario> usuarios);

    // Ayuda a convertir la entidad Role al DTO creado
    //  El campo en RoleDto debe ser 'nombre' 
    @Mapping(target = "nombre", source = "name") 
    public abstract RoleDto roleToRoleDto(Role role);

    // Este método convierte la colección completa de Roles a DTOs para el UsuarioResponseDto
    public abstract List<RoleDto> mapRoles(Set<Role> roles);

    // Convertidor: Nombres de Roles (Strings) a Entidades Role
    @Named("mapStringsToRoles")
    public Set<Role> mapStringsToRoles(Set<String> roleNames) {
        if (roleNames == null || roleNames.isEmpty()) {
            return roleRepository.findByName("ROLE_USUARIO")
                    .map(Collections::singleton)
                    .orElseThrow(() -> new ResourceNotFoundException("Error: Rol por defecto no encontrado"));
        }
        return roleNames.stream()
                .map(roleName -> roleRepository.findByName(roleName)
                        .orElseThrow(() -> new ResourceNotFoundException("Role no encontrado: " + roleName)))
                .collect(Collectors.toSet());
    }

    // Convertidor: Entidades Role a Nombres de Roles (Strings) 
    @Named("mapRolesToStrings")
    public Set<String> mapRolesToStrings(Set<Role> roles) {
        if (roles == null) {
            return Collections.emptySet();
        }
        return roles.stream()
                .map(Role::getName)
                .collect(Collectors.toSet());
    }
}