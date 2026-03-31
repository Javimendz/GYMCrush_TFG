package com.backend.service;

import com.backend.domain.CategoriaDieta;
import com.backend.domain.Dieta;
import com.backend.dto.DietaRequestDto;
import com.backend.dto.DietaResponseDto;
import com.backend.exceptions.ResourceNotFoundException;
import com.backend.mapper.DietaMapper;
import com.backend.repository.CategoriaDietaRepository;
import com.backend.repository.DietaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DietaServiceImp implements IDietaService {

    private final DietaRepository dietaRepository;
    private final DietaMapper dietaMapper;
    private final CategoriaDietaRepository categoriaRepository;

    @Override
    @Transactional
    public DietaResponseDto crearDieta(DietaRequestDto dto) {
        log.info("Creando dieta base: {}", dto.getNombre());

        // Convertimos a entidad
        Dieta dieta = dietaMapper.toEntity(dto);

        // Asignamos la categoría
        if (dto.getCategoriaDietaId() != null) {
            CategoriaDieta categoria = categoriaRepository.findById(dto.getCategoriaDietaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con ID: "
                            + dto.getCategoriaDietaId()));
            dieta.setCategoriaDieta(categoria);
        }

       
        Dieta guardada = dietaRepository.save(dieta);
        log.info("Dieta base creada con ID: {}", guardada.getId());
        return dietaMapper.toResponseDto(guardada);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DietaResponseDto> listarTodas() {
        log.info("Listando todas las dietas del catálogo");
        return dietaMapper.toResponseDtoList(dietaRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public DietaResponseDto obtenerPorId(Long id) {
        log.info("Buscando dieta con ID: {}", id);
        return dietaRepository.findById(id)
                .map(dietaMapper::toResponseDto)
                .orElseThrow(() -> new ResourceNotFoundException("Dieta no encontrada con ID: " + id));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        log.info("Eliminando dieta con ID: {}", id);
        if (!dietaRepository.existsById(id)) {
            throw new ResourceNotFoundException("No se puede eliminar: Dieta no encontrada con ID: " + id);
        }
        dietaRepository.deleteById(id);
        log.info("Dieta {} eliminada correctamente", id);
    }
}