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

import com.backend.domain.Usuario;
import com.backend.dto.UsuarioResponseDto;
import com.backend.mapper.UsuarioMapper;
import com.backend.repository.RoleRepository;
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
    private final RoleRepository roleRepository; // Repositorio de roles
    private final PasswordEncoder passwordEncoder; // Codificador de contraseñas
    private final UsuarioMapper usuarioMapper;

    @PostMapping("/login") // Peticion para autenticar a un usuario
    public ResponseEntity<JwtAuthResponseDto> authenticateUser(@RequestBody LoginDto loginDto) {
        // autenticar con spring security
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword()));
        // Almacen temporal para guardar token
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // generar token
        String jwt = jwtGenerator.generateToken(authentication);

        // Extraer datos para el dto
        String username = authentication.getName();
        // Buscamos el usuario en la bd
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Error crítico: Usuario autenticado no encontrado en DB"));
        // Convertimos el set<Role> a set<String> con los nombres de los roles
        List<String> rolesNames = usuario.getRol().stream()
                .map(role -> role.getName())
                .toList();

        // Construyo respuesta para el builder
        JwtAuthResponseDto response = JwtAuthResponseDto.builder()
                .id(usuario.getId())
                .username(usuario.getUsername())
                .nombre(usuario.getPerfil().getNombre())
                .roles(rolesNames)
                .accessToken(jwt)
                .tokenType("Bearer")
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterDto registerDto) {

        if (usuarioRepository.existsByUsername(registerDto.getUsername())) {

            return new ResponseEntity<>("Nombre de usuario, ya existe...", HttpStatus.BAD_REQUEST);
        }

        if (usuarioRepository.existsByCorreo(registerDto.getCorreo())) {

            return new ResponseEntity<>("Email de usuario, ya existe...", HttpStatus.BAD_REQUEST);
        }
        Usuario user = usuarioMapper.registerDtoToUser(registerDto);
        user.setPassword(passwordEncoder.encode(registerDto.getPassword()));

        if (user.getPerfil() != null) {
            user.getPerfil().setUsuario(user);
        }

        Usuario savedUser = usuarioRepository.save(user);
        UsuarioResponseDto response = usuarioMapper.toUsuarioResponseDto(savedUser);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
