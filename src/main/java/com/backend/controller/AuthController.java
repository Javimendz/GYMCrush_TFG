//Paquete
package com.backend.controller;

import java.util.List;

//Imports
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RestController;

import com.backend.domain.Perfil;
import com.backend.domain.Role;
import com.backend.domain.Usuario;
import com.backend.dto.UsuarioResponseDto;
import com.backend.mapper.UsuarioMapper;
import com.backend.repository.PerfilRepository;
import com.backend.repository.UsuarioRepository;
import com.backend.security.dto.JwtAuthResponseDto;
import com.backend.security.dto.LoginDto;
import com.backend.security.dto.RegisterDto;
import com.backend.security.jwt.JwtGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

//Clase para manejar la autenticacion de usuarios
@RestController // Controlador para manejar las peticiones de autenticacion
@RequiredArgsConstructor // Inyeccion de dependencias
@RequestMapping("/api/v1/auth") // Ruta base para las peticiones de autenticacion
public class AuthController {

    private final AuthenticationManager authenticationManager; // Administrador de autenticacion
    private final JwtGenerator jwtGenerator; // Generador de tokens JWT
    private final UsuarioRepository usuarioRepository; // Repositorio de usuarios
    private final PasswordEncoder passwordEncoder; // Codificador de contraseñas
    private final UsuarioMapper usuarioMapper;
    private final PerfilRepository perfilRepository;

    @PostMapping("/login")
    public ResponseEntity<JwtAuthResponseDto> authenticateUser(@RequestBody LoginDto loginDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtGenerator.generateToken(authentication);

        Usuario usuario = usuarioRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Error: Usuario no encontrado"));

        String nombreAMostrar = (usuario.getPerfil() != null) ? usuario.getPerfil().getNombre() : "Usuario sin Perfil";

        List<String> rolesNames = usuario.getRol().stream()
                .map(Role::getName)
                .toList();

        JwtAuthResponseDto response = JwtAuthResponseDto.builder()
                .id(usuario.getId())
                .username(usuario.getUsername())
                .nombre(nombreAMostrar) 
                .roles(rolesNames)
                .accessToken(jwt)
                .tokenType("Bearer")
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
public ResponseEntity<?> registerUser(@RequestBody RegisterDto registerDto) {
    if (usuarioRepository.existsByUsername(registerDto.getUsername())) {
        return new ResponseEntity<>("Nombre de usuario ya existe", HttpStatus.BAD_REQUEST);
    }

    // Mapeamos el DTO a la Entidad
    Usuario user = usuarioMapper.registerDtoToUser(registerDto);
    user.setPassword(passwordEncoder.encode(registerDto.getPassword()));

    //  Vinculamos el perfil antes de guardar
    if (user.getPerfil() != null) {
        user.getPerfil().setUsuario(user);
    } else {
        
        Perfil nuevoPerfil = new Perfil();
        nuevoPerfil.setNombre(user.getUsername());
        nuevoPerfil.setUsuario(user);
        user.setPerfil(nuevoPerfil);
    }

    // Guardamos solo una vez. El Cascade guarda el perfil automáticamente.
    Usuario savedUser = usuarioRepository.save(user);

    UsuarioResponseDto response = usuarioMapper.toUsuarioResponseDto(savedUser);
    return new ResponseEntity<>(response, HttpStatus.CREATED);
}
}
