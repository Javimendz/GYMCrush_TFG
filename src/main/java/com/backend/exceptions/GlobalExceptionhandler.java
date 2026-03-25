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

@RestControllerAdvice 
public class GlobalExceptionhandler {

    // ERRORES DE VALIDACIÓN DTOs con campos incorrectos
    //  3 métodos en uno que devuelve un String claro con todos los errores.
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
   
    // CREDENCIALES INVÁLIDAS 401
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponseDto<Void>> handleBadCredentialsException(BadCredentialsException ex) {
        ApiResponseDto<Void> response = ApiResponseDto.<Void>builder()
                .mensaje("Usuario o contraseña incorrectos.")
                .success(false)
                .build();

        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    // RECURSO NO ENCONTRADO 404
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponseDto<Void>> handleResourceNotFoundException(ResourceNotFoundException ex) {
        ApiResponseDto<Void> response = ApiResponseDto.<Void>builder()
                .mensaje(ex.getMessage())
                .success(false)
                .build();

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    // CONFLICTOS DE DATOS / INTEGRIDAD 409
    // Combinado para evitar el error de ambigüedad
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponseDto<Void>> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        String mensaje = "Error de base de datos: El registro ya existe o viola una restricción.";
        
        ApiResponseDto<Void> response = ApiResponseDto.<Void>builder()
                .mensaje(mensaje)
                .success(false)
                .build();

        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    // ACCESO DENEGADO 403
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponseDto<Void>> handleAccessDeniedException(AccessDeniedException ex) {
        ApiResponseDto<Void> response = ApiResponseDto.<Void>builder()
                .mensaje("No tienes permisos suficientes para esta acción.")
                .success(false)
                .build();

        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    // ERRORES DE LOGICA PERSONALIZADOS RuntimeException
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponseDto<Void>> handleRuntimeException(RuntimeException ex) {
        ApiResponseDto<Void> respuesta = ApiResponseDto.<Void>builder()
                .mensaje(ex.getMessage())
                .success(false)
                .build();
        
        return new ResponseEntity<>(respuesta, HttpStatus.BAD_REQUEST);
    }

    //  ERROR GENERICO 500
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