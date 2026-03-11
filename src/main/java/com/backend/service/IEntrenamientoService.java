package com.backend.service;

import com.backend.dto.EntrenamientoResponseDto;
import java.util.List;

public interface IEntrenamientoService {
    
    List<EntrenamientoResponseDto> listarPorUsuario(Long usuarioId);
    List<EntrenamientoResponseDto> listarTodos();
    EntrenamientoResponseDto guardar(EntrenamientoResponseDto dto);
}