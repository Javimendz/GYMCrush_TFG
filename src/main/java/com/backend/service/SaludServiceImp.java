package com.backend.service;

import java.util.List;

import com.backend.domain.Salud;
import com.backend.domain.Usuario;
import com.backend.dto.SaludRequestDto;
import com.backend.dto.SaludResponseDto;
import com.backend.exceptions.ResourceNotFoundException;
import com.backend.mapper.SaludMapper;
import com.backend.repository.SaludRepository;
import com.backend.repository.UsuarioRepository;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Data
@Transactional
public class SaludServiceImp implements ISaludService {

    private final SaludRepository saludRepository;
    private final UsuarioRepository usuarioRepository;
    private final SaludMapper saludMapper;

    @Override
    public SaludResponseDto registrarProgreso(SaludRequestDto dto, Long usuarioId) {

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + usuarioId));

        Salud salud = saludMapper.toEntity(dto);

        salud.setUsuario(usuario);

        Salud guardada = saludRepository.save(salud);

        return saludMapper.toDto(guardada);
    }

    @Override
    public List<SaludResponseDto> obtenerHistorialPorUsuario(Long usuarioId) {

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + usuarioId));

        List<Salud> historial = saludRepository.findByUsuarioOrderByFechaMedicionDesc(usuario);

        return saludMapper.toDtoList(historial);

    }

    @Override
public SaludResponseDto obtenerProgresoActual(Long usuarioId) {
    // Buscamos al usuario
    Usuario usuario = usuarioRepository.findById(usuarioId)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + usuarioId));

     //Salud actual, sino tratamos excepcion
    Salud actual = saludRepository.findFirstByUsuarioOrderByFechaMedicionDesc(usuario)
            .orElseThrow(() -> new ResourceNotFoundException("El usuario aún no tiene registros de salud"));

    //  Mapeamos y devolvemos el registro más reciente
    return saludMapper.toDto(actual);
}
    @Override
    public List<SaludResponseDto> obtenerTodosLosRegistros() {

        return saludMapper.toDtoList(saludRepository.findAll());
    }

    @Override
    public void eliminarRegistro(Long id) {
        
        if(!saludRepository.existsById(id)) {
            throw new ResourceNotFoundException("Registro no encontrado con id: " + id);
        }
        
        saludRepository.deleteById(id);
    }

}
