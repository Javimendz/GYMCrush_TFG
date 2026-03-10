package com.backend.service;

import com.backend.dto.EntrenamientoResponseDto;
import java.util.List;

public interface IEntrenamientoService {
    // Registrar una nueva sesión de entrenamiento [cite: 98, 359]
    EntrenamientoResponseDto guardar(EntrenamientoResponseDto dto);
    
    // Obtener todo el historial de un usuario para el calendario [cite: 16, 278]
    List<EntrenamientoResponseDto> listarPorUsuario(Integer usuarioId);
    
    // Calcular estadísticas (ej. total de minutos) para el panel de salud [cite: 63, 324]
    Integer obtenerTotalMinutosMes(Integer usuarioId);

}