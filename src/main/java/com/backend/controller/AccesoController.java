package com.backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.dto.AccesoRequestDto;
import com.backend.dto.AccesoResponseDto;
import com.backend.security.dto.ApiResponseDto;
import com.backend.security.jwt.JwtGenerator;
import com.backend.service.IAccesoService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/accesos")
@RequiredArgsConstructor
@io.swagger.v3.oas.annotations.tags.Tag(name = "Acceso", description = "Operaciones relacionadas con el acceso al sistema")
public class AccesoController {

    private final IAccesoService accesoService;
    private final JwtGenerator jwtGenerator;

    // El usuario genera su QR en el móvil
    @GetMapping("/generar-qr")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @io.swagger.v3.oas.annotations.Operation(summary = "Obtener token QR", description = "Genera un token QR válido por 30 segundos")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Token QR generado con éxito")
    public ResponseEntity<ApiResponseDto<String>> obtenerToken(Authentication auth) {
        // obtenemos el token
        String tokenQr = jwtGenerator.generateQrToken(auth.getName(), null);

        return ResponseEntity.ok(ApiResponseDto.<String>builder()
                .mensaje("Token QR generado. Válido por 30 segundos.")
                .datos(tokenQr)
                .success(true)
                .build());
    }

    @PostMapping("/validar-entrada")
    @PreAuthorize("hasRole('ADMIN')")
    @io.swagger.v3.oas.annotations.Operation(summary = "Validar entrada", description = "Registra la entrada de un usuario por su token QR")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Token QR del usuario para registrar la entrada")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Entrada registrada con éxito")
    public ResponseEntity<ApiResponseDto<Void>> validarEntrada(@RequestBody AccesoRequestDto dto) {
        accesoService.registrarEntrada(dto.getToken());
        return ResponseEntity.ok(ApiResponseDto.<Void>builder()
                .mensaje("Entrada registrada con éxito")
                .success(true)
                .build());
    }

    @PostMapping("/validar-salida")
    @PreAuthorize("hasRole('ADMIN')")
    @io.swagger.v3.oas.annotations.Operation(summary = "Validar salida", description = "Registra la salida de un usuario por su token QR")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Token QR del usuario para registrar la salida")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Salida registrada con éxito")
    public ResponseEntity<ApiResponseDto<Void>> validarSalida(@RequestBody AccesoRequestDto dto) {
        accesoService.registrarSalida(dto.getToken());
        return ResponseEntity.ok(ApiResponseDto.<Void>builder()
                .mensaje("Salida registrada con éxito. ¡Buen descanso!")
                .success(true)
                .build());
    }

    @GetMapping("/aforo")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @io.swagger.v3.oas.annotations.Operation(summary = "Consultar aforo", description = "Obtiene el número actual de personas dentro del establecimiento")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Aforo actual recuperado con éxito")
    public ResponseEntity<ApiResponseDto<Long>> consultarAforo() {

        long personasDentro = accesoService.obtenerAforoActual();

        return ResponseEntity.ok(ApiResponseDto.<Long>builder()
                .mensaje("Aforo actual recuperado")
                .datos(personasDentro)
                .success(true)
                .build());

    }

    @GetMapping("/historial")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @io.swagger.v3.oas.annotations.Operation(summary = "Ver historial de accesos", description = "Obtiene el historial de accesos del usuario autenticado")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Historial de accesos recuperado con éxito")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "No se encontró historial para el usuario")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Acceso denegado. El usuario no tiene permisos para ver el historial")
    public ResponseEntity<ApiResponseDto<List<AccesoResponseDto>>> verHistorial(Authentication auth) {
        List<AccesoResponseDto> historial = accesoService.obtenerHistorialUsuario(auth.getName());

        return ResponseEntity.ok(ApiResponseDto.<List<AccesoResponseDto>>builder()
                .mensaje("Historial de accesos recuperado correctamente")
                .datos(historial)
                .success(true)
                .build());
    }
}
