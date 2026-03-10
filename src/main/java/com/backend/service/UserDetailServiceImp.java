package com.backend.service;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collector;
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

@Service
@RequiredArgsConstructor
public class UserDetailServiceImp implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    //Metodo de la interface UserDetails
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        
        
        Usuario user = usuarioRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con username: " + username));

        if (user == null) {
            throw new UsernameNotFoundException("Usuario no encontrado con el nombre: " + username);
        }

        return new org.springframework.security.core.userdetails.User( user.getUsername(), user.getPassword(), mapRolesToAuthorities(user.getRoles()));
    }

    //Metodo privado para mapear los roles
    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Set<Role> roles){

        return  roles.stream().map(role -> 
            new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList());
        

    }

}