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
import com.backend.domain.Usuario;
import com.backend.dto.UsuarioResponseDto;
import com.backend.mapper.UsuarioMapper;
import com.backend.repository.UsuarioRepository;
import com.backend.security.dto.ApiResponseDto;
import com.backend.security.dto.JwtAuthResponseDto;
import com.backend.security.dto.LoginDto;
import com.backend.security.dto.RegisterDto;
import com.backend.security.jwt.JwtGenerator;

import jakarta.validation.Valid;
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

   @PostMapping("/login")
    public ResponseEntity<ApiResponseDto<JwtAuthResponseDto>> login(@Valid @RequestBody LoginDto loginDto) {
        // 1. Autenticar credenciales
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        // 2. Generar JWT
        String jwt = jwtGenerator.generateToken(authentication);

        // 3. Obtener datos extra del usuario para la App
        Usuario usuario = usuarioRepository.findByUsername(loginDto.getUsername())
                .orElseThrow(() -> new RuntimeException("Error: Usuario no encontrado"));

        String nombreAMostrar = (usuario.getPerfil() != null) ? usuario.getPerfil().getNombre() : usuario.getUsername();
        List<String> roles = usuario.getRol().stream().map(r -> r.getName()).toList();

        JwtAuthResponseDto data = JwtAuthResponseDto.builder()
                .id(usuario.getId())
                .username(usuario.getUsername())
                .nombre(nombreAMostrar)
                .roles(roles)
                .accessToken(jwt)
                .tokenType("Bearer")
                .build();

        return ResponseEntity.ok(ApiResponseDto.<JwtAuthResponseDto>builder()
                .mensaje("¡Bienvenido de nuevo!")
                .datos(data)
                .success(true)
                .build());
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
    

