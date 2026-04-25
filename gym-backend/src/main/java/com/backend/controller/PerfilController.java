package com.backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.domain.Perfil;
import com.backend.dto.PerfilRequestDto;
import com.backend.dto.PerfilResponseDto;
import com.backend.exceptions.ResourceNotFoundException;
import com.backend.mapper.PerfilMapper;
import com.backend.repository.PerfilRepository;
import com.backend.repository.SaludRepository;
import com.backend.security.dto.ApiResponseDto;
import com.backend.service.IPerfilService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Controlador REST para gestionar los perfiles de usuarios.
 * <p>
 * Este controlador proporciona endpoints para:
 * <ul>
 * <li>Listar todos los perfiles (ADMIN)</li>
 * <li>Obtener perfil por ID</li>
 * <li>Actualizar perfil de usuario</li>
 * </ul>
 * Los perfiles incluyen información personal y datos de salud asociados.
 * </p>
 *
 * @author Backend Team
 * @version 1.0
 * @since 2026
 */
@RestController
@RequestMapping("/api/v1/perfil")
@RequiredArgsConstructor
@Slf4j
@io.swagger.v3.oas.annotations.tags.Tag(name = "Perfil", description = "Operaciones relacionadas con los perfiles de usuarios")
public class PerfilController {

    private final IPerfilService perfilService;
    private final PerfilMapper perfilMapper;
    private final PerfilRepository perfilRepository;
    private final SaludRepository saludRepository;

    /**
     * Lista todos los perfiles de usuarios con datos de salud enriquecidos.
     * <p>
     * Solo los usuarios con rol ADMIN pueden listar todos los perfiles.
     * Los perfiles se enriquecen con datos de salud del usuario si están
     * disponibles.
     * </p>
     *
     * @return lista de perfiles de usuarios
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @io.swagger.v3.oas.annotations.Operation(summary = "Listar perfiles", description = "Recupera una lista de todos los perfiles de usuarios")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lista de perfiles recuperada con éxito")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<ApiResponseDto<List<PerfilResponseDto>>> findAll() {
        log.info("Listando todos los perfiles");
        List<Perfil> perfiles = perfilRepository.findAll();
        log.debug("Total de perfiles encontrados: {}", perfiles.size());

        List<PerfilResponseDto> listaEnriquecida = perfiles.stream().map(perfil -> {
            PerfilResponseDto dto = perfilMapper.toDto(perfil);
            saludRepository.findFirstByUsuarioOrderByFechaMedicionDesc(perfil.getUsuario())
                    .ifPresent(salud -> {
                        dto.setPeso(salud.getPeso());
                        dto.setEstatura(salud.getEstatura());
                        dto.setImc(salud.getImc());
                        dto.setNivelActividad(salud.getNivelActividad());
                    });
            return dto;
        }).toList();

        return ResponseEntity.ok(ApiResponseDto.<List<PerfilResponseDto>>builder()
                .mensaje("Lista de perfiles recuperada con éxito")
                .datos(listaEnriquecida)
                .success(true)
                .build());
    }

    @GetMapping("/usuario/{usuarioId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USUARIO')")
    @io.swagger.v3.oas.annotations.Operation(summary = "Recuperar perfil por ID de Usuario")
    public ResponseEntity<ApiResponseDto<PerfilResponseDto>> getPerfilByUsuarioId(
            @PathVariable("usuarioId") Long usuarioId) {

        log.info("Buscando perfil para el usuario ID: {}", usuarioId);

        // Buscamos el perfil asociado a ese usuario
        Perfil perfil = perfilRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Perfil no encontrado para este usuario"));

        PerfilResponseDto dto = perfilMapper.toDto(perfil);

        // Enriquecemos con salud (copia la lógica que ya tienes en el findAll)
        saludRepository.findFirstByUsuarioOrderByFechaMedicionDesc(perfil.getUsuario())
                .ifPresent(salud -> {
                    dto.setPeso(salud.getPeso());
                    dto.setEstatura(salud.getEstatura());
                });

        return ResponseEntity.ok(ApiResponseDto.<PerfilResponseDto>builder()
                .mensaje("Perfil recuperado con éxito")
                .datos(dto)
                .success(true)
                .build());
    }

    /**
     * Obtiene los detalles de un perfil específico por su ID.
     *
     * @param id el ID del perfil a buscar
     * @return ResponseEntity con los datos del perfil encontrado
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USUARIO')")
    @io.swagger.v3.oas.annotations.Operation(summary = "Recuperar perfil por ID", description = "Recupera los detalles de un perfil de usuario por su ID")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Perfil recuperado con éxito")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Perfil no encontrado")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<ApiResponseDto<PerfilResponseDto>> getPerfilById(
            @PathVariable("id") Long id) {

        log.info("Obteniendo perfil por ID: {}", id);
        PerfilResponseDto perfil = perfilService.findById(id);
        log.debug("Perfil encontrado: {} - {}", perfil.getId(), perfil.getNombre());

        return ResponseEntity.ok(ApiResponseDto.<PerfilResponseDto>builder()
                .mensaje("Perfil recuperado con éxito")
                .datos(perfil)
                .success(true)
                .build());
    }

    /**
     * Actualiza los datos de un perfil de usuario.
     * <p>
     * Permite a usuarios ADMIN o USUARIO actualizar la información del perfil.
     * </p>
     *
     * @param id  el ID del perfil a actualizar
     * @param dto los nuevos datos del perfil
     * @return ResponseEntity con el perfil actualizado
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USUARIO')")
    @io.swagger.v3.oas.annotations.Operation(summary = "Actualizar perfil", description = "Actualiza los datos de un perfil de usuario por su ID")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Perfil actualizado con éxito")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Perfil no encontrado")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<ApiResponseDto<PerfilResponseDto>> updatePerfil(
            @PathVariable("id") Long id,
            @Valid @RequestBody PerfilRequestDto dto) {

        log.info("Actualizando perfil ID: {}", id);
        PerfilResponseDto actualizado = perfilService.update(id, dto);

        return ResponseEntity.ok(ApiResponseDto.<PerfilResponseDto>builder()
                .mensaje("Perfil actualizado correctamente")
                .datos(actualizado)
                .success(true)
                .build());
    }
}
