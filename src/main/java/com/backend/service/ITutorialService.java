package com.backend.service;

import com.backend.dto.CategoriaTutorialResponseDto;
import com.backend.dto.TutorialRequestDto;
import com.backend.dto.TutorialResponseDto;
import java.util.List;

public interface ITutorialService {
    List<TutorialResponseDto> obtenerTodos();
    List<TutorialResponseDto> obtenerPorCategoria(Long categoriaId);
    TutorialResponseDto crear(TutorialRequestDto dto);
    void eliminar(Long id);
    List<CategoriaTutorialResponseDto> listarCategorias();
}