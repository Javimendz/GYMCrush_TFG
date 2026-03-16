//Paquete
package com.backend.mapper;
//Imports
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.backend.domain.Salud;
import com.backend.dto.SaludRequestDto;
import com.backend.dto.SaludResponseDto;

@Mapper(componentModel = "spring")
public interface SaludMapper {

    @Mapping(target = "username", source = "usuario.username")
    SaludResponseDto toDto(Salud salud);
    
    @Mapping(target = "usuario", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fechaMedicion", ignore = true)
    @Mapping(target = "imc", ignore = true)
    Salud toEntity(SaludRequestDto dto);


    List<SaludResponseDto> toDtoList(List<Salud> saludList);
}
