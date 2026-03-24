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
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice 
public class GlobalExceptionhandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponseDto<Map<String, String>>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> validationErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> 
            validationErrors.put(error.getField(), error.getDefaultMessage())
        );

        ApiResponseDto<Map<String, String>> response = ApiResponseDto.<Map<String, String>>builder()
                .mensaje("Errores de validación en los datos enviados")
                .success(false)
                .datos(validationErrors) 
                .build();

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    // manejar errores globales
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseDto<Void>> handleGlobalException(Exception ex) {
        ApiResponseDto<Void> respuesta = ApiResponseDto.<Void>builder()
                .mensaje("Ha ocurrido un error inesperado en el servidor")
                .success(false)
                .build();
        
        return new ResponseEntity<>(respuesta, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    // Manejo de credenciales inválidas (401)
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponseDto<Void>> handleBadCredentialsException(BadCredentialsException ex) {
        ApiResponseDto<Void> response = ApiResponseDto.<Void>builder()
                .mensaje("Usuario o contraseña incorrectos.") // Mensaje genérico por seguridad
                .success(false)
                .datos(null)
                .build();

        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED); // 401 Unauthorized
    }
    // Recurso no encontrado (404)
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponseDto<Void>> handleResourceNotFoundException(ResourceNotFoundException ex) {
        ApiResponseDto<Void> response = ApiResponseDto.<Void>builder()
                .mensaje(ex.getMessage())
                .success(false)
                .datos(null)
                .build();

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    //  Conflictos de datos (409)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponseDto<Void>> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        String detail = ex.getRootCause() != null ? ex.getRootCause().getMessage() : "Conflicto de integridad de datos";
        
        ApiResponseDto<Void> response = ApiResponseDto.<Void>builder()
                .mensaje("Error de base de datos: " + detail)
                .success(false)
                .datos(null)
                .build();

        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    // Acceso denegado (403)
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponseDto<Void>> handleAccessDeniedException(AccessDeniedException ex) {
        ApiResponseDto<Void> response = ApiResponseDto.<Void>builder()
                .mensaje("No tienes permisos suficientes para realizar esta acción.")
                .success(false)
                .datos(null)
                .build();

        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    //  Errores generales (500)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseDto<Void>> handleGeneralException(Exception ex) {
        ex.printStackTrace(); // Log para el desarrollador

        ApiResponseDto<Void> response = ApiResponseDto.<Void>builder()
                .mensaje("Ocurrió un error inesperado en el sistema.")
                .success(false)
                .datos(null)
                .build();

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    // Validación de datos
    // Maneja excepciones de validación de datos
    @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponseDto<Void>> handleValidationException(org.springframework.web.bind.MethodArgumentNotValidException ex) {
        String mensajeError = ex.getBindingResult().getFieldError().getDefaultMessage();
        
        ApiResponseDto<Void> respuesta = ApiResponseDto.<Void>builder()
                .mensaje("Error de validación: " + mensajeError)
                .success(false)
                .build();
                
        return new ResponseEntity<>(respuesta, HttpStatus.BAD_REQUEST);
    }

    //CAPTURA ERRORES DE LÓGICA (Los "throw new RuntimeException" que pusimos)
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponseDto<Void>> handleRuntimeException(RuntimeException ex) {
        ApiResponseDto<Void> respuesta = ApiResponseDto.<Void>builder()
                .mensaje(ex.getMessage()) // "Error: La categoría seleccionada no existe"
                .success(false)
                .datos(null)
                .build();
        
        return new ResponseEntity<>(respuesta, HttpStatus.BAD_REQUEST);
    }


    

    // CAPTURA ERRORES DE BASE DE DATOS 
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponseDto<Void>> handleDataIntegrity(DataIntegrityViolationException ex) {
        ApiResponseDto<Void> respuesta = ApiResponseDto.<Void>builder()
                .mensaje("Error de base de datos: El registro ya existe o viola una restricción de integridad.")
                .success(false)
                .build();
        
        return new ResponseEntity<>(respuesta, HttpStatus.CONFLICT);
    }

    // CAPTURA ERRORES DE VALIDACIÓN (Si falta un campo en el JSON)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponseDto<Void>> handleValidation(MethodArgumentNotValidException ex) {
        String errores = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(" | "));

        ApiResponseDto<Void> respuesta = ApiResponseDto.<Void>builder()
                .mensaje("Campos incorrectos: " + errores)
                .success(false)
                .build();
                
        return new ResponseEntity<>(respuesta, HttpStatus.BAD_REQUEST);
    }
}
