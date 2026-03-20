package com.backend.service;

import java.util.List;

import com.backend.dto.HorarioRequestDto;
import com.backend.dto.HorarioResponseDto;

public interface IHorarioService {
    
/**
     * Retorna todos los horarios registrados en el sistema.
     * @return Lista de DTOs con la información de la actividad incluida.
     */
    List<HorarioResponseDto> listarTodos();

    /**
     * Busca un horario específico por su ID.
     * @param id Identificador único del horario.
     * @return DTO del horario encontrado.
     */
    HorarioResponseDto obtenerPorId(Long id);

    /**
     * Crea un nuevo horario vinculándolo a una actividad existente.
     * @param dto Datos del horario (día, horas, aforo e id de actividad).
     * @return El horario creado y persistido.
     */
    HorarioResponseDto crear(HorarioRequestDto dto);

    /**
     * Actualiza un horario existente.
     * @param id ID del horario a modificar.
     * @param dto Nuevos datos para el horario.
     * @return El horario actualizado.
     */
    HorarioResponseDto actualizar(Long id, HorarioRequestDto dto);

    /**
     * Elimina un horario del sistema.
     * @param id ID del horario a borrar.
     */
    void eliminar(Long id);

    /**
     * Filtra los horarios por un día de la semana específico.
     * @param dia Nombre del día (LUNES, MARTES, etc.).
     * @return Lista de horarios para ese día.
     */
    List<HorarioResponseDto> listarPorDia(String dia);

    /**
     * Obtiene todos los horarios programados para una actividad concreta.
     * @param actividadId ID de la actividad (ej: ID de Zumba).
     * @return Lista de horarios asignados a esa actividad.
     */
    List<HorarioResponseDto> buscarPorActividad(Long actividadId);
}