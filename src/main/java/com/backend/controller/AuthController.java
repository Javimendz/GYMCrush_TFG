//Paquete
package com.backend.controller;

import java.util.List;

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
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controlador REST para gestionar la autenticación de usuarios.
 * <p>
 * Este controlador proporciona endpoints para:
 * <ul>
 * <li>Inicio de sesión de usuarios existentes</li>
 * <li>Registro de nuevos usuarios</li>
 * </ul>
 * Maneja la generación de tokens JWT para la autenticación segura.
 * </p>
 *
 * @author Backend Team
 * @version 1.0
 * @since 2026
 */
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/auth")
@io.swagger.v3.oas.annotations.tags.Tag(name = "Auth", description = "Operaciones relacionadas con la autenticacion de usuarios")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtGenerator jwtGenerator;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final UsuarioMapper usuarioMapper;

    /**
     * Autentica un usuario y genera un token JWT.
     * <p>
     * Valida las credenciales del usuario y, si son correctas, genera un token JWT
     * que debe ser usado en las peticiones subsiguientes para autenticación.
     * </p>
     *
     * @param loginDto las credenciales de inicio de sesión (username y password)
     * @return ResponseEntity con el token JWT y datos del usuario autenticado
     */
    @PostMapping("/login")
    @io.swagger.v3.oas.annotations.Operation(summary = "Iniciar sesión", description = "Autentica al usuario y devuelve un token JWT")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Inicio de sesión exitoso")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Credenciales inválidas")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Acceso denegado. El usuario no tiene permisos para iniciar sesión")
    public ResponseEntity<ApiResponseDto<JwtAuthResponseDto>> login(@Valid @RequestBody LoginDto loginDto) {
        log.info("Intento de inicio de sesión para el usuario: {}", loginDto.getUsername());
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        log.info("Inicio de sesión exitoso para el usuario: {}", loginDto.getUsername());

        String jwt = jwtGenerator.generateToken(authentication);
        log.debug("Token JWT generado para el usuario: {}", loginDto.getUsername());

        Usuario usuario = usuarioRepository.findByUsername(loginDto.getUsername())
                .orElseThrow(() -> new RuntimeException("Error: Usuario no encontrado"));
        String nombreAMostrar = (usuario.getPerfil() != null) ? usuario.getPerfil().getNombre() : usuario.getUsername();
        log.debug("Usuario autenticado: {} - Perfil: {}", usuario.getUsername(), nombreAMostrar);

        List<String> roles = usuario.getRol().stream().map(r -> r.getName()).toList();

        JwtAuthResponseDto data = JwtAuthResponseDto.builder()
                .id(usuario.getId())
                .username(usuario.getUsername())
                .nombre(nombreAMostrar)
                .roles(roles)
                .accessToken(jwt)
                .tokenType("Bearer")
                .build();
        log.info("Login exitoso - Usuario: {} con roles: {}", usuario.getUsername(), roles);
        return ResponseEntity.ok(ApiResponseDto.<JwtAuthResponseDto>builder()
                .mensaje("¡Bienvenido de nuevo!")
                .datos(data)
                .success(true)
                .build());
    }

    /**
     * Registra un nuevo usuario en el sistema.
     * <p>
     * Crea una cuenta de usuario con el nombre de usuario y contraseña
     * proporcionados.
     * Si el nombre de usuario ya existe, retorna un error 400.
     * Crea automáticamente un perfil básico para el usuario.
     * </p>
     *
     * @param registerDto los datos de registro del nuevo usuario
     * @return ResponseEntity con los datos del usuario creado o mensaje de error
     */
 @PostMapping("/register")
@io.swagger.v3.oas.annotations.Operation(summary = "Registro de usuario", description = "Registra un nuevo usuario en el sistema")
@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Usuario registrado con éxito")
@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Datos de registro inválidos (username o correo ya existen)")
public ResponseEntity<ApiResponseDto<UsuarioResponseDto>> registerUser(@RequestBody RegisterDto registerDto) {
    log.info("Intento de registro de usuario: {} con correo: {}", registerDto.getUsername(), registerDto.getCorreo());

    // 1. VALIDACIÓN DE USERNAME (Ya la tenías)
    if (usuarioRepository.existsByUsername(registerDto.getUsername())) {
        log.warn("Registro fallido - Username ya existe: {}", registerDto.getUsername());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponseDto.error("El nombre de usuario '" + registerDto.getUsername() + "' ya está en uso"));
    }

    // 2. CORRECCIÓN: VALIDACIÓN DE CORREO (Añadido)
    // Esto evita que la base de datos lance el error de "unique constraint"
    if (usuarioRepository.existsByCorreo(registerDto.getCorreo())) {
        log.warn("Registro fallido - Correo ya existe: {}", registerDto.getCorreo());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponseDto.error("El correo electrónico '" + registerDto.getCorreo() + "' ya está registrado"));
    }

    try {
        // Mapeo y cifrado de contraseña
        Usuario user = usuarioMapper.registerDtoToUser(registerDto);
        user.setPassword(passwordEncoder.encode(registerDto.getPassword()));

        // Gestión del Perfil
        if (user.getPerfil() != null) {
            user.getPerfil().setUsuario(user);
        } else {
            Perfil nuevoPerfil = new Perfil();
            nuevoPerfil.setNombre(user.getUsername());
            nuevoPerfil.setUsuario(user);
            user.setPerfil(nuevoPerfil);
        }

        // Guardado
        Usuario savedUser = usuarioRepository.save(user);
        log.info("Usuario registrado exitosamente: {} con ID: {}", savedUser.getUsername(), savedUser.getId());

        // Respuesta de éxito
        UsuarioResponseDto response = usuarioMapper.toUsuarioResponseDto(savedUser);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDto.success("¡Bienvenido! Usuario registrado con éxito", response));

    } catch (Exception e) {
        log.error("Error inesperado durante el registro: ", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseDto.error("Error interno al procesar el registro: " + e.getMessage()));
    }
}
}
