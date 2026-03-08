package com.backend.mapper;

import org.mapstruct.Mapper;
import com.backend.domain.SugerenciaDieta;
import com.backend.dto.SugerenciaDietaReponseDto;

@Mapper(componentModel = "spring")
public interface SugerenciaDietaMapper {

    SugerenciaDietaReponseDto sugerenciaDietaToSugerenciaDietaDTO(SugerenciaDieta sugerenciaDieta);
    
    
}
