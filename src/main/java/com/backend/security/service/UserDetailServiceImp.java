//Paquete
package com.backend.security.service;

//Imports
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.backend.repository.UsuarioRepository;
import com.backend.domain.Role;
import com.backend.domain.Usuario;
import lombok.RequiredArgsConstructor;

//Servicio para la entidad UserDetail
@Service
@RequiredArgsConstructor
public class UserDetailServiceImp implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    // Metodo de la interface UserDetails
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        
        List<SimpleGrantedAuthority> authorities = usuario.getRol().stream()
                .map(rol -> new SimpleGrantedAuthority(rol.getName())) 
                .collect(Collectors.toList());

        return new User(usuario.getUsername(), usuario.getPassword(), authorities);
    }

    // Metodo privado para mapear los roles
    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Set<Role> rol) {

        return rol.stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList());

    }

}