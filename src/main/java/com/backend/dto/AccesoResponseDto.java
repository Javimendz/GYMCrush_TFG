package com.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO para la respuesta del acceso al chat mediante un token QR")
public class AccesoResponseDto {
    @Schema(description = "ID único del acceso", example = "1001")
    private Long id;
    @Schema(description = "Nombre de usuario que accedió al chat", example = "usuario123")
    private String username;
    @Schema(description = "Fecha y hora de entrada al chat", example = "2023-12-01T10:00:00")
    private LocalDateTime fechaHoraEntrada;
    @Schema(description = "Fecha y hora de salida del chat", example = "2023-12-01T12:00:00")
    private LocalDateTime fechaHoraSalida;
    @Schema(description = "Tipo de acceso (ENTRADA o SALIDA)", example = "ENTRADA")
    private String tipo; // ENTRADA o SALIDA
    @Schema(description = "Mensaje relacionado con el acceso", example = "Acceso concedido")
    private String mensaje; 
}