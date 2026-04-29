package com.backend.service;

import com.backend.dto.RutinaRequestDto;
import com.backend.dto.RutinaResponseDto;
import java.time.LocalDate;
import java.util.List;

public interface IRutinaService {
    RutinaResponseDto asignarEntrenamiento(RutinaRequestDto dto);

    List<RutinaResponseDto> obtenerRutinaDiaria(Long usuarioId, LocalDate fecha);

    RutinaResponseDto marcarComoCompletada(Long id);
List<RutinaResponseDto> obtenerPorRango(Long usuarioId, LocalDate inicio, LocalDate fin);
    void eliminarAsignacion(Long id);
}