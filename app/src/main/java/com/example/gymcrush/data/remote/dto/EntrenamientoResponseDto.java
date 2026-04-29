package com.example.gymcrush.data.remote.dto;

import com.google.gson.annotations.SerializedName;

public class EntrenamientoResponseDto {
    @SerializedName("id")
    private Long id;
    @SerializedName("nombre")
    private String nombre;
    @SerializedName("descripcion")
    private String descripcion;
    @SerializedName("duracion")
    private Integer duracion;
    @SerializedName("intensidad")
    private String intensidad;
    @SerializedName("categoria")
    private String categoria;
    @SerializedName("urlVideo")
    private String urlVideo;
    @SerializedName("urlImagen")
    private String urlImagen;

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public Integer getDuracion() { return duracion; }
    public String getIntensidad() { return intensidad; }
    public String getCategoria() { return categoria; }
    public String getUrlVideo() { return urlVideo; }
    public String getUrlImagen() { return urlImagen; }
}