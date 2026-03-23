package com.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.domain.Perfil;
import com.backend.dto.PerfilRequestDto;
import com.backend.dto.PerfilResponseDto;
import com.backend.exceptions.ResourceNotFoundException;
import com.backend.mapper.PerfilMapper;
import com.backend.repository.PerfilRepository;
import com.backend.repository.SaludRepository;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@Service
public class PerfilServiceImp implements IPerfilService {

    private final PerfilRepository perfilRepository;
    private final SaludRepository saludRepository;
    private final PerfilMapper perfilMapper;

   @Override
@Transactional(readOnly = true)
public List<PerfilResponseDto> findAll() {
    List<Perfil> perfiles = perfilRepository.findAll();
    
    return perfiles.stream().map(perfil -> {
        //  Convertimos a DTO
        PerfilResponseDto dto = perfilMapper.toDto(perfil);
        
        // Enriquecemos con salud 
        saludRepository.findFirstByUsuarioOrderByFechaMedicionDesc(perfil.getUsuario())
            .ifPresent(salud -> {
                dto.setPeso(salud.getPeso());
                dto.setEstatura(salud.getEstatura());
                dto.setImc(salud.getImc());
                dto.setNivelActividad(salud.getNivelActividad());
            });
        return dto;
    }).toList();
}

    @Override
    @Transactional
    public Perfil save(Perfil perfil) {

        return perfilRepository.save(perfil);
    }

    @Override
    @Transactional(readOnly = true)
    public PerfilResponseDto findById(Long id) {

        Perfil perfil = perfilRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Perfil no encontrado"));
        
        PerfilResponseDto  dto = perfilMapper.toDto(perfil);
        
        saludRepository.findFirstByUsuarioOrderByFechaMedicionDesc(perfil.getUsuario())
        .ifPresent(salud -> {
            dto.setPeso(salud.getPeso());
            dto.setEstatura(salud.getEstatura());
            dto.setImc(salud.getImc());
            dto.setNivelActividad(salud.getNivelActividad());
        });

        return dto;
    }

    @Override
    @Transactional
    public void deleteById(Long id) {

        if (!perfilRepository.existsById(id)) {
            throw new ResourceNotFoundException("Perfil no encontrado");
        }
        perfilRepository.deleteById(id);
    }

    @Override
@Transactional
public PerfilResponseDto update(Long id, PerfilRequestDto dto) {
    //  busco el perfil por id
    Perfil perfil = perfilRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Perfil no encontrado"));

    // uso el maper para actualizar
    perfilMapper.updatePerfilFromDto(dto, perfil);

    // guardo entidad actualizada
    Perfil actualizado = perfilRepository.save(perfil);

    // devuelvo el dto actualizado
    return perfilMapper.toDto(actualizado);
}
}
