package com.backend.security.service;

import java.util.Collection;
import java.util.stream.Collectors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // 👈 Importante
import com.backend.repository.UsuarioRepository;
import com.backend.domain.Role;
import com.backend.domain.Usuario;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserDetailServiceImp implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    @Override
    @Transactional(readOnly = true) // Evita errores de carga de roles, Lazy Loading
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con el nombre: " + username));

        // Usamos el método privado, asi no repito codigo
        return new User(
                usuario.getUsername(), 
                usuario.getPassword(), 
                mapRolesToAuthorities(usuario.getRol())
        );
    }

    // Método para mapear los roles 
    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(java.util.Set<Role> roles) {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName())) // Ej: "ROLE_USER"
                .collect(Collectors.toSet());
    }
}