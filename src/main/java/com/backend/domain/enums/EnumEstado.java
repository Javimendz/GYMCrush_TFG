package com.backend.domain.enums;

public enum EnumEstado {
    CONFIRMADA,      // El usuario tiene su hueco asegurado.
    CANCELADA,       // El usuario canceló (idealmente antes de que empiece).
    ASISTIDA,        // El profesor marcó que el usuario vino (útil para estadísticas).
    NO_PRESENTADO,   // El usuario no vino y no canceló (el famoso "No-show").
    LISTA_ESPERA     // La clase estaba llena y está el primero para entrar si alguien cancela.
}

