package com.backend.service;

import com.backend.domain.Rutina;
import com.backend.domain.Usuario;
import com.backend.domain.Entrenamiento;
import com.backend.dto.RutinaRequestDto;
import com.backend.dto.RutinaResponseDto;
import com.backend.exceptions.ResourceNotFoundException;
import com.backend.mapper.RutinaMapper;
import com.backend.repository.RutinaRepository;
import com.backend.repository.UsuarioRepository;
import com.backend.repository.EntrenamientoRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RutinaServiceImp implements IRutinaService {

    private final RutinaRepository rutinaRepository;
    private final UsuarioRepository usuarioRepository;
    private final EntrenamientoRepository entrenamientoRepository;
    private final RutinaMapper rutinaMapper;

    @Override
    @Transactional
    public RutinaResponseDto asignarEntrenamiento(RutinaRequestDto dto) {
        log.info("Asignando entrenamiento ID {} al usuario ID {}", dto.getEntrenamientoId(), dto.getUsuarioId());

        // 1. Buscar Usuario y Entrenamiento
        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        
        Entrenamiento entrenamiento = entrenamientoRepository.findById(dto.getEntrenamientoId())
                .orElseThrow(() -> new ResourceNotFoundException("Entrenamiento no encontrado"));

        // 2. Mapear DTO a Entidad y setear relaciones manualmente
        Rutina rutina = rutinaMapper.toEntity(dto);
        rutina.setUsuario(usuario);
        rutina.setEntrenamiento(entrenamiento);

        // 3. Guardar y retornar
        Rutina guardada = rutinaRepository.save(rutina);
        return rutinaMapper.toResponseDto(guardada);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RutinaResponseDto> obtenerRutinaDiaria(Long usuarioId, LocalDate fecha) {
        log.info("Obteniendo rutina para usuario {} en fecha {}", usuarioId, fecha);
        
        return rutinaRepository.findByUsuarioIdAndFechaAsignacionOrderByOrdenAsc(usuarioId, fecha)
                .stream()
                .map(rutinaMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public RutinaResponseDto marcarComoCompletada(Long id) {
        log.info("Marcando rutina ID {} como completada", id);
        
        Rutina rutina = rutinaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Asignación de rutina no encontrada"));
        
        rutina.setCompletado(true);
        return rutinaMapper.toResponseDto(rutinaRepository.save(rutina));
    }

    @Override
    @Transactional
    public void eliminarAsignacion(Long id) {
        log.info("Eliminando asignación de rutina ID {}", id);
        if (!rutinaRepository.existsById(id)) {
            throw new ResourceNotFoundException("No existe la rutina con ID: " + id);
        }
        rutinaRepository.deleteById(id);
    }
}