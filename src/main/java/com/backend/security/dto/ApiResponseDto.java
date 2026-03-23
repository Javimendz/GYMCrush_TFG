package com.backend.security.dto;



import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor 
@NoArgsConstructor
public class ApiResponseDto<T> {
    private String mensaje;
    private T datos;
    private boolean success;


    public static <T> ApiResponseDto<T> error(String mensaje) {
        return ApiResponseDto.<T>builder()
                .mensaje(mensaje)
                .success(false)
                .datos(null)
                .build();
    }
    public static <T> ApiResponseDto<T> success(String mensaje, T datos) {
        return ApiResponseDto.<T>builder()
                .mensaje(mensaje)
                .success(true)
                .datos(datos)
                .build();
    }
}