package com.example.gymcrush.data.remote.dto;

import com.google.gson.annotations.SerializedName;

public class TutorialResponseDto {
    @SerializedName("id")
    private Long id;
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
    @SerializedName("nombreCategoria")
    private String nombreCategoria;
    @SerializedName("musculoObjetivo")
    private String musculoObjetivo;
    @SerializedName("equipamiento")
    private String equipamiento;

    public TutorialResponseDto() {}

    public Long getId() { return id; }
    public String getTitulo() { return titulo; }
    public String getDescripcion() { return descripcion; }
    public String getUrlVideo() { return urlVideo; }
    public Integer getDuracionMin() { return duracionMin; }
    public Long getCategoriaId() { return categoriaId; }
    public String getNombreCategoria() { return nombreCategoria; }
    public String getMusculoObjetivo() { return musculoObjetivo; }
    public String getEquipamiento() { return equipamiento; }
}
