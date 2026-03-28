package com.backend.dto;

import lombok.Data;

@Data
public class PerfilFisicoRequest {
    private Long usuarioId;
    private Double peso;
    private Double altura;
    private String objetivo; // "GANAR", "PERDER" o "MANTENER"
}
