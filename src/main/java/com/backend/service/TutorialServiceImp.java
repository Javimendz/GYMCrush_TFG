
package com.backend.service;

import com.backend.domain.CategoriaTutorial;
import com.backend.domain.Tutorial;
import com.backend.dto.CategoriaTutorialResponseDto;
import com.backend.dto.TutorialRequestDto;
import com.backend.dto.TutorialResponseDto;
import com.backend.mapper.CategoriaTutorialMapper;
import com.backend.mapper.TutorialMapper;
import com.backend.repository.CategoriaTutorialRepository;
import com.backend.repository.TutorialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TutorialServiceImp implements ITutorialService {

    private final TutorialRepository tutorialRepository;
    private final CategoriaTutorialRepository categoriaRepository;
    private final TutorialMapper tutorialMapper;
    private final CategoriaTutorialMapper categoriaMapper;

    @Override
    @Transactional(readOnly = true)
    public List<TutorialResponseDto> obtenerTodos() {
        return tutorialRepository.findAll().stream()
                .map(tutorialMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TutorialResponseDto> obtenerPorCategoria(Long categoriaId) {
        return tutorialRepository.findByCategoriaId(categoriaId).stream()
                .map(tutorialMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public TutorialResponseDto crear(TutorialRequestDto dto) {
        // 1. Buscamos la categoría real en la BD
        CategoriaTutorial categoria = categoriaRepository.findById(dto.getCategoriaId())
                .orElseThrow(() -> new RuntimeException("Error: La categoría seleccionada no existe"));

        // 2. Mapeamos el DTO a Entidad
        Tutorial tutorial = tutorialMapper.toEntity(dto);
        
        // 3. Establecemos la relación manualmente
        tutorial.setCategoria(categoria);

        // 4. Guardamos y devolvemos el DTO de respuesta
        return tutorialMapper.toDto(tutorialRepository.save(tutorial));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        if (!tutorialRepository.existsById(id)) {
            throw new RuntimeException("No se puede eliminar: El tutorial no existe");
        }
        tutorialRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoriaTutorialResponseDto> listarCategorias() {
        // Buscamos todas las categorías y las mapeamos a DTO
        return categoriaRepository.findAll().stream()
                .map(categoriaMapper::toDto)
                .toList();
    }
}