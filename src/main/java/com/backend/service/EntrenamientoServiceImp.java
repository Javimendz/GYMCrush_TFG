package com.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.backend.dto.EntrenamientoResponseDto;

@Service
public class EntrenamientoServiceImp implements IEntrenamientoService {

    @Override
    public EntrenamientoResponseDto guardar(EntrenamientoResponseDto dto) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'guardar'");
    }

    @Override
    public List<EntrenamientoResponseDto> listarPorUsuario(Integer usuarioId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'listarPorUsuario'");
    }

    @Override
    public Integer obtenerTotalMinutosMes(Integer usuarioId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'obtenerTotalMinutosMes'");
    }
    
}