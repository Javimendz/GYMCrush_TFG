package com.backend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.backend.domain.Entrenamiento;
import com.backend.dto.EntrenamientoResponseDto;
import com.backend.mapper.EntrenamientoMapper;
import com.backend.repository.EntrenamientoRepository;

@Service
public class EntrenamientoServiceImp implements IEntrenamientoService {

    @Autowired
    private EntrenamientoRepository entrenamientoRepository;

    @Autowired
    private EntrenamientoMapper entrenamientoMapper;

    @Override
    public List<EntrenamientoResponseDto> listarPorUsuario(Long usuarioId) {
        // 1. Obtener entidades de la DB
        List<Entrenamiento> entrenamientos = entrenamientoRepository.findByUsuarioId(usuarioId);
        
        // 2. Convertir la lista de entidades a lista de DTOs usando el mapper
        return entrenamientos.stream()
                .map(entrenamientoMapper::entrenamientoToEntrenamientoDTO)
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public List<EntrenamientoResponseDto> listarTodos() {
        List<Entrenamiento> entrenamientos = entrenamientoRepository.findAll();
        return entrenamientos.stream()
                .map(entrenamientoMapper::entrenamientoToEntrenamientoDTO)
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public EntrenamientoResponseDto guardar(EntrenamientoResponseDto dto) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'guardar'");
    }
}
