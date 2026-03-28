package com.backend.service;

import com.backend.domain.*;
import com.backend.dto.TutorialResponseDto;
import com.backend.dto.VisualizacionRequestDto;
import com.backend.dto.VisualizacionResponseDto;
import com.backend.repository.*;
import com.backend.mapper.VisualizacionMapper;
import com.backend.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class VisualizacionServiceImp implements IVisualizacionService {

    private final VisualizacionRepository visualizacionRepository;
    private final UsuarioRepository usuarioRepository;
    private final TutorialRepository tutorialRepository;
    private final VisualizacionMapper visualizacionMapper;

    @Override
    @Transactional
    public VisualizacionResponseDto guardarProgreso(VisualizacionRequestDto dto) {
        // 1. Buscar si ya existe el registro (Upsert Logic)
        Visualizacion visualizacion = visualizacionRepository
                .findByUsuarioIdAndTutorialId(dto.getUsuarioId(), dto.getTutorialId())
                .orElseGet(() -> crearNuevaVisualizacion(dto));

        // 2. Actualizar datos de progreso
        visualizacion.setProgresoSegundos(dto.getProgresoSegundos());
        visualizacion.setFechaVisualizacion(LocalDateTime.now());

        // Si el DTO dice que está completado o si los segundos superan el 95% del video
        // (opcional)
        if (dto.getCompletado() != null) {
            visualizacion.setCompletado(dto.getCompletado());
        }

        log.info("Progreso guardado: Usuario {} en Video {}. Segundos: {}",
                dto.getUsuarioId(), dto.getTutorialId(), dto.getProgresoSegundos());

        return visualizacionMapper.toResponseDto(visualizacionRepository.save(visualizacion));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TutorialResponseDto> obtenerPopulares() {
        log.info("Calculando los 3 tutoriales más populares");

        // Solicitamos los 3 mejores (página 0, tamaño 3)
        List<Tutorial> populares = visualizacionRepository.findPopularTutorials(PageRequest.of(0, 3));

        // Mapeamos a DTO (Asumiendo que tienes un tutorialMapper o convirtiendo
        // manualmente)
        return populares.stream()
                .map(t -> TutorialResponseDto.builder()
                        .id(t.getId())
                        .titulo(t.getTitulo())
                        .descripcion(t.getDescripcion())
                        .urlVideo(t.getUrlVideo())
                        .nombreCategoria(t.getCategoria().getNombre())
                        .build())
                .toList();
    }

    private Visualizacion crearNuevaVisualizacion(VisualizacionRequestDto dto) {
        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        Tutorial tutorial = tutorialRepository.findById(dto.getTutorialId())
                .orElseThrow(() -> new ResourceNotFoundException("Tutorial no encontrado"));

        return Visualizacion.builder()
                .usuario(usuario)
                .tutorial(tutorial)
                .completado(false)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<VisualizacionResponseDto> obtenerHistorial(Long usuarioId) {
        return visualizacionRepository.findByUsuarioIdAndCompletadoTrue(usuarioId)
                .stream().map(visualizacionMapper::toResponseDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<VisualizacionResponseDto> obtenerContinuarViendo(Long usuarioId) {
        // Obtenemos los videos no terminados, ordenados por fecha más reciente
        return visualizacionRepository.findByUsuarioIdAndCompletadoFalseOrderByFechaVisualizacionDesc(usuarioId)
                .stream().map(visualizacionMapper::toResponseDto).toList();
    }

    @Override
    @Transactional
    public void borrarDeHistorial(Long id) {
        if (!visualizacionRepository.existsById(id))
            throw new ResourceNotFoundException("Registro no encontrado");
        visualizacionRepository.deleteById(id);
    }
}