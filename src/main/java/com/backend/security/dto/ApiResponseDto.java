package com.backend.security.dto;



import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ApiResponseDto<T> {
    private String mensaje;
    private T datos;
    private boolean success;
}