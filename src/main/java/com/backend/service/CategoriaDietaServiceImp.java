package com.backend.service;

import com.backend.domain.CategoriaDieta;
import com.backend.dto.CategoriaDietaRequestDto;
import com.backend.dto.CategoriaDietaResponseDto;
import com.backend.exceptions.ResourceNotFoundException;
import com.backend.mapper.CategoriaDietaMapper;
import com.backend.repository.CategoriaDietaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoriaDietaServiceImp implements ICategoriaDietaService {

    private final CategoriaDietaRepository categoriaRepository;
    private final CategoriaDietaMapper categoriaMapper;

    @Override
    @Transactional
    public CategoriaDietaResponseDto crear(CategoriaDietaRequestDto dto) {
        // Validar si ya existe para evitar errores de duplicado
        if (categoriaRepository.existsByNombreIgnoreCase(dto.getNombre())) {
            throw new RuntimeException("La categoría " + dto.getNombre() + " ya existe.");
        }

        CategoriaDieta entidad = categoriaMapper.toEntity(dto);

        return categoriaMapper.toResponseDto(categoriaRepository.save(entidad));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoriaDietaResponseDto> listarTodas() {
        return categoriaMapper.toResponseDtoList(categoriaRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public CategoriaDietaResponseDto obtenerPorId(Long id) {
        return categoriaRepository.findById(id)
                .map(categoriaMapper::toResponseDto)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con ID: " + id));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        if (!categoriaRepository.existsById(id)) {
            throw new ResourceNotFoundException("No se puede eliminar: Categoría no encontrada");
        }
        categoriaRepository.deleteById(id);
    }
}