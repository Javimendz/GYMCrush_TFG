//Paquete
package com.backend.mapper;
//Imports
import java.util.List;
import org.mapstruct.Mapper;
import com.backend.domain.Salud;
import com.backend.dto.SaludResponseDto;

@Mapper(componentModel = "spring")
public interface SaludMapper {

    List<SaludResponseDto> saludToSaludResponseDtoList(List<Salud> saluds);
}
