package com.backend.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

import com.backend.domain.Actividad;
import com.backend.dto.ActividadResponseDto;
import com.backend.mapper.ActividadMapper;
import com.backend.repository.ActividadRepository;

@Service
@RequiredArgsConstructor // Importante: Esto inyecta automáticamente el Repo y el Mapper
public class ActividadServiceImp implements IActividadService {

    private final ActividadRepository actividadRepository;
    private final ActividadMapper actividadMapper;

    @Override
    @Transactional(readOnly = true)
    public List<ActividadResponseDto> listarTodos() {
        List<Actividad> actividades = actividadRepository.findAll();
        return actividadMapper.toResponseDtoList(actividades);
    }

    @Override
    @Transactional(readOnly = true)
    public ActividadResponseDto buscarPorId(Long id) {
        Actividad actividad = actividadRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Actividad no encontrada"));
        return actividadMapper.toResponseDto(actividad);
    }

    @Override
    @Transactional
    public ActividadResponseDto crear(ActividadResponseDto dto) {
        // Usamos toEntityCreate para que el ID sea autogenerado por la DB
        Actividad nueva = actividadMapper.toEntityCreate(dto);
        return actividadMapper.toResponseDto(actividadRepository.save(nueva));
    }

    @Override
    @Transactional
    public ActividadResponseDto actualizar(Long id, ActividadResponseDto dto) {
        Actividad existente = actividadRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ID no encontrado para actualizar"));
        
        // El mapper actualiza los campos de la entidad 'existente' con los del DTO
        actividadMapper.updateActividadFromDto(dto, existente);
        
        return actividadMapper.toResponseDto(actividadRepository.save(existente));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        if (!actividadRepository.existsById(id)) {
            throw new RuntimeException("No existe la actividad con ese ID");
        }
        actividadRepository.deleteById(id);
    }

    // --- MÉTODOS DE FILTRO SEGÚN TU REPOSITORY ---

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
    public List<ActividadResponseDto> filtrarPorPrecioMaximo(Integer precio) {
        List<Actividad> actividades = actividadRepository.findByPrecioLessThanEqual(precio);
        return actividadMapper.toResponseDtoList(actividades);
    }
}
