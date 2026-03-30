package com.backend.service;

import com.backend.domain.CategoriaDieta;
import com.backend.domain.Dieta;
import com.backend.dto.*;
import com.backend.exceptions.ResourceNotFoundException;
import com.backend.mapper.DietaMapper;
import com.backend.repository.CategoriaDietaRepository;
import com.backend.repository.DietaRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DietaServiceImp implements IDietaService {
    private final DietaRepository dietaRepository;
    private final DietaMapper dietaMapper;
    private final CategoriaDietaRepository categoriaRepository;

    @Override
    @Transactional
    @PreAuthorize("hasRole('ROLE_ADMIN')")

    public DietaResponseDto crearDieta(DietaRequestDto dto) {
        //  Convertimos a entidad
        Dieta dieta = dietaMapper.toEntity(dto);

        // Buscamos la categoría y la asignamos 
        if (dto.getCategoriaDietaId() != null) {
            CategoriaDieta categoria = categoriaRepository.findById(dto.getCategoriaDietaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada"));
            dieta.setCategoriaDieta(categoria);
        }

        // Cálculo dinámico de calorías 
        int kcal = (dto.getCantidadProteinas() * 4) +
                (dto.getCantidadCarbohidratos() * 4) +
                (dto.getCantidadGrasas() * 9);
        dieta.setObjetivoCalorico(kcal);

        //  Guardar y devolver
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