package com.backend.controller;

import com.backend.domain.Perfil;
import com.backend.domain.Role;
import com.backend.domain.Usuario;
import com.backend.dto.UsuarioRequestDto;
import com.backend.dto.UsuarioResponseDto;
import com.backend.mapper.UsuarioMapper;
import com.backend.repository.PerfilRepository;
import com.backend.repository.RoleRepository;
import com.backend.service.IUsuarioService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController // Controlador REST para manejar solicitudes HTTP relacionadas con usuarios
@RequestMapping("/api/v1/usuario") // Mapea todas las solicitudes HTTP a /api/v1/usuario
@RequiredArgsConstructor
@io.swagger.v3.oas.annotations.tags.Tag(name = "Usuario", description = "Operaciones relacionadas con los usuarios")
public class UsuarioController {

    private final IUsuarioService usuarioService;
    private final UsuarioMapper usuarioMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final PerfilRepository perfilRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USUARIO')")
    @io.swagger.v3.oas.annotations.Operation(summary = "Listar todos los usuarios", description = "Permite a un administrador o usuario listar todos los usuarios registrados")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Listado de usuarios recuperado con éxito")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")  
    public ResponseEntity<List<UsuarioResponseDto>> getAllUsuarios() {

        List<Usuario> usuarios = usuarioService.getAllUsuarios();
        List<UsuarioResponseDto> responseDtoList = usuarioMapper.toUsuarioResponseDtoList(usuarios);
        return ResponseEntity.ok(responseDtoList);
    }

    @PostMapping("/crear")
    @PreAuthorize("hasRole('ADMIN')")
    @io.swagger.v3.oas.annotations.Operation(summary = "Crear un nuevo usuario", description = "Permite al administrador crear un nuevo usuario")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Usuario creado con éxito")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Solicitud inválida")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")  
    @Transactional
    public ResponseEntity<UsuarioResponseDto> createUsuario(@Valid @RequestBody UsuarioRequestDto usuarioDto) {

        Usuario usuarioToSave = usuarioMapper.toEntity(usuarioDto);

        // Roles
        Set<Role> rolesReales = new HashSet<>();
        if (usuarioDto.getRol() != null && !usuarioDto.getRol().isEmpty()) {
            for (String nombreRol : usuarioDto.getRol()) {
                Role roleBd = roleRepository.findByName(nombreRol)
                        .orElseThrow(() -> new RuntimeException("Error: El rol " + nombreRol + " no existe en la DB"));
                rolesReales.add(roleBd);
            }
        } else {
            Role defaultRole = roleRepository.findByName("ROLE_USUARIO")
                    .orElseThrow(() -> new RuntimeException("Error: Rol ROLE_USUARIO no encontrado en la DB"));
            rolesReales.add(defaultRole);
        }
        usuarioToSave.setRol(rolesReales);
        usuarioToSave.setPassword(passwordEncoder.encode(usuarioDto.getPassword()));

        Perfil perfil = usuarioToSave.getPerfil();
        usuarioToSave.setPerfil(null);

        Usuario savedUsuario = usuarioService.save(usuarioToSave);
        entityManager.flush();
        entityManager.clear();

        savedUsuario = usuarioService.findById(savedUsuario.getId());

        if (perfil != null) {
            perfil.setUsuario(savedUsuario);
            Perfil savedPerfil = perfilRepository.save(perfil);
            savedUsuario.setPerfil(savedPerfil);
        }

        UsuarioResponseDto responseDto = usuarioMapper.toUsuarioResponseDto(savedUsuario);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USUARIO')")
    @io.swagger.v3.oas.annotations.Operation(summary = "Obtener usuario por ID", description = "Permite a un administrador o usuario obtener un usuario por su ID")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Usuario recuperado con éxito")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")  
    public ResponseEntity<UsuarioResponseDto> getUsuarioById(@PathVariable Long id) {
        Usuario usuario = usuarioService.findById(id);
        UsuarioResponseDto responseDto = usuarioMapper.toUsuarioResponseDto(usuario);
        return ResponseEntity.ok(responseDto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USUARIO')")
    @io.swagger.v3.oas.annotations.Operation(summary = "Actualizar usuario por ID", description = "Permite a un administrador o usuario actualizar un usuario por su ID")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Usuario actualizado con éxito")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Solicitud inválida")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")  
    public ResponseEntity<UsuarioResponseDto> updateUsuario(@PathVariable Long id,
            @Valid @RequestBody UsuarioRequestDto usuarioDto) {
        Usuario updatedUsuario = usuarioService.findById(id);
        usuarioMapper.updateUsuarioFromDto(usuarioDto, updatedUsuario);

        if (usuarioDto.getRol() != null && !usuarioDto.getRol().isEmpty()) {
            Set<Role> rolesReales = new HashSet<>();
            for (String nombreRol : usuarioDto.getRol()) {
                Role roleBd = roleRepository.findByName(nombreRol)
                        .orElseThrow(() -> new RuntimeException("Error: El rol " + nombreRol + " no existe"));
                rolesReales.add(roleBd);
            }
            // Sustituimos los roles del mapper por los reales con ID
            updatedUsuario.setRol(rolesReales);
        }

        if (usuarioDto.getPassword() != null && !usuarioDto.getPassword().isEmpty()) {

            updatedUsuario.setPassword(passwordEncoder.encode(usuarioDto.getPassword()));
        }
        Usuario updateUsuario = usuarioService.save(updatedUsuario);
        return ResponseEntity.ok(usuarioMapper.toUsuarioResponseDto(updateUsuario));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @io.swagger.v3.oas.annotations.Operation(summary = "Eliminar usuario por ID", description = "Permite al administrador eliminar un usuario por su ID")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Usuario eliminado con éxito")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")  
    public ResponseEntity<Void> deleteUsuario(@PathVariable Long id) {

        usuarioService.deleteById(id);
        return ResponseEntity.noContent().build(); // El estandar pide devolver un 204 noContent para informar al
                                                   // cliente
    }
}
