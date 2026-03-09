package com.backend.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.backend.domain.Usuario;
import com.backend.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UsuarioServiceImp implements IUsuarioService {

    //Eliminamos el uso de Autowired al usar lombok
    private final UsuarioRepository usuarioRepo;

    
    @Override
    public Usuario createUsuario(Usuario usuario) {
        return usuarioRepo.save(usuario);
    }


    // Actualizar un usuario existente
    //Uso el map como en TypeScript para actualizar el usuario junto a la Api Optional
    @Override
    public Usuario updateUsuario(Long id, Usuario usuario) {
        return usuarioRepo.findById(id).map(existente -> {
            usuario.setId(id); // Le asignamos el ID al nuevo objeto
            return usuarioRepo.save(usuario); // Sobrescribimos el registro
        }).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado con id: " + id));
    }

    @Override
    public void deleteUsuario(Long id) {
        usuarioRepo.deleteById(id);
    }

    @Override
    public Usuario getUsuarioById(Long id) {
        return usuarioRepo.findById(id).orElse(null);
    }

    @Override
    public List<Usuario> getAllUsuarios() {
        return usuarioRepo.findAll();
    }


    @Override
    public Usuario save(Usuario usuario) {

        return usuarioRepo.save(usuario);
    }

    
}
