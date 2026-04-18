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
import com.backend.domain.enums.EnumRol;
import com.backend.dto.UsuarioResponseDto;
import com.backend.repository.RoleRepository;
import com.backend.domain.Role;

@RequiredArgsConstructor
@Slf4j
@Transactional
@Service
public class UsuarioServiceImp implements IUsuarioService {

    private final UsuarioRepository usuarioRepository; 
    private final UsuarioMapper usuarioMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    // En UsuarioServiceImp.java lo implementas:
    @Override
    @Transactional(readOnly = true)
    public List<Usuario> listarEntrenadores() {
        log.info("Recuperando lista de entrenadores para asignación de horarios");
        return usuarioRepository.findEntrenadores();
    }

    @Override
    @Transactional
    public UsuarioResponseDto crearUsuarioConRol(UsuarioRequestDto dto, EnumRol nombreRol) {
        log.info("Iniciando creación de usuario con rol: {}", nombreRol);

        Usuario usuario = usuarioMapper.toEntity(dto);
        usuario.setPassword(passwordEncoder.encode(dto.getPassword()));

        // 2. Búsqueda del Rol
        Role role = roleRepository.findByName(nombreRol.name())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Error: El rol " + nombreRol + " no existe en la base de datos"));

        // 3. Asignación del Rol
        usuario.getRoles().add(role);

        // 4. Guardado
        Usuario guardado = usuarioRepository.save(usuario);

        log.info("Usuario {} creado exitosamente con ID: {}", guardado.getUsername(), guardado.getId());

        // 5. Retorno mapeado
        return usuarioMapper.toUsuarioResponseDto(guardado);
    }

    @Override
    public Usuario save(Usuario usuario) {
        log.info("Guardando usuario: {}", usuario.getUsername());
        return usuarioRepository.save(usuario);
    }

    @Override
    public Usuario generarTokenAcceso(Long id) {
        log.info("Generando token de acceso para usuario ID: {}", id);
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));

        usuario.setQrToken(java.util.UUID.randomUUID().toString());
        usuario.setTokenExpiraEn(LocalDateTime.now().plusSeconds(30));

        return usuarioRepository.save(usuario);
    }

    @Override
    public List<Usuario> getAllUsuarios() {
        log.info("Obteniendo todos los usuarios");
        return usuarioRepository.findAll();
    }

   @Override
@Transactional
public void asignarRolEntrenador(Long id) {
    // 1. Asegúrate de usar 'usuarioRepository' o 'usuarioRepo' según lo hayas declarado arriba
    Usuario usuario = usuarioRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
    
    // 2. Aquí usa el nombre EXACTO de tu Enum (probablemente ENTRENADOR)
   Role rolEntrenador = roleRepository.findByName(EnumRol.ENTRENADOR.name()) 
                .orElseThrow(() -> new RuntimeException("Error: El rol ENTRENADOR no existe en la DB"));

    // 3. getRoles() ahora funcionará si pusiste @Data en Usuario.java
    usuario.getRoles().add(rolEntrenador);
    
    usuarioRepository.save(usuario);
    log.info("Rol de entrenador asignado al usuario: {}", usuario.getUsername());
}

    @Override
    public Usuario findById(Long id) {
        log.info("Buscando usuario por ID: {}", id);
       return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        log.info("Borrando usuario ID: {}", id);
        Usuario usuario = findById(id);

        // Limpieza de relaciones para evitar errores de integridad
        if (usuario.getRoles() != null) {
            usuario.getRoles().clear();
        }
        if (usuario.getReservas() != null) {
            usuario.getReservas().clear();
        }
        if (usuario.getRutinas() != null) {
            usuario.getRutinas().clear();
        }

        usuarioRepository.saveAndFlush(usuario);
        usuarioRepository.delete(usuario);
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
        usuarioRepository.save(existente);
    }

}