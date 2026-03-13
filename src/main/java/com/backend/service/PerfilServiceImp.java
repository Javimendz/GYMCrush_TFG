package com.backend.service;

import com.backend.domain.Perfil;
import com.backend.dto.PerfilResponseDto;
import com.backend.mapper.PerfilMapper;
import com.backend.repository.PerfilRespository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor // Inyecta las dependencias automáticamente (Repo y Mapper)
public class PerfilServiceImp implements IPerfilService {

    private final PerfilRespository perfilRepository;
    private final PerfilMapper perfilMapper;

    @Override
    public PerfilResponseDto obtenerPerfilPorUsuario(Long usuarioId) {
        Perfil perfil = perfilRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Perfil no encontrado"));
        return perfilMapper.perfilToPerfilDTO(perfil);
    }

    @Override
    public PerfilResponseDto actualizarPerfil(Long id, PerfilResponseDto updateDto) {
    // 1. Buscamos el perfil actual por el ID del usuario
    Perfil perfil = perfilRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("No existe el perfil para este usuario"));

    // 2. Mapeo manual de datos (Actualizamos el objeto de la BD con lo que viene del DTO)
    perfil.setNombre(updateDto.getNombre());
    perfil.setApellidos(updateDto.getApellidos());
    perfil.setTelefono(updateDto.getTelefono());
    perfil.setDni(updateDto.getDni());
    perfil.setFechaNacimiento(updateDto.getFechaNacimiento());
    perfil.setGenero(updateDto.getGenero());
    perfil.setFoto(updateDto.getFoto());

    // 3. Guardamos en la base de datos
    Perfil perfilGuardado = perfilRepository.save(perfil);

    // 4. Devolvemos el resultado convertido a ResponseDTO
    return perfilMapper.perfilToPerfilDTO(perfilGuardado);
}

    
}
