package com.backend.service;

import java.util.List;

import com.backend.dto.SaludRequestDto;
import com.backend.dto.SaludResponseDto;

public interface ISaludService {
    

    //Registra un nuevo progreso de salud de un usuario
    SaludResponseDto registrarProgreso(SaludRequestDto dto, Long usuarioId);

    //Obtiene todo el historial de un usuario
    List<SaludResponseDto> obtenerHistorialPorUsuario(Long usuarioId);

    SaludResponseDto obtenerProgresoActual(Long usuarioId);

    //El admin vera todo los registros disponibles
    List<SaludResponseDto> obtenerTodosLosRegistros();

    //elimina 
    void eliminarRegistro(Long id);
}
