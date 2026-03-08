package com.backend.mapper;

import org.mapstruct.Mapper;
import com.backend.domain.Perfil;
import com.backend.dto.PerfilResponseDto;

@Mapper(componentModel = "spring")
public interface PerfilMapper {

    PerfilResponseDto perfilToPerfilDTO(Perfil perfil);
}
