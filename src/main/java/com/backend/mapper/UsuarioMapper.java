//Paquete
package com.backend.mapper;
//Imports
import java.util.List;
import org.mapstruct.Mapper;
import com.backend.domain.Usuario;
import com.backend.dto.UsuarioRequestDto;
import com.backend.dto.UsuarioResponseDto;

@Mapper(componentModel = "spring") // Indica que se usara Spring para inyectar las dependencias (Me genera todo el codigo x la cara D:)
public interface UsuarioMapper {
    
    Usuario toEntity(UsuarioRequestDto usuarioRequestDto);

    UsuarioResponseDto toUsuarioResponseDto(Usuario usuario);

    List<UsuarioResponseDto> toUsuarioResponseDtoList(List<Usuario> usuarios);
    
}
