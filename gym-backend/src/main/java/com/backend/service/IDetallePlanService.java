package com.backend.service;

import com.backend.dto.DetallePlanResponseDto;
import java.util.List;

public interface IDetallePlanService {
    // Añade un ejercicio específico a un día concreto de un plan
    
    // Obtiene todos los ejercicios de un plan maestro
    List<DetallePlanResponseDto> listarPorPlan(Long planId);
    
    // Elimina un ejercicio de la composición del plan
    void eliminarEjercicio(Long detalleId);
    
    // (Opcional) Permite cambiar el orden o el día de un ejercicio ya asignado
    DetallePlanResponseDto actualizarOrden(Long detalleId, Integer nuevoDia, Integer nuevoOrden);
}