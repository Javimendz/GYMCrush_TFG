package com.backend.Controller;

import com.backend.domain.Usuario;
import com.backend.dto.UsuarioRequestDto;
import com.backend.dto.UsuarioResponseDto;
import com.backend.mapper.UsuarioMapper;
import com.backend.service.IUsuarioService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/usuario")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // Importante para conectar con el Frontend
public class UsuarioController {

    private final IUsuarioService usuarioService;
    private final UsuarioMapper usuarioMapper;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USUARIO')")
    public List<UsuarioResponseDto> getAllUsuarios() {
        List<Usuario> usuarios = usuarioService.getAllUsuarios();
        return usuarioMapper.toUsuarioResponseDtoList(usuarios);
    }

    @PostMapping("/crear")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UsuarioResponseDto> createUsuario(@Valid @RequestBody UsuarioRequestDto usuarioDto) {
        Usuario usuarioToSave = usuarioMapper.toEntity(usuarioDto);
        Usuario savedUsuario = usuarioService.save(usuarioToSave);
        UsuarioResponseDto responseDto = usuarioMapper.toUsuarioResponseDto(savedUsuario);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDto> getUsuarioById(@PathVariable Long id) {
        Usuario usuario = usuarioService.findById(id);
        UsuarioResponseDto responseDto = usuarioMapper.toUsuarioResponseDto(usuario);
        return ResponseEntity.ok(responseDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponseDto> updateUsuario(@PathVariable Long id,
            @Valid @RequestBody UsuarioRequestDto usuarioDto) {
        Usuario updatedUsuario = usuarioService.findById(id);
        usuarioMapper.updateUsuarioFromDto(usuarioDto, updatedUsuario);
        Usuario updateUsuario = usuarioService.save(updatedUsuario);
        return ResponseEntity.ok(usuarioMapper.toUsuarioResponseDto(updateUsuario));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUsuario(@PathVariable Long id) {
        usuarioService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}