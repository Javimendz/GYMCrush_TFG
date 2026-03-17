package com.backend.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

import com.backend.domain.Actividad;
import com.backend.domain.Horario;
import com.backend.dto.HorarioResponseDto;
import com.backend.mapper.HorarioMapper;
import com.backend.repository.ActividadRepository;
import com.backend.repository.HorarioRepository;

@Service
@RequiredArgsConstructor // Inyecta automáticamente los campos 'final'
public class HorarioServiceImp implements IHorarioService {

    private final HorarioRepository horarioRepository;
    private final ActividadRepository actividadRepository;
    private final HorarioMapper horarioMapper;

    @Override
    @Transactional(readOnly = true)
    public List<HorarioResponseDto> listarTodos() {
        List<Horario> horarios = horarioRepository.findAll();
        return horarioMapper.toResponseDtoList(horarios);
    }

    @Override
    @Transactional
    public HorarioResponseDto crear(HorarioResponseDto dto) {
        // 1. Usamos el mapper para crear la entidad base (id y actividad ignorados)
        Horario nuevoHorario = horarioMapper.toEntityCreate(dto);
        
        // 2. Buscamos la actividad real en la BD usando el ID que viene en el DTO
        Actividad actividad = actividadRepository.findById(dto.getActividadId())
                .orElseThrow(() -> new RuntimeException("No se puede crear el horario: Actividad no encontrada"));
        
        // 3. Establecemos la relación (vínculo Actividad <-> Horario)
        nuevoHorario.setActividad(actividad);
        
        // 4. Guardamos y devolvemos el resultado mapeado
        return horarioMapper.toResponseDto(horarioRepository.save(nuevoHorario));
    }

    @Override
    @Transactional
    public HorarioResponseDto actualizar(Long id, HorarioResponseDto dto) {
        Horario existente = horarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Horario no encontrado para actualizar"));
        
        // Actualizamos los campos básicos (día, horas, aforo)
        horarioMapper.updateHorarioFromDto(dto, existente);
        
        // Si el DTO pide cambiar de actividad, la buscamos y actualizamos
        if (dto.getActividadId() != null) {
            Actividad nuevaActividad = actividadRepository.findById(dto.getActividadId())
                    .orElseThrow(() -> new RuntimeException("Actividad no encontrada"));
            existente.setActividad(nuevaActividad);
        }
        
        return horarioMapper.toResponseDto(horarioRepository.save(existente));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        if (!horarioRepository.existsById(id)) {
            throw new RuntimeException("No se puede eliminar: El horario no existe");
        }
        horarioRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public HorarioResponseDto obtenerPorId(Long id) {
        Horario horario = horarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Horario no encontrado"));
        return horarioMapper.toResponseDto(horario);
    }

    @Override
    @Transactional(readOnly = true)
    public List<HorarioResponseDto> listarPorDia(String dia) {
        // Este método usa el query method que definimos en el Repository
        return horarioMapper.toResponseDtoList(
                horarioRepository.findByDiaSemanaIgnoreCase(dia)
        );
    }
}