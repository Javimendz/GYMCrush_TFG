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

import java.time.LocalDate;
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
    log.info("Iniciando validación de sesión para usuario ID: {}", dto.getUsuarioId());
    
    LocalDate fechaSoloDia = dto.getFecha().toLocalDate();

    if (fechaSoloDia.isAfter(LocalDate.now())) {
        throw new IllegalArgumentException("No puedes registrar un entrenamiento para una fecha futura.");
    }

    // Validar si ya existe un entrenamiento ese mismo día a esa hora
    if (entrenamientoRepository.existsByUsuarioIdAndFechaAndHora(dto.getUsuarioId(), fechaSoloDia, dto.getHora())) {
        log.warn("Intento de duplicado: Usuario {} ya tiene sesión el {} a las {}", dto.getUsuarioId(), fechaSoloDia, dto.getHora());
        throw new IllegalArgumentException("Ya tienes un entrenamiento registrado para este día y hora.");
    }

    //  Buscar el usuario
    Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + dto.getUsuarioId()));

    //  Mapear DTO a Entidad
    Entrenamiento entrenamiento = entrenamientoMapper.toEntity(dto);
    
    //  Ajustes manuales para asegurar que los tipos coincidan
    entrenamiento.setUsuario(usuario);
    entrenamiento.setFecha(fechaSoloDia); // Seteamos el LocalDate extraído
    
    entrenamiento.setCantidadEjercicios(dto.getCantidadEjercicios()); 

    // Guardar y retornar
    Entrenamiento guardado = entrenamientoRepository.save(entrenamiento);
    log.info("Entrenamiento guardado exitosamente con ID: {}", guardado.getId());
    
    return entrenamientoMapper.toResponseDto(guardado);
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

   public EntrenamientoResponseDto actualizar(Long id, EntrenamientoRequestDto dto) {
    log.info("Iniciando actualización del entrenamiento ID: {}", id);

    // Buscar el entrenamiento existente
    Entrenamiento existente = entrenamientoRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("No se puede actualizar: Entrenamiento no encontrado"));

    //  Validar que el usuario que intenta editar es el dueño real
    if (!existente.getUsuario().getId().equals(dto.getUsuarioId())) {
        log.error("Intento de violación de seguridad: Usuario {} intentó editar entrenamiento de Usuario {}", 
                  dto.getUsuarioId(), existente.getUsuario().getId());
        throw new IllegalArgumentException("No tienes permiso para modificar este entrenamiento.");
    }

    LocalDate fechaNueva = dto.getFecha().toLocalDate();
    if (fechaNueva.isAfter(LocalDate.now())) {
        throw new IllegalArgumentException("No puedes actualizar un entrenamiento con una fecha futura.");
    }

    existente.setFecha(fechaNueva);
    existente.setHora(dto.getHora());
    existente.setDuracion(dto.getDuracion());
    existente.setIntensidad(dto.getIntensidad());
    existente.setCantidadEjercicios(dto.getCantidadEjercicios());

    // 5. GUARDAR
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
