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
    private final EntrenamientoRepository entrenamientoRepo; // ¡Vital para evitar nulls!
    private final PlanMapper planMapper;

    @Override
    @Transactional
    public PlanResponseDto crearPlan(PlanRequestDto dto) {
        log.info("Creando nuevo plan maestro: {}", dto.getNombre());
        PlanEntrenamiento plan = planMapper.toEntity(dto);
        // Usamos saveAndFlush para sincronizar con la DB de inmediato
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
        
        // 1. Buscamos al usuario destino
        Usuario usuarioDestino = usuarioRepo.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
                
        if (!esAdmin && !usuarioDestino.getUsername().equals(usernameLogueado)) {
            log.warn("Intento de suscripción no autorizada. Usuario logueado: {}, Intentó suscribir a: {}", usernameLogueado, usuarioDestino.getUsername());
            throw new RuntimeException("Acceso denegado: No tienes permiso para suscribir a otros usuarios.");
        }

        // CORRECCIÓN 1: Usamos el método con JOIN FETCH para traer los ejercicios obligatoriamente
        PlanEntrenamiento plan = planRepo.findWithEjerciciosById(planId)
                .orElseThrow(() -> new RuntimeException("Plan no encontrado"));

        LocalDate hoy = LocalDate.now();

        // CORRECCIÓN 2: Comprobamos si está vacía (isEmpty), no solo null.
        if (plan.getEjercicios() == null || plan.getEjercicios().isEmpty()) {
            log.warn("ATENCIÓN: El plan '{}' no tiene ejercicios en DetallePlan. No hay rutinas que guardar.", plan.getNombre());
            return; // Salimos silenciosamente porque no hay nada que crear
        }

        log.info("Iniciando suscripción. Se van a generar {} ejercicios para {}", plan.getEjercicios().size(), usuarioDestino.getUsername());

        // CORRECCIÓN 3: Creamos una lista para guardar todo de una sola vez
        java.util.List<Rutina> rutinasAGuardar = new java.util.ArrayList<>();

        for (DetallePlan detalle : plan.getEjercicios()) {
            
            // Ojo: getDiaSemana() debe ser un int de 1 (Lunes) a 7 (Domingo). Si pasas 0, fallará aquí.
            LocalDate fechaAsignacion = hoy.with(java.time.DayOfWeek.of(detalle.getDiaSemana()));
            
            if (fechaAsignacion.isBefore(hoy)) {
                fechaAsignacion = fechaAsignacion.plusWeeks(1); 
            }

            Rutina nuevaRutina = Rutina.builder()
                    .usuario(usuarioDestino)
                    .entrenamiento(detalle.getEntrenamiento())
                    .fechaAsignacion(fechaAsignacion)
                    .orden(detalle.getOrden())
                    .completado(false)
                    .build();

            rutinasAGuardar.add(nuevaRutina);
            log.info("Generando: {} para la fecha {}", detalle.getEntrenamiento().getNombre(), fechaAsignacion);
        }

        // CORRECCIÓN 4: saveAll es más eficiente en base de datos que hacer save() dentro de un bucle
        rutinaRepo.saveAll(rutinasAGuardar);
        log.info("¡Éxito! Se han guardado {} rutinas en la base de datos.", rutinasAGuardar.size());
    }
    
    @Override
    @Transactional
    public DetallePlanResponseDto añadirEjercicioAlPlan(Long planId, DetallePlanRequestDto dto) {
        // 1. Buscamos el plan
       PlanEntrenamiento plan = planRepo.findWithEjerciciosById(planId)
        .orElseThrow(() -> new RuntimeException("Plan no encontrado"));

        // 2. Buscamos el Entrenamiento COMPLETO (Esto trae el nombre de la DB)
        Entrenamiento entrenamiento = entrenamientoRepo.findById(dto.getEntrenamientoId())
                .orElseThrow(() -> new RuntimeException("Entrenamiento no encontrado"));

        // 3. Creamos la relación y le pasamos el objeto lleno
        DetallePlan detalle = new DetallePlan();
        detalle.setPlan(plan);
        detalle.setEntrenamiento(entrenamiento); // <-- Aquí va la magia
        detalle.setDiaSemana(dto.getDiaSemana());
        detalle.setOrden(dto.getOrden());

        // 4. Guardamos el detalle y forzamos el guardado
        DetallePlan guardado = detalleRepo.saveAndFlush(detalle);

        // 5. Mapeamos y devolvemos la respuesta
        return planMapper.toDetalleResponseDto(guardado);
    }

  
}