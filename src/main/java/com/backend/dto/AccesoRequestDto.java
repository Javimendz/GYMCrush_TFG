package com.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO para el acceso al chat mediante un token QR")
public class AccesoRequestDto {

    @NotBlank(message = "El token del QR es obligatorio")
    @Schema(description = "Token QR para acceder al chat", example = "123456")
    private String token;
}