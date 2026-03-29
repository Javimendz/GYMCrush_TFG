package com.backend.service;

import com.backend.dto.CategoriaDietaRequestDto;
import com.backend.dto.CategoriaDietaResponseDto;
import java.util.List;

public interface ICategoriaDietaService {
    CategoriaDietaResponseDto crear(CategoriaDietaRequestDto dto);
    List<CategoriaDietaResponseDto> listarTodas();
    CategoriaDietaResponseDto obtenerPorId(Long id);
    void eliminar(Long id);
}