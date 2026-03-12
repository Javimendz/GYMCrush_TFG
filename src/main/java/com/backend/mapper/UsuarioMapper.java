//Paquete
package com.backend.mapper;

//Imports
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
import com.backend.dto.UsuarioRequestDto;
import com.backend.dto.UsuarioResponseDto;
import com.backend.repository.RoleRepository;
import com.backend.security.dto.RegisterDto;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public abstract class UsuarioMapper {

    @Autowired
    protected RoleRepository roleRepository;

    @Mapping(target = "password", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "rol", source = "registerDto.rol", qualifiedByName = "mapRoleNamesToRoles")
    public abstract Usuario registerDtoToUser(RegisterDto registerDto);

    @Named("mapRoleNamesToRoles") // Definimos una etiqueta para este metodo indicando que se llama asi para
                                  // usarlo en una conversion especial
    public Set<Role> mapRoleNamesToRoles(Set<String> roleNames) {
        // Evaluamos si tenemos roles, sino tenemos, asignamos el rol por defecto, sino
        // esta lanzamos error
        if (roleNames == null || roleNames.isEmpty()) {
            return roleRepository.findByName("ROLE_USUARIO")
                    .map(Collections::singleton)
                    .orElseThrow(() -> new RuntimeException("Error: Rol por defecto no encontrado"));
        }
        return roleNames.stream()
                .map(roleName -> roleRepository.findByName(roleName)
                        .orElseThrow(() -> new RuntimeException("Role no encontrado")))
                .collect(Collectors.toSet());
    }
    @Mapping(target = "rol", source = "dto.rol", qualifiedByName = "mapRoleNamesToRoles")
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "id", ignore = true)
    public abstract void updateUsuarioFromDto(UsuarioRequestDto dto, @MappingTarget Usuario usuario);

    @Mapping(target = "rol", source="dto.rol", qualifiedByName = "mapRoleNamesToRoles")
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "id", ignore = true)
    public abstract Usuario toEntity(UsuarioRequestDto dto);

    @Mapping(target = "password", ignore = true)
    public abstract List<UsuarioResponseDto> toUsuarioResponseDtoList(List<Usuario> usuarios);
   

    public abstract UsuarioResponseDto toUsuarioResponseDto(Usuario usuario);
    
    @Named("mapRolesToRoleNames")
    public Set<String> mapRolesToRoleNames(Set<Role> roles) {
        if (roles == null) {
            return Collections.emptySet();
        }
        return roles.stream()
                .map(Role::getName)
                .collect(Collectors.toSet());
    }
}
