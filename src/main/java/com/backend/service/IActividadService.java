package com.backend.service;

import java.util.List;

import com.backend.dto.ActividadResponseDto;

public interface IActividadService {
    List<ActividadResponseDto> listarTodos();
    ActividadResponseDto buscarPorId(Long id); 
    ActividadResponseDto crear(ActividadResponseDto dto);
    ActividadResponseDto actualizar(Long id, ActividadResponseDto dto);
    void eliminar(Long id);
    List<ActividadResponseDto> buscarPorNombre(String nombre);
    List<ActividadResponseDto> filtrarPorPrecioMaximo(Integer precio);
    List<ActividadResponseDto> listarPorDia(String dia);
}
