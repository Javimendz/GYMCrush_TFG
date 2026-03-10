//Paquete
package com.backend.mapper;
//Imports
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.backend.domain.Usuario;
import com.backend.dto.RegisterDto;
import com.backend.dto.UsuarioRequestDto;
import com.backend.dto.UsuarioResponseDto;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE) // Indica que se usara Spring para inyectar las dependencias (Me genera todo el codigo x la cara D:)
public interface UsuarioMapper {
    
    Usuario toEntity(UsuarioRequestDto usuarioRequestDto);

    UsuarioResponseDto toUsuarioResponseDto(Usuario usuario);

    List<UsuarioResponseDto> toUsuarioResponseDtoList(List<Usuario> usuarios); //Evita hacer el bucle en el servicio a mano

    void  updateUsuarioFromDto(UsuarioRequestDto usuarioRequestDto,@MappingTarget Usuario usuario);
    
    @Mapping(target = "password", ignore = true) // El campo contraseña lo tenemos que transformar
    @Mapping(target = "id", ignore = true) //El id no deberia venir
    Usuario registerDtoToUser(RegisterDto registerDto);

}
