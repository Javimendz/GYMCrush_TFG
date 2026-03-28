package com.backend.service;

import com.backend.dto.DietaRequestDto;
import com.backend.dto.DietaResponseDto;
import java.util.List;

public interface IDietaService {
   
    DietaResponseDto crearDieta(DietaRequestDto dto);
    List<DietaResponseDto> listarTodas();
    DietaResponseDto obtenerPorId(Long id);
    void eliminar(Long id);
}