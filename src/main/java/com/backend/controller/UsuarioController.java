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
    import org.springframework.web.bind.annotation.*;
    import org.springframework.web.bind.annotation.GetMapping;
    import org.springframework.web.bind.annotation.PathVariable;

    @RestController() // Controlador REST para manejar solicitudes HTTP relacionadas con usuarios
    @RequestMapping("/api/v1/usuario") // Mapea todas las solicitudes HTTP a /api/v1/usuario
    @RequiredArgsConstructor
    public class UsuarioController {

        private final IUsuarioService usuarioService;
        private final UsuarioMapper usuarioMapper;

        @GetMapping
        public List<UsuarioResponseDto> getAllUsuarios() {

            List<Usuario> usuarios = usuarioService.getAllUsuarios();
            return usuarioMapper.toUsuarioResponseDtoList(usuarios);
        }

        @PostMapping("/crear")
        public ResponseEntity<UsuarioResponseDto> createUsuario(@Valid @RequestBody UsuarioRequestDto usuarioDto) {

            Usuario usuarioToSave = usuarioMapper.toEntity(usuarioDto);
            Usuario savedUsuario = usuarioService.save(usuarioToSave);
            UsuarioResponseDto responseDto = usuarioMapper.toUsuarioResponseDto(savedUsuario);
            return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
        }

        @GetMapping("/{id}")
        public ResponseEntity<UsuarioResponseDto> getUsuarioById(@PathVariable Long id) {
            Usuario usuario = usuarioService.findById(id);
            UsuarioResponseDto responseDto = usuarioMapper.toUsuarioResponseDto(usuario);
            return ResponseEntity.ok(responseDto);
        }

        @PutMapping("/{id}")
        public ResponseEntity<UsuarioResponseDto> updateUsuario(@PathVariable Long id,
                @Valid @RequestBody UsuarioRequestDto usuarioDto) {
            Usuario updatedUsuario = usuarioService.findById(id);
            usuarioMapper.updateUsuarioFromDto(usuarioDto, updatedUsuario);
            Usuario updateUsuario = usuarioService.save(updatedUsuario);
            return ResponseEntity.ok(usuarioMapper.toUsuarioResponseDto(updateUsuario));
        }

        @DeleteMapping("/{id}")
        public ResponseEntity<Void> deleteUsuario(@PathVariable Long id) {

            usuarioService.deleteById(id);
            return ResponseEntity.noContent().build(); // El estandar pide devolver un 204 noContent para informar al
                                                    // cliente
        }
    }
