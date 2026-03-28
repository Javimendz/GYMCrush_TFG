package com.backend.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

import com.backend.domain.Actividad;
import com.backend.dto.ActividadRequestDto;
import com.backend.dto.ActividadResponseDto;
import com.backend.mapper.ActividadMapper;
import com.backend.repository.ActividadRepository;
import com.backend.exceptions.ResourceNotFoundException;

@Service
@RequiredArgsConstructor 
public class ActividadServiceImp implements IActividadService {

    private final ActividadRepository actividadRepository;
    private final ActividadMapper actividadMapper;

    @Override
    @Transactional(readOnly = true)
    public List<ActividadResponseDto> listarTodos() {

        return actividadMapper.toResponseDtoList(actividadRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public ActividadResponseDto buscarPorId(Long id) {
        Actividad actividad = actividadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Actividad no encontrada con ID: " + id));
        return actividadMapper.toResponseDto(actividad);
    }

    @Override
    @Transactional
    public ActividadResponseDto crear(ActividadRequestDto dto) {

        Actividad nueva = actividadMapper.toEntityCreate(dto);
        Actividad guardada = actividadRepository.save(nueva);
        
        return actividadMapper.toResponseDto(guardada);
    }

    @Override
    @Transactional
    public ActividadResponseDto actualizar(Long id, ActividadRequestDto dto) {

        Actividad existente = actividadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se puede actualizar: la actividad con ID " + id + " no existe"));
        
        actividadMapper.updateActividadFromDto(dto, existente);
        
        return actividadMapper.toResponseDto(actividadRepository.save(existente));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        if (!actividadRepository.existsById(id)) {
            throw new ResourceNotFoundException("Error al eliminar: ID " + id + " no encontrado");
        }
        actividadRepository.deleteById(id);
    }

    

    @Override
    @Transactional(readOnly = true)
    public List<ActividadResponseDto> buscarPorNombre(String nombre) {
        return actividadMapper.toResponseDtoList(
            actividadRepository.findByNombreContainingIgnoreCase(nombre)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<ActividadResponseDto> listarPorDia(String dia) {
        return actividadMapper.toResponseDtoList(
            actividadRepository.findActividadesDisponiblesPorDia(dia)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<ActividadResponseDto> filtrarPorPrecioMaximo(Integer precio) {
        return actividadMapper.toResponseDtoList(
            actividadRepository.findByPrecioLessThanEqual(precio)
        );
    }
}
