package com.example.gymcrush.data.remote.dto;

import com.google.gson.annotations.SerializedName;

public class DetallePlanRequestDto {
    @SerializedName("entrenamientoId")
    private Long entrenamientoId;
    @SerializedName("diaSemana")
    private Integer diaSemana;
    @SerializedName("orden")
    private Integer orden;

    public DetallePlanRequestDto(Long entrenamientoId, Integer diaSemana, Integer orden) {
        this.entrenamientoId = entrenamientoId;
        this.diaSemana = diaSemana;
        this.orden = orden;
    }

    // Getters and Setters
    public Long getEntrenamientoId() { return entrenamientoId; }
    public void setEntrenamientoId(Long entrenamientoId) { this.entrenamientoId = entrenamientoId; }
    public Integer getDiaSemana() { return diaSemana; }
    public void setDiaSemana(Integer diaSemana) { this.diaSemana = diaSemana; }
    public Integer getOrden() { return orden; }
    public void setOrden(Integer orden) { this.orden = orden; }
}
