package com.backend.service;

import java.util.List;

import com.backend.dto.NutricionResponseDto;
import com.backend.dto.PerfilFisicoRequest;

public interface INutricionService{

    NutricionResponseDto generarPlanAutomatico(PerfilFisicoRequest request);
    NutricionResponseDto obtenerUltimoPlanPorUsuario(Long usuarioId);
    List<NutricionResponseDto> obtenerHistorial(Long usuarioId);

} 
