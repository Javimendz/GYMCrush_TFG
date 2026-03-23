//Paquete
package com.backend.service;

import java.time.LocalDateTime;
//Imports
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

//Servicio para la entidad Usuario
@RequiredArgsConstructor
@Transactional // Anotacion para indicar que los metodos de esta clase son transaccionales, es
               // decir realizan mas de una operacion
@Service
public class UsuarioServiceImp implements IUsuarioService {

    // Eliminamos el uso de Autowired al usar lombok
    private final UsuarioRepository usuarioRepo;
    private final UsuarioMapper usuarioMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Usuario save(Usuario usuario) {

        return usuarioRepo.save(usuario);
    }

    // Logica para el QR de acceso
    @Override
    public Usuario generarTokenAcceso(Long id) {
        Usuario usuario = usuarioRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));
        usuario.setQrToken(java.util.UUID.randomUUID().toString());
        usuario.setTokenExpiraEn(java.time.LocalDateTime.now().plusSeconds(30));

        return usuarioRepo.save(usuario);
    }

    @Override
    public List<Usuario> getAllUsuarios() {
        return usuarioRepo.findAll();
    }

    @Override
    public Usuario findById(Long id) {
        return usuarioRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));
    }

    @Override
    public void deleteById(Long id) {
        Usuario usuario = this.findById(id);
        usuarioRepo.delete(usuario);
    }

    public boolean validarTokenQR(String token, Long userId) {
        Usuario usuario = findById(userId);
        if (usuario.getQrToken() == null || usuario.getTokenExpiraEn() == null) {
            return false; // No hay token generado
        }
        return token.equals(usuario.getQrToken()) &&
                LocalDateTime.now().isBefore(usuario.getTokenExpiraEn());
    }

    @Override
    public void update(UsuarioRequestDto usuarioRequestDto, Long id) {

        Usuario existente = this.findById(id);

        if (usuarioRequestDto.getPassword() != null && !usuarioRequestDto.getPassword().isEmpty()) {
            existente.setPassword(passwordEncoder.encode(usuarioRequestDto.getPassword()));
        } // Mapeamos los cambios del DTO al objeto

        usuarioMapper.updateUsuarioFromDto(usuarioRequestDto, existente);
        // Guardamos los cambios
        usuarioRepo.save(existente);
    }
}
