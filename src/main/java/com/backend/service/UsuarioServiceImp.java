package com.backend.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.backend.domain.Usuario;
import com.backend.dto.UsuarioRequestDto;
import com.backend.exceptions.ResourceNotFoundException;
import com.backend.mapper.UsuarioMapper;
import com.backend.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional // Anotacion para indicar que los metodos de esta clase son transaccionales, es
               // decir realizan mas de una operacion
@Service
public class UsuarioServiceImp implements IUsuarioService {

    // Eliminamos el uso de Autowired al usar lombok
    private final UsuarioRepository usuarioRepo;
    private final UsuarioMapper usuarioMapper;

    @Override
    public Usuario save(Usuario usuario) {

        return usuarioRepo.save(usuario);
    }

    @Override
    public Boolean existsByCorreo(String correo) {
        return usuarioRepo.existsByCorreo(correo);
    }

    /*
     * 
     * @Override
     * public Usuario updateUsuario(Long id, Usuario usuarioActualizado) {
     * return usuarioRepo.findById(id).map(existente -> {
     * 
     * existente.setNombre(usuarioActualizado.getNombre());
     * existente.setApellidos(usuarioActualizado.getApellidos());
     * existente.setTelefono(usuarioActualizado.getTelefono());
     * existente.setDireccion(usuarioActualizado.getDireccion());
     * existente.setCiudad(usuarioActualizado.getCiudad());
     * existente.setPais(usuarioActualizado.getPais());
     * existente.setCodigoPostal(usuarioActualizado.getCodigoPostal());
     * 
     * return usuarioRepo.save(existente);
     * }).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
     * "Usuario no encontrado"));
     * }
     */
    // Logica para el QR de acceso
    @Override
    public Usuario generarTokenAcceso(Long id) {
        Usuario usuario = usuarioRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

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

        Usuario usuarioToDelete = this.findById(id);
        usuarioRepo.delete(usuarioToDelete);

    }

    @Override
    public void update(UsuarioRequestDto usuarioRequestDto, Long id) {

        Usuario existente = this.findById(id);

        // Mapeamos los cambios del DTO al objeto
        usuarioMapper.updateUsuarioFromDto(usuarioRequestDto, existente);

        // Guardamos los cambios
        usuarioRepo.save(existente);
    }
}
