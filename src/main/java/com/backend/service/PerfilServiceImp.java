package com.backend.service;

import com.backend.domain.Perfil;
import com.backend.dto.PerfilResponseDto;
import com.backend.mapper.PerfilMapper;
import com.backend.repository.PerfilRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PerfilServiceImp implements IPerfilService {

    private final PerfilRepository perfilRepository;
    private final PerfilMapper perfilMapper;

    @Override
    public PerfilResponseDto obtenerPerfilPorUsuario(Long usuarioId) {
        //Buscamos por el ID del usuario, no del perfil
        Perfil perfil = perfilRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new RuntimeException("Perfil no encontrado para el usuario ID: " + usuarioId));
        return perfilMapper.perfilToPerfilDTO(perfil);
    }

    @Override
    @Transactional
    public PerfilResponseDto actualizarPerfil(Long usuarioId, PerfilResponseDto updateDto) {
        //Buscamos por el ID del usuario para saber qué perfil editar
        Perfil perfil = perfilRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new RuntimeException("No existe el perfil para este usuario"));

        // Actualizamos los campos
        perfil.setNombre(updateDto.getNombre());
        perfil.setApellidos(updateDto.getApellidos());
        perfil.setTelefono(updateDto.getTelefono());
        perfil.setDni(updateDto.getDni());
        perfil.setFechaNacimiento(updateDto.getFechaNacimiento());
        perfil.setGenero(updateDto.getGenero());
        perfil.setFoto(updateDto.getFoto());

        return perfilMapper.perfilToPerfilDTO(perfilRepository.save(perfil));
    }
}
