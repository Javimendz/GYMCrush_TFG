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
    //puede causar problema de consultas N+1 (una consulta para todos los perfiles y luego una consulta extra por cada perfil para su salud
    public List<PerfilResponseDto> findAll() {

        //Creo una lista de perfiles
       List<Perfil> perfiles = perfilRepository.findAll();

    //  Uso el mapper para convertir toda la lista a DTOs
    List<PerfilResponseDto> dtos = perfilMapper.toDtoList(perfiles);

    //  Para cada DTO, busco su salud actual y la inyecto
    dtos.forEach(dto -> {
        // Busco el perfil para obtener el usuario
        Perfil p = perfiles.stream().filter(per -> per.getId().equals(dto.getId())).findFirst().get();
        
        saludRepository.findFirstByUsuarioOrderByFechaMedicionDesc(p.getUsuario())
            .ifPresent(salud -> {
                dto.setPeso(salud.getPeso());
                dto.setEstatura(salud.getEstatura());
                dto.setImc(salud.getImc());
                dto.setNivelActividad(salud.getNivelActividad());
            });
    });

    return dtos;
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
