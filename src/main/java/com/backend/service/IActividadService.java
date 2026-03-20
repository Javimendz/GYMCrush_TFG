package com.backend.service;

import java.util.List;
import com.backend.dto.ActividadRequestDto;
import com.backend.dto.ActividadResponseDto;

public interface IActividadService {

    // Obtener todas las actividades disponibles
    List<ActividadResponseDto> listarTodos();

    //  Buscar una actividad por su id
    ActividadResponseDto buscarPorId(Long id);

    //  Registrar una nueva actividad, solo ADMIN
    ActividadResponseDto crear(ActividadRequestDto dto);

    // Modificar una actividad existente, solo ADMIN
    ActividadResponseDto actualizar(Long id, ActividadRequestDto dto);

    // Dar de baja una actividad, solo ADMIN
    void eliminar(Long id);

    // MÉTODOS DE BÚSQUEDA Y FILTRADO 

    // Buscar actividades por  nombre
    List<ActividadResponseDto> buscarPorNombre(String nombre);

    //  Listar actividades por un día concreto 
    List<ActividadResponseDto> listarPorDia(String dia);

    // Filtrar actividades por presupuesto máximo
    List<ActividadResponseDto> filtrarPorPrecioMaximo(Integer precio);
}