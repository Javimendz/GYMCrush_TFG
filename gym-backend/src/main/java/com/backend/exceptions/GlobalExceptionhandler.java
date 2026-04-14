package com.backend.exceptions;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.backend.security.dto.ApiResponseDto;
import java.util.stream.Collectors;

/**
 * Manejador global de excepciones para la API REST.
 * <p>
 * Esta clase centraliza el manejo de todas las excepciones lanzadas por los controladores,
 * transformándolas en respuestas HTTP estandarizadas con {@link ApiResponseDto}.
 * </p>
 * <p>
 * Excepciones manejadas:
 * <ul>
 *   <li>{@link MethodArgumentNotValidException} - Errores de validación (400)</li>
 *   <li>{@link BadCredentialsException} - Credenciales inválidas (401)</li>
 *   <li>{@link ResourceNotFoundException} - Recurso no encontrado (404)</li>
 *   <li>{@link DataIntegrityViolationException} - Violación de integridad (409)</li>
 *   <li>{@link AccessDeniedException} - Acceso denegado (403)</li>
 *   <li>{@link RuntimeException} - Errores de lógica de negocio (400)</li>
 *   <li>{@link Exception} - Errores genéricos del sistema (500)</li>
 * </ul>
 * </p>
 *
 * @author Backend Team
 * @version 1.0
 * @since 2026
 */
@RestControllerAdvice 
public class GlobalExceptionhandler {

    /**
     * Maneja errores de validación en DTOs con campos incorrectos.
     * Devuelve un string con todos los errores de validación concatenados.
     *
     * @param ex la excepción de validación con los errores de binding
     * @return ResponseEntity con mensaje de error y código HTTP 400 (BAD_REQUEST)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponseDto<Void>> handleValidationException(MethodArgumentNotValidException ex) {
        String errores = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(" | "));

        ApiResponseDto<Void> respuesta = ApiResponseDto.<Void>builder()
                .mensaje("Error de validación: " + errores)
                .success(false)
                .build();
                
        return new ResponseEntity<>(respuesta, HttpStatus.BAD_REQUEST);
    }
   
    /**
     * Maneja intentos de autenticación con credenciales inválidas.
     *
     * @param ex la excepción de credenciales incorrectas
     * @return ResponseEntity con mensaje de error y código HTTP 401 (UNAUTHORIZED)
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponseDto<Void>> handleBadCredentialsException(BadCredentialsException ex) {
        ApiResponseDto<Void> response = ApiResponseDto.<Void>builder()
                .mensaje("Usuario o contraseña incorrectos.")
                .success(false)
                .build();

        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    /**
     * Maneja el caso en que un recurso solicitado no existe en la base de datos.
     *
     * @param ex la excepción de recurso no encontrado
     * @return ResponseEntity con mensaje de error y código HTTP 404 (NOT_FOUND)
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponseDto<Void>> handleResourceNotFoundException(ResourceNotFoundException ex) {
        ApiResponseDto<Void> response = ApiResponseDto.<Void>builder()
                .mensaje(ex.getMessage())
                .success(false)
                .build();

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    /**
     * Maneja violaciones de integridad de datos (registros duplicados, constraints, etc.).
     *
     * @param ex la excepción de violación de integridad
     * @return ResponseEntity con mensaje de error y código HTTP 409 (CONFLICT)
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponseDto<Void>> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        String mensaje = "Error de base de datos: El registro ya existe o viola una restricción.";
        
        ApiResponseDto<Void> response = ApiResponseDto.<Void>builder()
                .mensaje(mensaje)
                .success(false)
                .build();

        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    /**
     * Maneja intentos de acceso a recursos sin los permisos necesarios.
     *
     * @param ex la excepción de acceso denegado
     * @return ResponseEntity con mensaje de error y código HTTP 403 (FORBIDDEN)
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponseDto<Void>> handleAccessDeniedException(AccessDeniedException ex) {
        ApiResponseDto<Void> response = ApiResponseDto.<Void>builder()
                .mensaje("No tienes permisos suficientes para esta acción.")
                .success(false)
                .build();

        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    /**
     * Maneja excepciones de tiempo de ejecución de la lógica de negocio.
     *
     * @param ex la excepción de tiempo de ejecución
     * @return ResponseEntity con mensaje de error y código HTTP 400 (BAD_REQUEST)
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponseDto<Void>> handleRuntimeException(RuntimeException ex) {
        ApiResponseDto<Void> respuesta = ApiResponseDto.<Void>builder()
                .mensaje(ex.getMessage())
                .success(false)
                .build();
        
        return new ResponseEntity<>(respuesta, HttpStatus.BAD_REQUEST);
    }

    /**
     * Manejador de último recurso para cualquier excepción no capturada.
     * Imprime el stack trace para debugging en la consola.
     *
     * @param ex la excepción genérica capturada
     * @return ResponseEntity con mensaje de error genérico y código HTTP 500 (INTERNAL_SERVER_ERROR)
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseDto<Void>> handleGeneralException(Exception ex) {
        ex.printStackTrace(); // Para que tú lo veas en la consola de Docker

        ApiResponseDto<Void> response = ApiResponseDto.<Void>builder()
                .mensaje("Ocurrió un error inesperado en el sistema.")
                .success(false)
                .build();

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}