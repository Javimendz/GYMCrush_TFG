package com.backend.service;

import java.util.List;

import com.backend.dto.NutricionResponseDto;
import com.backend.dto.PerfilFisicoRequest;
import com.backend.dto.SugerenciaDietaReponseDto;

public interface INutricionService{

    SugerenciaDietaReponseDto generarPlanAutomatico(PerfilFisicoRequest request);
    NutricionResponseDto obtenerUltimoPlanPorUsuario(Long usuarioId);
    List<SugerenciaDietaReponseDto> obtenerHistorial(Long usuarioId);

} 