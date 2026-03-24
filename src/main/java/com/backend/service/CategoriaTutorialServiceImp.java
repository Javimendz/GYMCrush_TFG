
package com.backend.service;

import com.backend.domain.CategoriaTutorial;
import com.backend.dto.CategoriaTutorialRequestDto;
import com.backend.dto.CategoriaTutorialResponseDto;
import com.backend.mapper.CategoriaTutorialMapper;
import com.backend.repository.CategoriaTutorialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoriaTutorialServiceImp implements ICategoriaTutorialService {

    private final CategoriaTutorialRepository categoriaRepository;
    private final CategoriaTutorialMapper categoriaMapper;

    @Override
    @Transactional(readOnly = true)
    public List<CategoriaTutorialResponseDto> listarTodas() {
        return categoriaRepository.findAll().stream()
                .map(categoriaMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public CategoriaTutorialResponseDto crear(CategoriaTutorialRequestDto dto) {
        CategoriaTutorial entity = categoriaMapper.toEntity(dto);
        return categoriaMapper.toDto(categoriaRepository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public CategoriaTutorialResponseDto buscarPorId(Long id) {
        CategoriaTutorial cat = categoriaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada con ID: " + id));
        return categoriaMapper.toDto(cat);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        if (!categoriaRepository.existsById(id)) {
            throw new RuntimeException("La categoría no existe");
        }
        categoriaRepository.deleteById(id);
    }
}