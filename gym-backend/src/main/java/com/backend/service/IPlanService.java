package com.backend.service;



import com.backend.dto.DetallePlanRequestDto;
import com.backend.dto.DetallePlanResponseDto;
import com.backend.dto.PlanRequestDto;
import com.backend.dto.PlanResponseDto;
import java.util.List;

public interface IPlanService {
    // Para el Administrador
    PlanResponseDto crearPlan(PlanRequestDto dto);
    void eliminarPlan(Long id);

    // Para el Usuario 
    List<PlanResponseDto> listarTodos();
    PlanResponseDto obtenerPorId(Long id);


    DetallePlanResponseDto añadirEjercicioAlPlan(Long planId, DetallePlanRequestDto dto);
void suscribirUsuarioAPlan(Long usuarioId, Long planId, String usernameLogueado, boolean esAdmin);}