package com.backend.service;

import java.util.List;

import com.backend.dto.SugerenciaDietaReponseDto;
import com.backend.dto.SugerenciaDietaRequestDto;

public interface ISugerenciaDietaService {
    SugerenciaDietaReponseDto guardarAsignacion(SugerenciaDietaRequestDto dto);
    List<SugerenciaDietaReponseDto> obtenerHistorial(Long usuarioId);
}