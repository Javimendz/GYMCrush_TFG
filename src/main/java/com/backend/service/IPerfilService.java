package com.backend.service;

import com.backend.dto.PerfilResponseDto;

public interface IPerfilService {
    PerfilResponseDto obtenerPerfilPorUsuario(Long id);
    PerfilResponseDto actualizarPerfil(Long id, PerfilResponseDto perfilResponseDto);
}