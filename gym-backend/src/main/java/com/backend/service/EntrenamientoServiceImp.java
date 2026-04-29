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

import com.backend.repository.TutorialRepository;
import com.backend.domain.Tutorial;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EntrenamientoServiceImp implements IEntrenamientoService {

    private final EntrenamientoRepository entrenamientoRepository;
    private final EntrenamientoMapper entrenamientoMapper;
    private final TutorialRepository tutorialRepository;

    @Override
    @Transactional
    public EntrenamientoResponseDto crear(EntrenamientoRequestDto dto) {
        log.info("Creando entrenamiento maestro: {}", dto.getNombre());

        //  Crear Entrenamiento
        Entrenamiento entrenamiento = new Entrenamiento();
        entrenamiento.setNombre(dto.getNombre());
        entrenamiento.setIntensidad(dto.getIntensidad());
        entrenamiento.setDuracion(dto.getDuracion());
        
        
        Entrenamiento guardado = entrenamientoRepository.save(entrenamiento);

        //  Crear Tutorial asociado
        Tutorial tutorial = new Tutorial();
        tutorial.setTitulo(dto.getNombre());
        tutorial.setUrlVideo(dto.getUrlVideo()); 
        tutorial.setDescripcion(dto.getDescripcion());
        tutorial.setEntrenamiento(guardado); 

        tutorialRepository.save(tutorial);

          return entrenamientoMapper.toResponseDto(guardado, tutorial);
    }

    @Override
    @Transactional(readOnly = true)
    public EntrenamientoResponseDto obtenerPorId(Long id) {
    Entrenamiento entrenamiento = entrenamientoRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("No encontrado"));

    // Sacamos el tutorial
    Tutorial tutorial = (entrenamiento.getTutoriales() != null && !entrenamiento.getTutoriales().isEmpty()) 
                        ? entrenamiento.getTutoriales().get(0) 
                        : null;

    // Retornamos pasando los dos argumentos
    return entrenamientoMapper.toResponseDto(entrenamiento, tutorial);
}

    @Override
    @Transactional(readOnly = true)
public List<EntrenamientoResponseDto> listarTodos() {
    return entrenamientoRepository.findAll().stream()
            .map(entrenamiento -> {
                // 1. Buscamos el primer tutorial de la lista de este entrenamiento
                // Si la lista está vacía, enviamos null
                Tutorial tutorial = (entrenamiento.getTutoriales() != null && !entrenamiento.getTutoriales().isEmpty()) 
                                    ? entrenamiento.getTutoriales().get(0) 
                                    : null;
                
                // 2. Ahora llamamos al mapper con los DOS parámetros que pide
                return entrenamientoMapper.toResponseDto(entrenamiento, tutorial);
            })
            .toList();
}
   @Override
@Transactional
public EntrenamientoResponseDto actualizar(Long id, EntrenamientoRequestDto dto) {
    log.info("Actualizando entrenamiento maestro ID: {}", id);

    Entrenamiento existente = entrenamientoRepository.findById(id)
            .orElseThrow(
                    () -> new ResourceNotFoundException("No se puede actualizar: Entrenamiento no encontrado"));

    // 1. Actualizamos los datos del ejercicio
    existente.setNombre(dto.getNombre());
    existente.setDescripcion(dto.getDescripcion());
    existente.setDuracion(dto.getDuracion());
    existente.setIntensidad(dto.getIntensidad());
    existente.setCantidadEjercicios(dto.getCantidadEjercicios());

    Entrenamiento guardado = entrenamientoRepository.save(existente);

    // 2. Buscamos el tutorial asociado para pasarlo al mapper
    Tutorial tutorial = (guardado.getTutoriales() != null && !guardado.getTutoriales().isEmpty()) 
                        ? guardado.getTutoriales().get(0) 
                        : null;

    // (Opcional) Si quieres que al editar el entrenamiento también se edite el video:
    if (tutorial != null) {
        tutorial.setUrlVideo(dto.getUrlVideo());
        // tutorial.setDescripcion(dto.getDescripcion()); // Si quieres sincronizar la descripción
        tutorialRepository.save(tutorial);
    }

    log.info("Entrenamiento {} actualizado correctamente", id);

    // 3. RETORNO CON LOS DOS PARÁMETROS (¡Esto es lo que pedía la línea 104!)
    return entrenamientoMapper.toResponseDto(guardado, tutorial); 
}
    @Override
    @Transactional
    public void eliminar(Long id) {
        if (!entrenamientoRepository.existsById(id)) {
            throw new ResourceNotFoundException("No se puede eliminar: Entrenamiento no encontrado");
        }
        entrenamientoRepository.deleteById(id);
    }

    private EntrenamientoResponseDto mapearADto(Entrenamiento entrenamiento, Tutorial tutorial) {
    return EntrenamientoResponseDto.builder()
            .id(entrenamiento.getId())
            .nombre(entrenamiento.getNombre())
            .intensidad(entrenamiento.getIntensidad())
            .duracion(entrenamiento.getDuracion())
            .urlVideo(tutorial.getUrlVideo())    
            .descripcion(tutorial.getDescripcion()) 
            .build();
}
}