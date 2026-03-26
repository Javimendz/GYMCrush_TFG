package com.backend.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.backend.domain.Actividad;
import com.backend.dto.ActividadRequestDto;
import com.backend.dto.ActividadResponseDto;
import com.backend.mapper.ActividadMapper;
import com.backend.repository.ActividadRepository;
import com.backend.exceptions.ResourceNotFoundException;

/**
 * Implementación del servicio para la gestión de actividades del gimnasio.
 * <p>
 * Esta clase maneja todas las operaciones CRUD para actividades,
 * incluyendo creación, consulta, actualización y eliminación de disciplinas
 * deportivas ofrecidas en el centro.
 * </p>
 *
 * @author Backend Team
 * @version 1.0
 * @since 2026
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ActividadServiceImp implements IActividadService {

    /** Repositorio para el acceso a datos de actividades */
    private final ActividadRepository actividadRepository;
    
    /** Mapper para convertir entre entidades y DTOs de actividades */
    private final ActividadMapper actividadMapper;

    /**
     * Obtiene todas las actividades disponibles en el sistema.
     * <p>
     * Retorna una lista completa de todas las disciplinas deportivas
     * ofrecidas por el gimnasio, ordenadas por defecto.
     * </p>
     *
     * @return Lista de DTOs con todas las actividades
     */
    @Override
    @Transactional(readOnly = true)
    public List<ActividadResponseDto> listarTodos() {
        log.info("Listando todas las actividades");
        
        // Obtener todas las actividades y convertirlas a DTOs
        List<ActividadResponseDto> actividades = actividadMapper.toResponseDtoList(actividadRepository.findAll());
        log.debug("Total de actividades encontradas: {}", actividades.size());
        return actividades;
    }

    /**
     * Busca una actividad específica por su identificador único.
     * <p>
     * Lanza excepción si la actividad no existe en la base de datos.
     * </p>
     *
     * @param id Identificador único de la actividad a buscar
     * @return DTO con los datos de la actividad encontrada
     * @throws ResourceNotFoundException si la actividad no existe
     */
    @Override
    @Transactional(readOnly = true)
    public ActividadResponseDto buscarPorId(Long id) {
        log.info("Buscando actividad por ID: {}", id);
        
        // Buscar la actividad o lanzar excepción si no existe
        Actividad actividad = actividadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Actividad no encontrada con ID: " + id));
        log.debug("Actividad encontrada: {}", actividad.getNombre());
        return actividadMapper.toResponseDto(actividad);
    }

    /**
     * Crea una nueva actividad en el sistema.
     * <p>
     * Convierte el DTO a entidad, la guarda en la base de datos
     * y retorna el DTO con el ID asignado.
     * </p>
     *
     * @param dto DTO con los datos de la nueva actividad
     * @return DTO con los datos de la actividad creada incluyendo su ID
     */
    @Override
    @Transactional
    public ActividadResponseDto crear(ActividadRequestDto dto) {
        log.info("Creando nueva actividad: {}", dto.getNombre());
        
        // Convertir DTO a entidad y guardar en la base de datos
        Actividad nueva = actividadMapper.toEntityCreate(dto);
        Actividad guardada = actividadRepository.save(nueva);
        log.info("Actividad creada exitosamente con ID: {}", guardada.getId());
        return actividadMapper.toResponseDto(guardada);
    }

    /**
     * Actualiza una actividad existente en el sistema.
     * <p>
     * Busca la actividad por su ID, actualiza sus datos con los del DTO
     * y guarda los cambios en la base de datos.
     * </p>
     *
     * @param id Identificador único de la actividad a actualizar
     * @param dto DTO con los datos actualizados de la actividad
     * @return DTO con los datos de la actividad actualizada
     * @throws ResourceNotFoundException si la actividad no existe
     */
    @Override
    @Transactional
    public ActividadResponseDto actualizar(Long id, ActividadRequestDto dto) {
        log.info("Actualizando actividad con ID: {}", id);
        Actividad existente = actividadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se puede actualizar: la actividad con ID " + id + " no existe"));
        
        actividadMapper.updateActividadFromDto(dto, existente);
        Actividad actualizada = actividadRepository.save(existente);
        log.info("Actividad {} actualizada exitosamente", id);
        return actividadMapper.toResponseDto(actualizada);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        log.info("Eliminando actividad con ID: {}", id);
        if (!actividadRepository.existsById(id)) {
            log.warn("No se puede eliminar: Actividad con ID {} no encontrada", id);
            throw new ResourceNotFoundException("Error al eliminar: ID " + id + " no encontrado");
        }
        actividadRepository.deleteById(id);
        log.info("Actividad {} eliminada exitosamente", id);
    }

    

    @Override
    @Transactional(readOnly = true)
    public List<ActividadResponseDto> buscarPorNombre(String nombre) {
        log.info("Buscando actividades por nombre: {}", nombre);
        List<ActividadResponseDto> actividades = actividadMapper.toResponseDtoList(
            actividadRepository.findByNombreContainingIgnoreCase(nombre)
        );
        log.debug("Actividades encontradas con nombre '{}': {}", nombre, actividades.size());
        return actividades;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ActividadResponseDto> listarPorDia(String dia) {
        log.info("Listando actividades por día: {}", dia);
        List<ActividadResponseDto> actividades = actividadMapper.toResponseDtoList(
            actividadRepository.findActividadesDisponiblesPorDia(dia)
        );
        log.debug("Actividades encontradas para día '{}': {}", dia, actividades.size());
        return actividades;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ActividadResponseDto> filtrarPorPrecioMaximo(Integer precio) {
        log.info("Filtrando actividades por precio máximo: {}", precio);
        List<ActividadResponseDto> actividades = actividadMapper.toResponseDtoList(
            actividadRepository.findByPrecioLessThanEqual(precio)
        );
        log.debug("Actividades encontradas con precio <= {}: {}", precio, actividades.size());
        return actividades;
    }
}