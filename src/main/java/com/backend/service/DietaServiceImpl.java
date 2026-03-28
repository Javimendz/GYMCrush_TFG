package com.backend.service;

import com.backend.domain.Dieta;
import com.backend.dto.*;
import com.backend.mapper.DietaMapper;
import com.backend.repository.DietaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DietaServiceImpl implements IDietaService {
    private final DietaRepository dietaRepository;
    private final DietaMapper dietaMapper;

    @Override
    @Transactional
    public DietaResponseDto crearDieta(DietaRequestDto dto) {
        Dieta dieta = dietaMapper.toEntityCreate(dto);
        int kcal = (dto.getCantidadProteinas() * 4) + (dto.getCantidadCarbohidratos() * 4) + (dto.getCantidadGrasas() * 9);
        dieta.setCantidadTotal(kcal);
        return dietaMapper.toResponseDto(dietaRepository.save(dieta));
    }

    @Override
    public List<DietaResponseDto> listarTodas() {
        return dietaMapper.toResponseDtoList(dietaRepository.findAll());
    }

    @Override
    public DietaResponseDto obtenerPorId(Long id) {
        return dietaRepository.findById(id)
                .map(dietaMapper::toResponseDto)
                .orElseThrow(() -> new RuntimeException("Dieta no encontrada"));
    }

    @Override
    public void eliminar(Long id) {
        dietaRepository.deleteById(id);
    }
    
   
}