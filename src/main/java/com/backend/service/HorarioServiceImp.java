package com.backend.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.backend.domain.Actividad;
import com.backend.domain.Horario;
import com.backend.domain.Sala;
import com.backend.dto.HorarioRequestDto;
import com.backend.dto.HorarioResponseDto;
import com.backend.mapper.HorarioMapper;
import com.backend.repository.ActividadRepository;
import com.backend.repository.HorarioRepository;
import com.backend.repository.SalaRepository;
import com.backend.exceptions.ResourceNotFoundException;

/**
 * Implementación de la lógica de negocio para la gestión de horarios del
 * gimnasio.
 * Esta clase coordina la asignación de actividades a franjas horarias
 * específicas,
 * asegurando la integridad referencial y el control de aforo.
 * * @author JavierMendez/TFG
 * 
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class HorarioServiceImp implements IHorarioService {

    private final HorarioRepository horarioRepository;
    private final ActividadRepository actividadRepository;
    private final HorarioMapper horarioMapper;
    private final SalaRepository salaRepository;

    /**
     * Recupera el listado completo de horarios configurados en el sistema.
     * * @return Lista de {@link HorarioResponseDto} con la información técnica y
     * comercial.
     */
    @Override
    @Transactional(readOnly = true)
    public List<HorarioResponseDto> listarTodos() {
        log.info("Listando todos los horarios");
        List<HorarioResponseDto> horarios = horarioMapper.toResponseDtoList(horarioRepository.findAll());
        log.debug("Total de horarios encontrados: {}", horarios.size());
        return horarios;
    }

    /**
     * Busca un horario específico por su identificador único.
     * * @param id Identificador del horario.
     * 
     * @return DTO con la información detallada del horario.
     * @throws ResourceNotFoundException si el ID no existe en la base de datos.
     */
    @Override
    @Transactional(readOnly = true)
    public HorarioResponseDto obtenerPorId(Long id) {
        log.info("Buscando horario por ID: {}", id);
        Horario horario = horarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Horario no encontrado con ID: " + id));
        log.debug("Horario encontrado: {} - {}", horario.getDiaSemana(), horario.getHoraInicio());
        return horarioMapper.toResponseDto(horario);
    }

    /**
     * Crea un nuevo registro de horario vinculándolo a una actividad existente.
     * Realiza una validación previa para asegurar que la actividad asignada sea
     * válida.
     * * @param dto Datos de la petición (día, horas, aforo e ID de actividad).
     * 
     * @return {@link HorarioResponseDto} con los datos persistidos y el nombre de
     *         la actividad.
     * @throws ResourceNotFoundException si la actividad referenciada no existe.
     */
    @Override
    @Transactional
    public HorarioResponseDto crear(HorarioRequestDto dto) {
        log.info("Creando nuevo horario para actividad ID: {}", dto.getActividadId());
        // Validación de existencia de la relación OneToMany
        Actividad actividad = actividadRepository.findById(dto.getActividadId())
                .orElseThrow(
                        () -> new ResourceNotFoundException("No se puede crear el horario: Actividad no encontrada"));
        log.debug("Actividad encontrada: {}", actividad.getNombre());

        Sala sala = salaRepository.findById(dto.getSalaId())
                .orElseThrow(() -> new ResourceNotFoundException("No se puede crear el horario: Sala no encontrada"));
        log.debug("Sala encontrada: {}", sala.getNombre());

        Horario nuevoHorario = horarioMapper.toEntity(dto);
        nuevoHorario.setActividad(actividad);
        nuevoHorario.setSala(sala);

        HorarioResponseDto resultado = horarioMapper.toResponseDto(horarioRepository.save(nuevoHorario));
        log.info("Horario creado exitosamente con ID: {}", resultado.getId());
        return resultado;
    }

    /**
     * Actualiza la información de un horario existente.
     * Si el ID de la actividad cambia, se actualiza el vínculo con la nueva
     * entidad.
     * * @param id ID del horario a modificar.
     * 
     * @param dto Nuevos datos de configuración.
     * @return DTO actualizado.
     * @throws ResourceNotFoundException si el horario o la nueva actividad no
     *                                   existen.
     */
    @Override
    @Transactional
    public HorarioResponseDto actualizar(Long id, HorarioRequestDto dto) {
        log.info("Actualizando horario con ID: {}", id);
        Horario existente = horarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se puede actualizar: Horario no encontrado"));

        // Gestión de cambio de actividad vinculada
        if (!existente.getActividad().getId().equals(dto.getActividadId())) {
            log.debug("Cambiando actividad vinculada de {} a {}", existente.getActividad().getId(),
                    dto.getActividadId());
            Actividad nuevaActividad = actividadRepository.findById(dto.getActividadId())
                    .orElseThrow(() -> new ResourceNotFoundException("La nueva actividad asignada no existe"));
            existente.setActividad(nuevaActividad);
        }
        if (!existente.getSala().getId().equals(dto.getSalaId())) {
            Sala nuevaSala = salaRepository.findById(dto.getSalaId())
                    .orElseThrow(() -> new ResourceNotFoundException("La nueva sala asignada no existe"));
            existente.setSala(nuevaSala);
        }

        // Actualización de atributos básicos
        existente.setDiaSemana(dto.getDiaSemana());
        existente.setHoraInicio(dto.getHoraInicio());

        existente.setHoraFin(dto.getHoraFin());
        existente.setAforoMax(dto.getAforoMax());

        HorarioResponseDto resultado = horarioMapper.toResponseDto(horarioRepository.save(existente));
        log.info("Horario {} actualizado exitosamente", id);
        return resultado;
    }

    /**
     * Elimina físicamente un horario del sistema.
     * * @param id ID del registro a eliminar.
     * 
     * @throws ResourceNotFoundException si el registro no existe.
     */
    @Override
    @Transactional
    public void eliminar(Long id) {
        log.info("Eliminando horario con ID: {}", id);
        if (!horarioRepository.existsById(id)) {
            log.warn("No se puede eliminar: Horario con ID {} no existe", id);
            throw new ResourceNotFoundException("No se puede eliminar: ID " + id + " no existe");
        }
        horarioRepository.deleteById(id);
        log.info("Horario {} eliminado exitosamente", id);
    }

    /**
     * Filtra los horarios disponibles según el día de la semana.
     * * @param dia Nombre del día (LUNES, MARTES, etc.).
     * 
     * @return Lista de horarios programados para dicho día.
     */
    @Override
    @Transactional(readOnly = true)
    public List<HorarioResponseDto> listarPorDia(String dia) {
        log.info("Listando horarios por día: {}", dia);
        List<HorarioResponseDto> horarios = horarioMapper.toResponseDtoList(
                horarioRepository.findByDiaSemanaIgnoreCase(dia));
        log.debug("Horarios encontrados para día '{}': {}", dia, horarios.size());
        return horarios;
    }

    /**
     * Obtiene el cronograma semanal de una actividad específica.
     * * @param actividadId ID de la actividad (ej: Crossfit).
     * 
     * @return Lista de horarios donde se imparte la actividad.
     */
    @Override
    @Transactional(readOnly = true)
    public List<HorarioResponseDto> buscarPorActividad(Long actividadId) {
        log.info("Buscando horarios por actividad ID: {}", actividadId);
        List<HorarioResponseDto> horarios = horarioMapper.toResponseDtoList(
                horarioRepository.findByActividadId(actividadId));
        log.debug("Horarios encontrados para actividad {}: {}", actividadId, horarios.size());
        return horarios;
    }
}