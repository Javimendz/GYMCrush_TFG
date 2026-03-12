//Paquete
package com.backend.controller;
import java.util.Collections;

//Imports
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RestController;

import com.backend.domain.Role;
import com.backend.domain.Usuario;
import com.backend.dto.JwtAuthResponseDto;
import com.backend.dto.LoginDto;
import com.backend.dto.RegisterDto;
import com.backend.mapper.UsuarioMapper;
import com.backend.repository.RoleRepository;
import com.backend.repository.UsuarioRepository;
import com.backend.security.jwt.JwtGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
//Clase para manejar la autenticacion de usuarios
@RestController //Controlador para manejar las peticiones de autenticacion
@RequiredArgsConstructor //Inyeccion de dependencias
@RequestMapping("/api/v1/auth") //Ruta base para las peticiones de autenticacion
public class AuthController {
    
    private final AuthenticationManager authenticationManager; //Administrador de autenticacion
    private final JwtGenerator jwtGenerator; //Generador de tokens JWT
    private final UsuarioRepository usuarioRepository; //Repositorio de usuarios
    private final RoleRepository roleRepository; //Repositorio de roles
    private final PasswordEncoder passwordEncoder; //Codificador de contraseñas
    private final UsuarioMapper usuarioMapper;
    @PostMapping("/login")//Peticion para autenticar a un usuario
    public ResponseEntity<JwtAuthResponseDto> authenticateUser(@RequestBody LoginDto loginDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword()));
        //Almacen temporal para guardar token
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtGenerator.generateToken(authentication);
                return new ResponseEntity<>(new JwtAuthResponseDto(jwt), HttpStatus.OK);  
    }
    
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody RegisterDto registerDto){

        if(usuarioRepository.existsByUsername(registerDto.getUsername())){

            return new ResponseEntity<>("Nombre de usuario, ya existe...", HttpStatus.BAD_REQUEST);
        }

         if(usuarioRepository.existsByCorreo(registerDto.getCorreo())){

            return new ResponseEntity<>("Email de usuario, ya existe...", HttpStatus.BAD_REQUEST);
        }
        Usuario user = usuarioMapper.registerDtoToUser(registerDto);
        user.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        usuarioRepository.save(user);
        return new ResponseEntity<>("Usuario registrado con éxito...", HttpStatus.OK);  
    }
 }
    

