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
import com.backend.exceptions.ResourceNotFoundException;
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
        // 1. Verificar existencia
        PlanEntrenamiento plan = planRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Plan no encontrado"));

        log.info("Eliminando plan: {}. Limpiando dependencias...", plan.getNombre());
        rutinaRepo.deleteByUsuarioPlanActivoIdAndCompletado(id, false);
        // 2. Limpiar la relación con los usuarios (para evitar el error de FK)
        usuarioRepo.desvincularPlanDeUsuarios(id);

        // 3. Borrar el plan.
        // Gracias a orphanRemoval = true, todos los 'detalles_plan' asociados
        // desaparecerán de la tabla que mostraste en la imagen.
        planRepo.delete(plan);

        log.info("Plan y sus detalles eliminados correctamente.");
    }

    @Override
    @Transactional
    public void suscribirUsuarioAPlan(Long usuarioId, Long planId, String usernameLogueado, boolean esAdmin) {
        Usuario usuarioDestino = usuarioRepo.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // 1. COMPROBACIÓN: ¿Ya está suscrito a este mismo plan?
        if (usuarioDestino.getPlanActivo() != null && usuarioDestino.getPlanActivo().getId().equals(planId)) {
            log.info("El usuario ya está suscrito a este plan. No se requiere acción.");
            return;
        }

        PlanEntrenamiento plan = planRepo.findWithEjerciciosById(planId)
                .orElseThrow(() -> new RuntimeException("Plan no encontrado"));

        LocalDate hoy = LocalDate.now();

        // 2. LIMPIEZA: Borramos rutinas pendientes para evitar duplicados
        rutinaRepo.deleteByUsuarioAndFechaAsignacionGreaterThanEqualAndCompletado(usuarioDestino, hoy, false);
        log.info("Limpieza de rutinas antiguas completada para el usuario {}", usuarioDestino.getUsername());

        // 3. GENERACIÓN: Repartimos ejercicios por días de la semana
        LocalDate lunesDeEstaSemana = hoy.with(java.time.DayOfWeek.MONDAY);
        List<Rutina> rutinasAGuardar = new java.util.ArrayList<>();

        for (DetallePlan detalle : plan.getEjercicios()) {
            LocalDate fechaAsignacion = lunesDeEstaSemana.plusDays(detalle.getDiaSemana() - 1);

            // CORRECCIÓN: Si el día ya pasó esta semana, lo movemos a la semana que viene
            // en lugar de descartarlo. Así siempre se ven todos los ejercicios del plan.
            if (fechaAsignacion.isBefore(hoy)) {
                fechaAsignacion = fechaAsignacion.plusWeeks(1);
            }

            log.info("Detalle: ejercicio={}, diaSemana={}, fechaAsignada={}",
                    detalle.getEntrenamiento().getNombre(),
                    detalle.getDiaSemana(),
                    fechaAsignacion);

            Rutina nuevaRutina = Rutina.builder()
                    .usuario(usuarioDestino)
                    .entrenamiento(detalle.getEntrenamiento())
                    .fechaAsignacion(fechaAsignacion)
                    .orden(detalle.getOrden())
                    .completado(false)
                    .build();
            rutinasAGuardar.add(nuevaRutina);
        }

        rutinaRepo.saveAll(rutinasAGuardar);
        log.info("Se guardaron {} rutinas para el usuario {}", rutinasAGuardar.size(), usuarioDestino.getUsername());

        // 4. VINCULACIÓN: Actualizamos el plan activo del usuario
        usuarioDestino.setPlanActivo(plan);
        usuarioRepo.save(usuarioDestino);
    }

    @Override
    @Transactional
    public PlanResponseDto añadirEjercicioAlPlan(Long planId, List<DetallePlanRequestDto> ejerciciosDto) {
        PlanEntrenamiento plan = planRepo.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("Plan no encontrado"));

        // 1. Convertimos los DTOs en entidades DetallePlan
        for (DetallePlanRequestDto dto : ejerciciosDto) {
            Entrenamiento entrenamiento = entrenamientoRepo.findById(dto.getEntrenamientoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Ejercicio no encontrado"));

            DetallePlan nuevoDetalle = new DetallePlan();
            nuevoDetalle.setEntrenamiento(entrenamiento);
            nuevoDetalle.setDiaSemana(dto.getDiaSemana());
            nuevoDetalle.setOrden(dto.getOrden());
            nuevoDetalle.setSeries(dto.getSeries());
            nuevoDetalle.setRepeticiones(dto.getRepeticiones());

            // 2. Usamos el método de ayuda que creamos en la entidad
            plan.addEjercicio(nuevoDetalle);
        }

        // 3. Guardamos el Plan (gracias al CascadeType.ALL, guardará todos los
        // DetallePlan nuevos)
        PlanEntrenamiento guardado = planRepo.save(plan);
        return planMapper.toResponseDto(guardado);

    }

}