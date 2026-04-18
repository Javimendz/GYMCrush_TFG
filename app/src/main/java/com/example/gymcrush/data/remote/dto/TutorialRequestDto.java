package com.example.gymcrush.data.remote.dto;

import com.google.gson.annotations.SerializedName;

public class TutorialRequestDto {
    @SerializedName("titulo")
    private String titulo;
    @SerializedName("descripcion")
    private String descripcion;
    @SerializedName("urlVideo")
    private String urlVideo;
    @SerializedName("duracionMin")
    private Integer duracionMin;
    @SerializedName("categoriaId")
    private Long categoriaId;
    @SerializedName("musculoObjetivo")
    private String musculoObjetivo;
    @SerializedName("equipamiento")
    private String equipamiento;

    public TutorialRequestDto() {}

    public TutorialRequestDto(String titulo, String descripcion, String urlVideo, Integer duracionMin, Long categoriaId, String musculoObjetivo, String equipamiento) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.urlVideo = urlVideo;
        this.duracionMin = duracionMin;
        this.categoriaId = categoriaId;
        this.musculoObjetivo = musculoObjetivo;
        this.equipamiento = equipamiento;
    }

    // Getters y Setters
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public String getUrlVideo() { return urlVideo; }
    public void setUrlVideo(String urlVideo) { this.urlVideo = urlVideo; }
    public Integer getDuracionMin() { return duracionMin; }
    public void setDuracionMin(Integer duracionMin) { this.duracionMin = duracionMin; }
    public Long getCategoriaId() { return categoriaId; }
    public void setCategoriaId(Long categoriaId) { this.categoriaId = categoriaId; }
    public String getMusculoObjetivo() { return musculoObjetivo; }
    public void setMusculoObjetivo(String musculoObjetivo) { this.musculoObjetivo = musculoObjetivo; }
    public String getEquipamiento() { return equipamiento; }
    public void setEquipamiento(String equipamiento) { this.equipamiento = equipamiento; }
}
