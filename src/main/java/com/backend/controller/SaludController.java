package com.backend.controller;

import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import lombok.RequiredArgsConstructor;

import com.backend.dto.SaludRequestDto;
import com.backend.dto.SaludResponseDto;
import com.backend.security.dto.ApiResponseDto;
import com.backend.service.ISaludService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/salud")
@RequiredArgsConstructor
@io.swagger.v3.oas.annotations.tags.Tag(name = "Salud", description = "Operaciones relacionadas con la salud de los usuarios")
public class SaludController {

    private final ISaludService saludService;

    // Endpoint para crear registro
    @PostMapping("/usuario/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USUARIO')")
    @io.swagger.v3.oas.annotations.Operation(summary = "Registrar progreso de salud", description = "Permite a un usuario registrar su progreso de salud")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Progreso registrado con éxito")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Solicitud inválida, posiblemente datos incompletos")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<SaludResponseDto> crearRegistro(@PathVariable Long id,
            @Valid @RequestBody SaludRequestDto dto) {

        return ResponseEntity.ok(saludService.registrarProgreso(dto, id));
    }

    // Endpoint para obtener progreso actual
    @GetMapping("/usuario/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USUARIO')")
    @io.swagger.v3.oas.annotations.Operation(summary = "Obtener progreso actual", description = "Permite a un usuario obtener su progreso de salud más reciente")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Progreso actual recuperado con éxito")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "No se encontró progreso para el usuario")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<SaludResponseDto> getActual(@PathVariable Long id) {

        return ResponseEntity.ok(saludService.obtenerProgresoActual(id));
    }

    // Endpoint para obtener historial
    @GetMapping("/usuario/{id}/historial")
    @PreAuthorize("hasAnyRole('ADMIN', 'USUARIO')")
    @io.swagger.v3.oas.annotations.Operation(summary = "Obtener historial de salud", description = "Permite a un usuario obtener su historial de progreso de salud")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Historial recuperado con éxito")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "No se encontró historial para el usuario")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    //  Cambio el tipo de retorno aquí para que acepte el ApiResponseDto
    public ResponseEntity<ApiResponseDto<List<SaludResponseDto>>> getHistorial(@PathVariable Long id) {

        List<SaludResponseDto> historial = saludService.obtenerHistorialPorUsuario(id);

        ApiResponseDto<List<SaludResponseDto>> respuesta = ApiResponseDto.<List<SaludResponseDto>>builder()
                .mensaje("Historial de salud recuperado con éxito")
                .datos(historial)
                .success(true)
                .build();

        // devolvemos la respuesta completa
        return ResponseEntity.ok(respuesta);
    }

    // Endpoint para eliminar registro
    @DeleteMapping("/usuario/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USUARIO')")
    @io.swagger.v3.oas.annotations.Operation(summary = "Eliminar progreso de salud", description = "Permite a un usuario eliminar su progreso de salud más reciente")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Progreso eliminado con éxito")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "No se encontró progreso para el usuario")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    public ResponseEntity<ApiResponseDto<Void>> delete(@PathVariable Long id) {
       saludService.eliminarRegistro(id);

    //   respuesta de éxito
    ApiResponseDto<Void> respuesta = ApiResponseDto.<Void>builder()
            .mensaje("Registro de salud eliminado correctamente")
            .datos(null) // Tras borrar, no hay datos que enviar
            .success(true)
            .build();

    // .ok() para que el JSON con el mensaje llegue
    return ResponseEntity.ok(respuesta);
    }
}
