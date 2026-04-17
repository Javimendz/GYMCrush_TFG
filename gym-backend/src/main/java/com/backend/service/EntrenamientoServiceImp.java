package com.backend.service;

import com.backend.domain.Entrenamiento;
import com.backend.dto.EntrenamientoRequestDto;
import com.backend.dto.EntrenamientoResponseDto;
import com.backend.repository.EntrenamientoRepository;
import com.backend.mapper.EntrenamientoMapper;
import com.backend.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EntrenamientoServiceImp implements IEntrenamientoService {

    private final EntrenamientoRepository entrenamientoRepository;
    private final EntrenamientoMapper entrenamientoMapper;

    @Override
    @Transactional
    public EntrenamientoResponseDto crear(EntrenamientoRequestDto dto) {
        log.info("Creando nuevo entrenamiento maestro en el catálogo: {}", dto.getNombre());
        
        // Simplemente mapeamos y guardamos. Ya no hay que validar fechas ni usuarios.
        Entrenamiento entrenamiento = entrenamientoMapper.toEntity(dto);
        Entrenamiento guardado = entrenamientoRepository.save(entrenamiento);
        
        log.info("Entrenamiento guardado exitosamente con ID: {}", guardado.getId());
        return entrenamientoMapper.toResponseDto(guardado);
    }

    @Override
    @Transactional(readOnly = true)
    public EntrenamientoResponseDto obtenerPorId(Long id) {
        return entrenamientoRepository.findById(id)
                .map(entrenamientoMapper::toResponseDto)
                .orElseThrow(() -> new ResourceNotFoundException("Entrenamiento maestro no encontrado"));
    }

   
    @Override
    @Transactional(readOnly = true)
    public List<EntrenamientoResponseDto> listarTodos() { // Ojo: Cambia el nombre en tu interfaz IEntrenamientoService
        return entrenamientoRepository.findAll().stream()
                .map(entrenamientoMapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional
    public EntrenamientoResponseDto actualizar(Long id, EntrenamientoRequestDto dto) {
        log.info("Actualizando entrenamiento maestro ID: {}", id);

        Entrenamiento existente = entrenamientoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se puede actualizar: Entrenamiento no encontrado"));

        // Actualizamos los datos genéricos del ejercicio
        existente.setNombre(dto.getNombre());
        existente.setDescripcion(dto.getDescripcion());
        existente.setDuracion(dto.getDuracion());
        existente.setIntensidad(dto.getIntensidad());
        existente.setCantidadEjercicios(dto.getCantidadEjercicios());

        Entrenamiento guardado = entrenamientoRepository.save(existente);
        log.info("Entrenamiento {} actualizado correctamente", id);

        return entrenamientoMapper.toResponseDto(guardado);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        if (!entrenamientoRepository.existsById(id)) {
            throw new ResourceNotFoundException("No se puede eliminar: Entrenamiento no encontrado");
        }
        entrenamientoRepository.deleteById(id);
    }
}