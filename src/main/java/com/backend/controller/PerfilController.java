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
import com.backend.mapper.PerfilMapper;
import com.backend.repository.PerfilRepository;
import com.backend.repository.SaludRepository;
import com.backend.security.dto.ApiResponseDto;
import com.backend.service.IPerfilService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/api/v1/perfil")
@RequiredArgsConstructor
@io.swagger.v3.oas.annotations.tags.Tag(name = "Perfil", description = "Operaciones relacionadas con los perfiles de usuarios")
public class PerfilController {

    private final IPerfilService perfilService;
    private final PerfilMapper perfilMapper;
    private final PerfilRepository perfilRepository;
    private final SaludRepository saludRepository;

    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @io.swagger.v3.oas.annotations.Operation(summary = "Listar perfiles", description = "Recupera una lista de todos los perfiles de usuarios")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lista de perfiles recuperada con éxito")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public List<PerfilResponseDto> findAll() {
        // 1. Traemos las entidades Perfil
        List<Perfil> perfiles = perfilRepository.findAll();

        // Uso el stream
        return perfiles.stream().map(perfil -> {
            // Convierto a DTO con los campos de salud
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
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USUARIO')")
    @io.swagger.v3.oas.annotations.Operation(summary = "Recuperar perfil por ID", description = "Recupera los detalles de un perfil de usuario por su ID")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Perfil recuperado con éxito")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Perfil no encontrado")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<ApiResponseDto<PerfilResponseDto>> getPerfilById(@PathVariable Long id) {

        //  Busco el perfil 
        PerfilResponseDto perfil = perfilService.findById(id);

        // Creo el envoltorio 
        ApiResponseDto<PerfilResponseDto> respuesta = ApiResponseDto.<PerfilResponseDto>builder()
                .mensaje("Perfil recuperado con éxito") 
                .datos(perfil) 
                .success(true)
                .build();

        // respuesta
        return ResponseEntity.ok(respuesta);
    }

    // Actualizar perfil
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USUARIO')")
    @io.swagger.v3.oas.annotations.Operation(summary = "Actualizar perfil", description = "Actualiza los datos de un perfil de usuario por su ID")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Perfil actualizado con éxito")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Perfil no encontrado")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")  
    public ResponseEntity<ApiResponseDto<PerfilResponseDto>> updatePerfil(
            @PathVariable Long id,
            @Valid @RequestBody PerfilRequestDto dto) {

        PerfilResponseDto actualizado = perfilService.update(id, dto);

        ApiResponseDto<PerfilResponseDto> respuesta = ApiResponseDto.<PerfilResponseDto>builder()
                .mensaje("Perfil actualizado correctamente")
                .datos(actualizado)
                .success(true)
                .build();

        return ResponseEntity.ok(respuesta);
    }
}
