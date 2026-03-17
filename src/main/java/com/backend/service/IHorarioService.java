package com.backend.service;

import java.util.List;

import com.backend.dto.HorarioResponseDto;

public interface IHorarioService {
    List<HorarioResponseDto> listarTodos();
    HorarioResponseDto crear(HorarioResponseDto dto);
    HorarioResponseDto actualizar(Long id, HorarioResponseDto dto);
    void eliminar(Long id);
    HorarioResponseDto obtenerPorId(Long id);
    List<HorarioResponseDto> listarPorDia(String dia);

}
