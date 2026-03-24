package com.backend.service;

import com.backend.dto.CategoriaTutorialRequestDto;
import com.backend.dto.CategoriaTutorialResponseDto;
import java.util.List;

public interface ICategoriaTutorialService {
    List<CategoriaTutorialResponseDto> listarTodas();
    CategoriaTutorialResponseDto crear(CategoriaTutorialRequestDto dto);
    CategoriaTutorialResponseDto buscarPorId(Long id);
    void eliminar(Long id);
}