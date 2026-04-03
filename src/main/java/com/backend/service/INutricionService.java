package com.backend.service;

import com.backend.dto.NutricionResponseDto;
import com.backend.dto.PerfilFisicoRequest;
import com.backend.dto.SugerenciaDietaReponseDto;

public interface INutricionService{

    SugerenciaDietaReponseDto generarPlanAutomatico(PerfilFisicoRequest request);
    NutricionResponseDto obtenerUltimoPlanPorUsuario(Long usuarioId);
} 
