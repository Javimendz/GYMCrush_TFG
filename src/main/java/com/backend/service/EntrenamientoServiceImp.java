package com.backend.service;

import com.backend.domain.Entrenamiento;
import com.backend.domain.Usuario;
import com.backend.dto.EntrenamientoRequestDto;
import com.backend.dto.EntrenamientoResponseDto;
import com.backend.repository.EntrenamientoRepository;
import com.backend.repository.UsuarioRepository;
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
    private final UsuarioRepository usuarioRepository;
    private final EntrenamientoMapper entrenamientoMapper;

    @Override
    @Transactional
    public EntrenamientoResponseDto crear(EntrenamientoRequestDto dto) {
        log.info("Creando sesión de entrenamiento para el usuario ID: {}", dto.getUsuarioId());
        
        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        Entrenamiento entrenamiento = entrenamientoMapper.toEntity(dto);
        entrenamiento.setUsuario(usuario);

        return entrenamientoMapper.toResponseDto(entrenamientoRepository.save(entrenamiento));
    }

    @Override
    @Transactional(readOnly = true)
    public EntrenamientoResponseDto obtenerPorId(Long id) {
        return entrenamientoRepository.findById(id)
                .map(entrenamientoMapper::toResponseDto)
                .orElseThrow(() -> new ResourceNotFoundException("Sesión de entrenamiento no encontrada"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<EntrenamientoResponseDto> listarPorUsuario(Long usuarioId) {
        return entrenamientoRepository.findByUsuarioId(usuarioId).stream()
                .map(entrenamientoMapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional
    public EntrenamientoResponseDto actualizar(Long id, EntrenamientoRequestDto dto) {
        Entrenamiento existente = entrenamientoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se puede actualizar: Entrenamiento no encontrado"));
        
        existente.setFecha(dto.getFecha());
        existente.setHora(dto.getHora());
        existente.setDuracion(dto.getDuracion());
        existente.setIntensidad(dto.getIntensidad());

        return entrenamientoMapper.toResponseDto(entrenamientoRepository.save(existente));
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