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
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ComidaDiariaServiceImpl implements IComidaDiariaService {

    private final ComidaDiariaRepository comidaRepository;
    private final NutricionRepository nutricionRepository;
    private final ComidaDiariaMapper comidaMapper;

    @Override
    @Transactional
    public void guardarComidas(List<ComidaDiariaRequestDto> comidasRequest, Long planId) {
        log.info("Iniciando guardado masivo de comidas para el plan ID: {}", planId);
        // Buscamos el plan padre
        Nutricion plan = nutricionRepository.findById(planId)
                .orElseThrow(() -> new RuntimeException("Plan nutricional no encontrado"));

      List<ComidaDiaria> entidades = comidasRequest.stream()
                .map(dto -> {
                    log.debug("Mapeando comida: {}", dto.getNombreAlimento());
                    return comidaMapper.toEntity(dto, plan);
                })
                .toList();

        comidaRepository.saveAll(entidades);
        log.info("Se han guardado {} comidas exitosamente", entidades.size());
    }

    @Override
    @Transactional
    public ComidaDiariaResponseDto añadirComida(ComidaDiariaRequestDto request) {
        log.info("Añadiendo comida individual '{}' al plan ID: {}", request.getNombreAlimento(), request.getPlanNutricionalId());
        Nutricion plan = nutricionRepository.findById(request.getPlanNutricionalId())
                .orElseThrow(() -> new RuntimeException("Plan no encontrado"));

        ComidaDiaria nuevaComida = comidaMapper.toEntity(request, plan);
        ComidaDiaria guardada = comidaRepository.save(nuevaComida);
        log.info("Comida guardada con éxito (ID: {})", guardada.getId());
        return comidaMapper.toResponseDto(guardada);
    }

    @Override
    @Transactional
    public void eliminarComida(Long id) {
        log.info("Solicitud para eliminar comida con ID: {}", id);
        if (!comidaRepository.existsById(id)) {
            log.warn("Intento de eliminar comida inexistente con ID: {}", id);
            throw new ResourceNotFoundException("Comida no encontrada con ID: " + id);
        }
        comidaRepository.deleteById(id);
        log.info("Comida ID {} eliminada correctamente", id);
    }

   
}