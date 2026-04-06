package com.backend.service;

import com.backend.dto.SugerenciaRequestDto;
import com.backend.dto.SugerenciaDietaReponseDto;
import java.util.List;

public interface ISugerenciaService {
    SugerenciaDietaReponseDto guardarAsignacion(SugerenciaRequestDto dto);
    List<SugerenciaDietaReponseDto> obtenerHistorial(Long usuarioId);
}
