package com.backend.service;

import com.backend.dto.EntrenamientoRequestDto;
import com.backend.dto.EntrenamientoResponseDto;
import java.util.List;

public interface IEntrenamientoService {
    EntrenamientoResponseDto crear(EntrenamientoRequestDto dto);
    EntrenamientoResponseDto actualizar(Long id, EntrenamientoRequestDto dto);
    EntrenamientoResponseDto obtenerPorId(Long id);
    List<EntrenamientoResponseDto> listarPorUsuario(Long usuarioId);
    void eliminar(Long id);
}