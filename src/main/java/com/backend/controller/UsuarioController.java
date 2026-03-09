package com.backend.controller;

import com.backend.domain.Usuario;
import com.backend.dto.UsuarioRequestDto;
import com.backend.dto.UsuarioResponseDto;
import com.backend.mapper.UsuarioMapper;
import com.backend.service.IUsuarioService;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.web.bind.annotation.*;


@RestController() // Controlador REST para manejar solicitudes HTTP relacionadas con usuarios
@RequestMapping("/api/v1/usuario") // Mapea todas las solicitudes HTTP a /api/v1/usuario
@RequiredArgsConstructor
public class UsuarioController {
    

    private final IUsuarioService usuarioService;
    private final UsuarioMapper usuarioMapper;

  
    @GetMapping
    public List<UsuarioResponseDto> getAllUsuarios() {

            List<Usuario> usuarios = usuarioService.getAllUsuarios();
            return usuarioMapper.toUsuarioResponseDtoList(usuarios);
    }
    
    @PostMapping("/crear")
    public UsuarioResponseDto createUsuario(@RequestBody UsuarioRequestDto usuarioDto) {
          
        Usuario usuarioToSave = usuarioMapper.toEntity(usuarioDto);
        Usuario savedUsuario = usuarioService.save(usuarioToSave);
        return usuarioMapper.toUsuarioResponseDto(savedUsuario);
    }
}
