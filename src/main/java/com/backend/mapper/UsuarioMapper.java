//Paquete
package com.backend.mapper;
//Imports
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.backend.domain.Role;
import com.backend.domain.Usuario;
import com.backend.dto.RegisterDto;
import com.backend.dto.UsuarioRequestDto;
import com.backend.dto.UsuarioResponseDto;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE) // Indica que se usara Spring para inyectar las dependencias (Me genera todo el codigo x la cara D:)
public interface UsuarioMapper {
    
    @Mapping(target = "password", ignore = true) // El campo contraseña lo tenemos que transformar
    @Mapping(target = "id", ignore = true) //El id no deberia venir
    @Mapping(target = "rol", expression = "java(stringSetToRoleSet(usuarioRequestDto.getRol()))")
    Usuario toEntity(UsuarioRequestDto usuarioRequestDto);

    UsuarioResponseDto toUsuarioResponseDto(Usuario usuario);

    List<UsuarioResponseDto> toUsuarioResponseDtoList(List<Usuario> usuarios); //Evita hacer el bucle en el servicio a mano
    
    // Método auxiliar para convertir Set<String> a Set<Role>
    default Set<Role> stringSetToRoleSet(Set<String> roleNames) {
        if (roleNames == null) {
            return null;
        }
        return roleNames.stream()
                .map(roleName -> {
                    Role role = new Role();
                    role.setName(roleName);
                    return role;
                })
                .collect(Collectors.toSet());
    }

    void  updateUsuarioFromDto(UsuarioRequestDto usuarioRequestDto,@MappingTarget Usuario usuario);
    
    @Mapping(target = "password", ignore = true) // El campo contraseña lo tenemos que transformar
    @Mapping(target = "id", ignore = true) //El id no deberia venir
    @Mapping(target = "rol", ignore = true) // Se maneja en AuthController
    Usuario registerDtoToUser(RegisterDto registerDto);

}
