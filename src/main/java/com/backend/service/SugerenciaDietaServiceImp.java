package com.backend.service;

import com.backend.domain.SugerenciaDieta;
import com.backend.dto.SugerenciaDietaReponseDto;
import com.backend.dto.SugerenciaDietaRequestDto;
import com.backend.mapper.SugerenciaDietaMapper;
import com.backend.repository.SugerenciaDietaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SugerenciaDietaServiceImp implements ISugerenciaDietaService {

    private final SugerenciaDietaRepository sugerenciaRepository;
    private final SugerenciaDietaMapper sugerenciaMapper;

    @Override
    @Transactional
    public SugerenciaDietaReponseDto guardarAsignacion(SugerenciaDietaRequestDto dto) {
        // Asignación MANUAL pura y dura, sin fatsecret ni nada, solo guardamos lo que nos envían
        SugerenciaDieta entidad = sugerenciaMapper.toEntity(dto);
        entidad.setFechaGeneracion(LocalDateTime.now());
        
        // La dieta  ya tiene su JSON base, así que solo la guardamos
        return sugerenciaMapper.toResponseDto(sugerenciaRepository.save(entidad));
    }
    @Override
    @Transactional(readOnly = true)
    public List<SugerenciaDietaReponseDto> obtenerHistorial(Long usuarioId) {
        // Obtenemos el historial ordenado por fecha descendente (más reciente primero)
        return sugerenciaMapper.toResponseDtoList(
                sugerenciaRepository.findByUsuarioIdOrderByFechaGeneracionDesc(usuarioId));
    }

}