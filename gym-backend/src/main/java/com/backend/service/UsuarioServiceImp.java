package com.backend.service;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.domain.Usuario;
import com.backend.dto.UsuarioRequestDto;
import com.backend.exceptions.ResourceNotFoundException;
import com.backend.mapper.UsuarioMapper;
import com.backend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Transactional
@Service
public class UsuarioServiceImp implements IUsuarioService {

    private final UsuarioRepository usuarioRepo;
    private final UsuarioMapper usuarioMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Usuario save(Usuario usuario) {
        log.info("Guardando usuario: {}", usuario.getUsername());
        return usuarioRepo.save(usuario);
    }

    @Override
    public Usuario generarTokenAcceso(Long id) {
        log.info("Generando token de acceso para usuario ID: {}", id);
        Usuario usuario = usuarioRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));
        
        usuario.setQrToken(java.util.UUID.randomUUID().toString());
        usuario.setTokenExpiraEn(LocalDateTime.now().plusSeconds(30));
        
        return usuarioRepo.save(usuario);
    }

    @Override
    public List<Usuario> getAllUsuarios() {
        log.info("Obteniendo todos los usuarios");
        return usuarioRepo.findAll();
    }

    @Override
    public Usuario findById(Long id) {
        log.info("Buscando usuario por ID: {}", id);
        return usuarioRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));
    }

    @Override
    @Transactional 
    public void deleteById(Long id) {
        log.info("Iniciando borrado integral del usuario ID: {}", id);

        Usuario usuario = usuarioRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));

        // 1. Desvincular roles
        if (usuario.getRol() != null) {
            usuario.getRol().clear();
        }

        // 2. Limpieza de relaciones (AQUÍ ESTABA EL ERROR)
        usuario.getReservas().clear();
        
        // 🔥 CAMBIO CLAVE: Cambiamos getEntrenamientos() por getRutinas()
        if (usuario.getRutinas() != null) {
            usuario.getRutinas().clear();
        }
        
        usuario.getSalud().clear();

        // 3. Sincronizar y borrar
        usuarioRepo.saveAndFlush(usuario);
        usuarioRepo.delete(usuario);
        
        log.info("Usuario '{}' (ID: {}) eliminado correctamente", usuario.getUsername(), id);
    }

    @Override
    public boolean validarTokenQR(String token, Long userId) {
        log.info("Validando token QR para usuario ID: {}", userId);
        Usuario usuario = findById(userId);
        if (usuario.getQrToken() == null || usuario.getTokenExpiraEn() == null) {
            return false;
        }
        return token.equals(usuario.getQrToken()) &&
               LocalDateTime.now().isBefore(usuario.getTokenExpiraEn());
    }

    @Override
    public void update(UsuarioRequestDto usuarioRequestDto, Long id) {
        log.info("Actualizando usuario con ID: {}", id);
        Usuario existente = this.findById(id);

        if (usuarioRequestDto.getPassword() != null && !usuarioRequestDto.getPassword().isEmpty()) {
            existente.setPassword(passwordEncoder.encode(usuarioRequestDto.getPassword()));
        }

        usuarioMapper.updateUsuarioFromDto(usuarioRequestDto, existente);
        usuarioRepo.save(existente);
    }
}