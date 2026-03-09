package com.backend.service;

import java.util.List;

import com.backend.domain.Usuario;

public interface IUsuarioService {
 

    Usuario createUsuario(Usuario usuario);

    Usuario updateUsuario(Long id, Usuario usuario);

    void deleteUsuario(Long id);

    Usuario getUsuarioById(Long id);

    List<Usuario> getAllUsuarios();

    Usuario save(Usuario usuario); // Guarda un Usuario en la base de datos
}
