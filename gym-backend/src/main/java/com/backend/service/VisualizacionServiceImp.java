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
import java.util.stream.Collectors;
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
        //  Buscar si ya existe la visualización o crear una nueva
        Visualizacion visualizacion = visualizacionRepository
                .findByUsuarioIdAndTutorialId(dto.getUsuarioId(), dto.getTutorialId())
                .orElseGet(() -> {
                    Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
                    Tutorial tutorial = tutorialRepository.findById(dto.getTutorialId())
                            .orElseThrow(() -> new ResourceNotFoundException("Tutorial no encontrado"));
                    
                   return Visualizacion.builder()
                    .usuario(usuario)
                    .tutorial(tutorial)
                    .contadorReproducciones(1) 
                    .progresoSegundos(0)
                    .completado(false)
                    .build();
                            });

        //  Actualizar datos de progreso
        visualizacion.setProgresoSegundos(dto.getProgresoSegundos());
        
        if (Boolean.FALSE.equals(visualizacion.getCompletado())) {
            Integer duracionMin = visualizacion.getTutorial().getDuracionMin();
            
            // Verificamos que el tutorial realmente tenga una duración asignada en la BD
            if (duracionMin != null && duracionMin > 0) {
                double duracionTotalSegundos = duracionMin * 60.0;
                if (dto.getProgresoSegundos() >= (duracionTotalSegundos * 0.9)) {
                    visualizacion.setCompletado(true);
                    log.info("Tutorial ID {} marcado como COMPLETADO para usuario {}", dto.getTutorialId(), dto.getUsuarioId());
                }
            }
        }

        //  GUARDAR y MAPEAR
        Visualizacion guardada = visualizacionRepository.save(visualizacion);
        VisualizacionResponseDto response = visualizacionMapper.toResponseDto(guardada);

        // Contar vistas totales
        long totalVistas = visualizacionRepository.countByTutorialId(dto.getTutorialId());
        response.setContadorReproducciones((int) totalVistas);

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public List<VisualizacionResponseDto> obtenerContinuarViendo(Long usuarioId) {
        return visualizacionRepository.findByUsuarioIdAndCompletadoFalseOrderByUltimaActualizacionDesc(usuarioId)
                .stream()
                .map(visualizacionMapper::toResponseDto)
                .collect(Collectors.toList());
    }

@Override
@Transactional 
public VisualizacionResponseDto obtenerProgreso(Long usuarioId, Long tutorialId) {
    Visualizacion v = visualizacionRepository.findByUsuarioIdAndTutorialId(usuarioId, tutorialId)
            .orElse(null);

    if (v == null) {
        return VisualizacionResponseDto.builder()
                .progresoSegundos(0)
                .contadorReproducciones(0)
                .build();
    }

    // Lógica de incremento
    int actual = (v.getContadorReproducciones() == null) ? 0 : v.getContadorReproducciones();
    v.setContadorReproducciones(actual + 1);
    
    // Forzamos el guardado
    v = visualizacionRepository.saveAndFlush(v); 

    VisualizacionResponseDto responseDto = visualizacionMapper.toResponseDto(v);
    
    long totalVistasGlobales = visualizacionRepository.countByTutorialId(tutorialId);
    responseDto.setContadorReproducciones((int) totalVistasGlobales); 
    
    return responseDto;
}
    @Override
    @Transactional(readOnly = true)
    public List<TutorialResponseDto> obtenerPopulares() {
        log.info("Calculando los 3 tutoriales más populares");

        // Solicitamos los 3 mejores (página 0, tamaño 3)
        List<Tutorial> populares = visualizacionRepository.findPopularTutorials(PageRequest.of(0, 3));

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
    @Transactional
    public void borrarDeHistorial(Long id) {
        if (!visualizacionRepository.existsById(id))
            throw new ResourceNotFoundException("Registro no encontrado");
        visualizacionRepository.deleteById(id);
    }
}