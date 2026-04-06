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
import lombok.extern.slf4j.Slf4j;

/**
 * Controlador REST para gestionar las operaciones relacionadas con el acceso al
 * sistema.
 * <p>
 * Este controlador proporciona endpoints para:
 * <ul>
 * <li>Generación de tokens QR para acceso al gimnasio</li>
 * <li>Validación de entrada mediante escaneo de QR</li>
 * <li>Validación de salida mediante escaneo de QR</li>
 * <li>Consulta de aforo actual del establecimiento</li>
 * <li>Consulta de historial de accesos del usuario</li>
 * </ul>
 * </p>
 *
 * @author Backend Team
 * @version 1.0
 * @since 2026
 */
@RestController
@RequestMapping("/api/v1/accesos")
@RequiredArgsConstructor
@Slf4j
@io.swagger.v3.oas.annotations.tags.Tag(name = "Acceso", description = "Operaciones relacionadas con el acceso al sistema")
public class AccesoController {

    private final IAccesoService accesoService;
    private final JwtGenerator jwtGenerator;

    /**
     * Genera un token QR para que el usuario pueda acceder al gimnasio.
     * <p>
     * El token generado es válido por 30 segundos y debe ser escaneado por el
     * personal
     * del gimnasio para registrar la entrada.
     * </p>
     *
     * @param auth la autenticación del usuario que solicita el token
     * @return ResponseEntity con el token QR generado y mensaje de confirmación
     */
    @GetMapping("/generar-qr")
    @PreAuthorize("hasAnyRole('ROLE_USUARIO', 'ROLE_ADMIN')")
    @io.swagger.v3.oas.annotations.Operation(summary = "Obtener token QR", description = "Genera un token QR válido por 30 segundos")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Token QR generado con éxito")
    public ResponseEntity<ApiResponseDto<String>> obtenerToken(Authentication auth) {
        log.info("Solicitud de generación de token QR para el usuario: {}", auth.getName());
        String tokenQr = jwtGenerator.generateQrToken(auth.getName(), null);
        log.info("Token QR generado para el usuario {}: {}", auth.getName(), tokenQr);
        return ResponseEntity.ok(ApiResponseDto.<String>builder()
                .mensaje("Token QR generado. Válido por 30 segundos.")
                .datos(tokenQr)
                .success(true)
                .build());
    }

    /**
     * Valida la entrada de un usuario al gimnasio mediante el escaneo de su token
     * QR.
     * <p>
     * Este endpoint es utilizado por el personal del gimnasio (ADMIN) para
     * registrar
     * la entrada del usuario en el sistema.
     * </p>
     *
     * @param dto el DTO que contiene el token QR a validar
     * @return ResponseEntity con confirmación de la entrada registrada
     */
    @PostMapping("/validar-entrada")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @io.swagger.v3.oas.annotations.Operation(summary = "Validar entrada", description = "Registra la entrada de un usuario por su token QR")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Token QR del usuario para registrar la entrada")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Entrada registrada con éxito")
    public ResponseEntity<ApiResponseDto<Void>> validarEntrada(@RequestBody AccesoRequestDto dto) {
        log.info("Solicitud de validación de entrada para el token QR: {}", dto.getToken());
        accesoService.registrarEntrada(dto.getToken());
        log.info("Entrada registrada con éxito para el token QR: {}", dto.getToken());
        return ResponseEntity.ok(ApiResponseDto.<Void>builder()
                .mensaje("Entrada registrada con éxito")
                .success(true)
                .build());
    }

    /**
     * Valida la salida de un usuario del gimnasio mediante el escaneo de su token
     * QR.
     * <p>
     * Este endpoint es utilizado por el personal del gimnasio (ADMIN) para
     * registrar
     * la salida del usuario en el sistema.
     * </p>
     *
     * @param dto el DTO que contiene el token QR a validar
     * @return ResponseEntity con confirmación de la salida registrada
     */
    @PostMapping("/validar-salida")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @io.swagger.v3.oas.annotations.Operation(summary = "Validar salida", description = "Registra la salida de un usuario por su token QR")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Token QR del usuario para registrar la salida")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Salida registrada con éxito")
    public ResponseEntity<ApiResponseDto<Void>> validarSalida(@RequestBody AccesoRequestDto dto) {
        log.info("Solicitud de validación de salida para el token QR: {}", dto.getToken());
        accesoService.registrarSalida(dto.getToken());
        log.info("Salida registrada con éxito para el token QR: {}", dto.getToken());
        return ResponseEntity.ok(ApiResponseDto.<Void>builder()
                .mensaje("Salida registrada con éxito. ¡Buen descanso!")
                .success(true)
                .build());
    }

    /**
     * Consulta el aforo actual del establecimiento.
     * <p>
     * Retorna el número de personas actualmente dentro del gimnasio.
     * Este endpoint puede ser consultado por usuarios y administradores.
     * </p>
     *
     * @return ResponseEntity con el número de personas dentro del gimnasio
     */
    @GetMapping("/aforo")
    @PreAuthorize("hasAnyRole('ROLE_USUARIO', 'ROLE_ADMIN')")
    @io.swagger.v3.oas.annotations.Operation(summary = "Consultar aforo", description = "Obtiene el número actual de personas dentro del establecimiento")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Aforo actual recuperado con éxito")
    public ResponseEntity<ApiResponseDto<Long>> consultarAforo() {
        log.info("Solicitud de consulta de aforo");
        long personasDentro = accesoService.obtenerAforoActual();
        log.info("Aforo actual: {} personas dentro", personasDentro);

        return ResponseEntity.ok(ApiResponseDto.<Long>builder()
                .mensaje("Aforo actual recuperado")
                .datos(personasDentro)
                .success(true)
                .build());

    }

    /**
     * Obtiene el historial de accesos del usuario autenticado.
     * <p>
     * Retorna una lista con todos los registros de entrada y salida del usuario
     * que realiza la petición.
     * </p>
     *
     * @param auth la autenticación del usuario que solicita su historial
     * @return ResponseEntity con la lista de accesos del usuario
     */
    @GetMapping("/historial")
    @PreAuthorize("hasAnyRole('ROLE_USUARIO', 'ROLE_ADMIN')")
    @io.swagger.v3.oas.annotations.Operation(summary = "Ver historial de accesos", description = "Obtiene el historial de accesos del usuario autenticado")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Historial de accesos recuperado con éxito")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "No se encontró historial para el usuario")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Acceso denegado. El usuario no tiene permisos para ver el historial")
    public ResponseEntity<ApiResponseDto<List<AccesoResponseDto>>> verHistorial(Authentication auth) {
        log.info("Solicitud de ver historial de accesos para el usuario: {}", auth.getName());
        List<AccesoResponseDto> historial = accesoService.obtenerHistorialUsuario(auth.getName());
        log.info("Historial de accesos recuperado para el usuario {}: {}", auth.getName(), historial);
        return ResponseEntity.ok(ApiResponseDto.<List<AccesoResponseDto>>builder()
                .mensaje("Historial de accesos recuperado correctamente")
                .datos(historial)
                .success(true)
                .build());
    }
}
