package com.backend.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

import com.backend.domain.Actividad;
import com.backend.domain.Horario;
import com.backend.dto.HorarioRequestDto;
import com.backend.dto.HorarioResponseDto;
import com.backend.mapper.HorarioMapper;
import com.backend.repository.ActividadRepository;
import com.backend.repository.HorarioRepository;
import com.backend.exceptions.ResourceNotFoundException;

/**
 * Implementación de la lógica de negocio para la gestión de horarios del gimnasio.
 * Esta clase coordina la asignación de actividades a franjas horarias específicas,
 * asegurando la integridad referencial y el control de aforo.
 * * @author Tu Nombre/TFG
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class HorarioServiceImp implements IHorarioService {

    private final HorarioRepository horarioRepository;
    private final ActividadRepository actividadRepository;
    private final HorarioMapper horarioMapper;

    /**
     * Recupera el listado completo de horarios configurados en el sistema.
     * * @return Lista de {@link HorarioResponseDto} con la información técnica y comercial.
     */
    @Override
    @Transactional(readOnly = true)
    public List<HorarioResponseDto> listarTodos() {
        return horarioMapper.toResponseDtoList(horarioRepository.findAll());
    }

    /**
     * Busca un horario específico por su identificador único.
     * * @param id Identificador del horario.
     * @return DTO con la información detallada del horario.
     * @throws ResourceNotFoundException si el ID no existe en la base de datos.
     */
    @Override
    @Transactional(readOnly = true)
    public HorarioResponseDto obtenerPorId(Long id) {
        Horario horario = horarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Horario no encontrado con ID: " + id));
        return horarioMapper.toResponseDto(horario);
    }

    /**
     * Crea un nuevo registro de horario vinculándolo a una actividad existente.
     * Realiza una validación previa para asegurar que la actividad asignada sea válida.
     * * @param dto Datos de la petición (día, horas, aforo e ID de actividad).
     * @return {@link HorarioResponseDto} con los datos persistidos y el nombre de la actividad.
     * @throws ResourceNotFoundException si la actividad referenciada no existe.
     */
    @Override
    @Transactional
    public HorarioResponseDto crear(HorarioRequestDto dto) {
        // Validación de existencia de la relación OneToMany
        Actividad actividad = actividadRepository.findById(dto.getActividadId())
                .orElseThrow(() -> new ResourceNotFoundException("No se puede crear el horario: Actividad no encontrada"));

        Horario nuevoHorario = horarioMapper.toEntity(dto);
        nuevoHorario.setActividad(actividad);

        return horarioMapper.toResponseDto(horarioRepository.save(nuevoHorario));
    }

    /**
     * Actualiza la información de un horario existente. 
     * Si el ID de la actividad cambia, se actualiza el vínculo con la nueva entidad.
     * * @param id ID del horario a modificar.
     * @param dto Nuevos datos de configuración.
     * @return DTO actualizado.
     * @throws ResourceNotFoundException si el horario o la nueva actividad no existen.
     */
    @Override
    @Transactional
    public HorarioResponseDto actualizar(Long id, HorarioRequestDto dto) {
        Horario existente = horarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se puede actualizar: Horario no encontrado"));

        // Gestión de cambio de actividad vinculada
        if (!existente.getActividad().getId().equals(dto.getActividadId())) {
            Actividad nuevaActividad = actividadRepository.findById(dto.getActividadId())
                    .orElseThrow(() -> new ResourceNotFoundException("La nueva actividad asignada no existe"));
            existente.setActividad(nuevaActividad);
        }

        // Actualización de atributos básicos
        existente.setDiaSemana(dto.getDiaSemana());
        existente.setHoraInicio(dto.getHoraInicio());
        existente.setSala(dto.getSala());
        existente.setHoraFin(dto.getHoraFin());
        existente.setAforoMax(dto.getAforoMax());

        return horarioMapper.toResponseDto(horarioRepository.save(existente));
    }

    /**
     * Elimina físicamente un horario del sistema.
     * * @param id ID del registro a eliminar.
     * @throws ResourceNotFoundException si el registro no existe.
     */
    @Override
    @Transactional
    public void eliminar(Long id) {
        if (!horarioRepository.existsById(id)) {
            throw new ResourceNotFoundException("No se puede eliminar: ID " + id + " no existe");
        }
        horarioRepository.deleteById(id);
    }

    /**
     * Filtra los horarios disponibles según el día de la semana.
     * * @param dia Nombre del día (LUNES, MARTES, etc.).
     * @return Lista de horarios programados para dicho día.
     */
    @Override
    @Transactional(readOnly = true)
    public List<HorarioResponseDto> listarPorDia(String dia) {
        return horarioMapper.toResponseDtoList(
            horarioRepository.findByDiaSemanaIgnoreCase(dia)
        );
    }

    /**
     * Obtiene el cronograma semanal de una actividad específica.
     * * @param actividadId ID de la actividad (ej: Crossfit).
     * @return Lista de horarios donde se imparte la actividad.
     */
    @Override
    @Transactional(readOnly = true)
    public List<HorarioResponseDto> buscarPorActividad(Long actividadId) {
        return horarioMapper.toResponseDtoList(
            horarioRepository.findByActividadId(actividadId)
        );
    }
}