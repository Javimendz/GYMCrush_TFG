package com.backend.service;

import com.backend.domain.ComidaDiaria;
import com.backend.domain.Nutricion;
import com.backend.dto.ComidaDiariaRequestDto;
import com.backend.dto.ComidaDiariaResponseDto;
import com.backend.exceptions.ResourceNotFoundException;
import com.backend.mapper.ComidaDiariaMapper;
import com.backend.repository.ComidaDiariaRepository;
import com.backend.repository.NutricionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ComidaDiariaServiceImpl implements IComidaDiariaService {

    private final ComidaDiariaRepository comidaRepository;
    private final NutricionRepository nutricionRepository;
    private final ComidaDiariaMapper comidaMapper;

    @Override
    @Transactional
    public void guardarComidas(List<ComidaDiariaRequestDto> comidasRequest, Long planId) {
        // Buscamos el plan padre
        Nutricion plan = nutricionRepository.findById(planId)
                .orElseThrow(() -> new RuntimeException("Plan nutricional no encontrado"));

        // Convertimos los DTOs en Entidades y las guardamos
        List<ComidaDiaria> entidades = comidasRequest.stream()
                .map(dto -> comidaMapper.toEntity(dto, plan))
                .toList();

        comidaRepository.saveAll(entidades);
    }

    @Override
    @Transactional
    public ComidaDiariaResponseDto añadirComida(ComidaDiariaRequestDto request) {
        Nutricion plan = nutricionRepository.findById(request.getPlanNutricionalId())
                .orElseThrow(() -> new RuntimeException("Plan no encontrado"));

        ComidaDiaria nuevaComida = comidaMapper.toEntity(request, plan);
        ComidaDiaria guardada = comidaRepository.save(nuevaComida);
        
        return comidaMapper.toResponseDto(guardada);
    }

    @Override
    @Transactional
    public void eliminarComida(Long id) {
        if (!comidaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Comida no encontrada con ID: " + id);
        }
        comidaRepository.deleteById(id);
    }

   
}