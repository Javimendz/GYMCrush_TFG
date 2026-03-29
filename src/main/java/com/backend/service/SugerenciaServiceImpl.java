package com.backend.service;

import com.backend.domain.SugerenciaDieta;
import com.backend.dto.SugerenciaDietaReponseDto;
import com.backend.dto.SugerenciaRequestDto;
import com.backend.mapper.SugerenciaMapper;
import com.backend.repository.SugerenciaDietaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SugerenciaServiceImpl implements ISugerenciaService {

    private final SugerenciaDietaRepository sugerenciaRepository;
    private final SugerenciaMapper sugerenciaMapper;

    @Override
    @Transactional
    public SugerenciaDietaReponseDto guardarAsignacion(SugerenciaRequestDto dto) {
        // Convertimos el DTO a entidad
        SugerenciaDieta entidad = sugerenciaMapper.toEntity(dto);
        
        // Seteamos la fecha actual antes de guardar
        entidad.setFechaGeneracion(LocalDateTime.now());
        
        // Guardamos y retornamos la respuesta mapeada
        return sugerenciaMapper.toResponseDto(sugerenciaRepository.save(entidad));
    }

    @Override
    @Transactional(readOnly = true)
    public List<SugerenciaDietaReponseDto> obtenerHistorial(Long usuarioId) {
        // Obtenemos el historial ordenado por fecha descendente (más reciente primero)
        return sugerenciaMapper.toResponseDtoList(
            sugerenciaRepository.findByUsuarioIdOrderByFechaGeneracionDesc(usuarioId)
        );
    }
}
