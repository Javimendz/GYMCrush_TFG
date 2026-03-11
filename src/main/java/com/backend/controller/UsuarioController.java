    package com.backend.controller;

    import com.backend.domain.Usuario;
    import com.backend.dto.UsuarioRequestDto;
    import com.backend.dto.UsuarioResponseDto;
    import com.backend.mapper.UsuarioMapper;
    import com.backend.service.IUsuarioService;

    import jakarta.validation.Valid;
    import lombok.RequiredArgsConstructor;

    import java.util.List;

    import org.springframework.http.HttpStatus;
    import org.springframework.http.ResponseEntity;
    import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

    @RestController() // Controlador REST para manejar solicitudes HTTP relacionadas con usuarios
    @RequestMapping("/api/v1/usuario") // Mapea todas las solicitudes HTTP a /api/v1/usuario
    @RequiredArgsConstructor
    public class UsuarioController {

        private final IUsuarioService usuarioService;
        private final UsuarioMapper usuarioMapper;
        private final PasswordEncoder passwordEncoder;

        @GetMapping
        @PreAuthorize("hasAnyRole('ADMIN', 'USUARIO')")
        public ResponseEntity<List<UsuarioResponseDto>> getAllUsuarios() {

            List<Usuario> usuarios = usuarioService.getAllUsuarios();
            List<UsuarioResponseDto> responseDtoList = usuarioMapper.toUsuarioResponseDtoList(usuarios);
            return ResponseEntity.ok(responseDtoList);
        }

        @PostMapping
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<UsuarioResponseDto> createUsuario(@Valid @RequestBody UsuarioRequestDto usuarioDto) {

            Usuario usuarioToSave = usuarioMapper.toEntity(usuarioDto);
            usuarioToSave.setPassword(passwordEncoder.encode(usuarioDto.getPassword()));
            Usuario savedUsuario = usuarioService.save(usuarioToSave);
            UsuarioResponseDto responseDto = usuarioMapper.toUsuarioResponseDto(savedUsuario);
            return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
        }

        @GetMapping("/{id}")
        @PreAuthorize("hasAnyRole('ADMIN', 'USUARIO')")
        public ResponseEntity<UsuarioResponseDto> getUsuarioById(@PathVariable Long id) {
            Usuario usuario = usuarioService.findById(id);
            UsuarioResponseDto responseDto = usuarioMapper.toUsuarioResponseDto(usuario);
            return ResponseEntity.ok(responseDto);
        }

        @PutMapping("/{id}")
        @PreAuthorize("hasAnyRole('ADMIN', 'USUARIO')")
        public ResponseEntity<UsuarioResponseDto> updateUsuario(@PathVariable Long id,
                @Valid @RequestBody UsuarioRequestDto usuarioDto) {
            Usuario updatedUsuario = usuarioService.findById(id);
            usuarioMapper.updateUsuarioFromDto(usuarioDto, updatedUsuario);

            if (usuarioDto.getPassword() != null && !usuarioDto.getPassword().isEmpty()) {
                
                updatedUsuario.setPassword(passwordEncoder.encode(usuarioDto.getPassword()));
            }
            Usuario updateUsuario = usuarioService.save(updatedUsuario);
            return ResponseEntity.ok(usuarioMapper.toUsuarioResponseDto(updateUsuario));
        }

        @DeleteMapping("/{id}")
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<Void> deleteUsuario(@PathVariable Long id) {

            usuarioService.deleteById(id);
            return ResponseEntity.noContent().build(); // El estandar pide devolver un 204 noContent para informar al
                                                    // cliente
        }
    }
