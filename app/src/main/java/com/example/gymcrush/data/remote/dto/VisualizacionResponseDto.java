package com.example.gymcrush.data.remote.dto;

import com.google.gson.annotations.SerializedName;

public class VisualizacionResponseDto {
    @SerializedName("id")
    private Long id;
    @SerializedName("tutorialId")
    private Long tutorialId;
    @SerializedName("progresoSegundos")
    private Integer progresoSegundos;
    @SerializedName("completado")
    private Boolean completado;

    public VisualizacionResponseDto() {}

    public Long getId() { return id; }
    public Long getTutorialId() { return tutorialId; }
    public Integer getProgresoSegundos() { return progresoSegundos; }
    public Boolean getCompletado() { return completado; }
}
