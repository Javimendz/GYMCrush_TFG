package com.backend.service;

import java.util.List;

import com.backend.dto.AccesoResponseDto;

public interface IAccesoService {

    void registrarEntrada(String token);
    void registrarSalida(String token);
    List<AccesoResponseDto> obtenerHistorialUsuario(String username);
}
