package com.backend.service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.domain.DetallePlan;
import com.backend.domain.Entrenamiento;
import com.backend.domain.PlanEntrenamiento;
import com.backend.domain.Rutina;
import com.backend.domain.Usuario;
import com.backend.dto.DetallePlanRequestDto;
import com.backend.dto.DetallePlanResponseDto;
import com.backend.dto.PlanRequestDto;
import com.backend.dto.PlanResponseDto;
import com.backend.mapper.PlanMapper;
import com.backend.repository.DetallePlanRepository;
import com.backend.repository.EntrenamientoRepository;
import com.backend.repository.PlanEntrenamientoRepository;
import com.backend.repository.RutinaRepository;
import com.backend.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PlanServiceImp implements IPlanService {

    private final PlanEntrenamientoRepository planRepo;
    private final RutinaRepository rutinaRepo;
    private final UsuarioRepository usuarioRepo;
    private final DetallePlanRepository detalleRepo;
    private final EntrenamientoRepository entrenamientoRepo;
    private final PlanMapper planMapper;

    @Override
    @Transactional
    public PlanResponseDto crearPlan(PlanRequestDto dto) {
        log.info("Creando nuevo plan maestro: {}", dto.getNombre());
        PlanEntrenamiento plan = planMapper.toEntity(dto);
        PlanEntrenamiento guardado = planRepo.saveAndFlush(plan);
        return planMapper.toResponseDto(guardado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlanResponseDto> listarTodos() {
        return planRepo.findAll().stream()
                .map(planMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PlanResponseDto obtenerPorId(Long id) {

        PlanEntrenamiento plan = planRepo.findWithEjerciciosById(id)
                .orElseThrow(() -> new RuntimeException("Plan no encontrado con ID: " + id));

        return planMapper.toResponseDto(plan);
    }

    @Override
    @Transactional
    public void eliminarPlan(Long id) {
        if (!planRepo.existsById(id)) {
            throw new RuntimeException("No se puede eliminar: Plan no encontrado");
        }
        planRepo.deleteById(id);
    }

   

@Override
@Transactional
public void suscribirUsuarioAPlan(Long usuarioId, Long planId, String usernameLogueado, boolean esAdmin) {
    Usuario usuarioDestino = usuarioRepo.findById(usuarioId)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

    // ... (tus validaciones de seguridad anteriores) ...

    // 1. COMPROBACIÓN: ¿Ya está suscrito a este mismo plan? 
    // Esto evita procesar todo si no ha cambiado de plan
    if (usuarioDestino.getPlanActivo() != null && usuarioDestino.getPlanActivo().getId().equals(planId)) {
        log.info("El usuario ya está suscrito a este plan. No se requiere acción.");
        return; 
    }

    PlanEntrenamiento plan = planRepo.findWithEjerciciosById(planId)
            .orElseThrow(() -> new RuntimeException("Plan no encontrado"));

    LocalDate hoy = LocalDate.now();

    // 2. LIMPIEZA: Borramos rutinas pendientes para evitar el Error 409
    // Borramos las rutinas de HOY en adelante que no estén completadas
    rutinaRepo.deleteByUsuarioAndFechaAsignacionGreaterThanEqualAndCompletado(usuarioDestino, hoy, false);
    log.info("Limpieza de rutinas antiguas completada para el usuario {}", usuarioDestino.getUsername());

    // 3. GENERACIÓN: (Tu lógica de las fechas que corregimos antes)
    LocalDate lunesDeEstaSemana = hoy.with(java.time.DayOfWeek.MONDAY);
    List<Rutina> rutinasAGuardar = new java.util.ArrayList<>();

    for (DetallePlan detalle : plan.getEjercicios()) {
        LocalDate fechaAsignacion = lunesDeEstaSemana.plusDays(detalle.getDiaSemana() - 1);

        // Solo guardamos rutinas de hoy en adelante (opcional, según prefieras)
        if (!fechaAsignacion.isBefore(hoy)) {
            Rutina nuevaRutina = Rutina.builder()
                    .usuario(usuarioDestino)
                    .entrenamiento(detalle.getEntrenamiento())
                    .fechaAsignacion(fechaAsignacion)
                    .orden(detalle.getOrden())
                    .completado(false)
                    .build();
            rutinasAGuardar.add(nuevaRutina);
        }
    }

    rutinaRepo.saveAll(rutinasAGuardar);

    // 4. VINCULACIÓN: Ahora sí, actualizamos el plan activo
    usuarioDestino.setPlanActivo(plan);
    usuarioRepo.save(usuarioDestino);
}

    @Override
    @Transactional
    public DetallePlanResponseDto añadirEjercicioAlPlan(Long planId, DetallePlanRequestDto dto) {
        // Buscamos el plan
        PlanEntrenamiento plan = planRepo.findWithEjerciciosById(planId)
                .orElseThrow(() -> new RuntimeException("Plan no encontrado"));

        Entrenamiento entrenamiento = entrenamientoRepo.findById(dto.getEntrenamientoId())
                .orElseThrow(() -> new RuntimeException("Entrenamiento no encontrado"));

        // Creamos la relación y le pasamos el objeto lleno
        DetallePlan detalle = new DetallePlan();
        detalle.setPlan(plan);
        detalle.setEntrenamiento(entrenamiento);
        detalle.setDiaSemana(dto.getDiaSemana());
        detalle.setOrden(dto.getOrden());

        // Guardamos el detalle y forzamos el guardado
        DetallePlan guardado = detalleRepo.saveAndFlush(detalle);

        // Mapeamos y devolvemos la respuesta
        return planMapper.toDetalleResponseDto(guardado);
    }

}