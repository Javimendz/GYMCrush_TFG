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

    @SerializedName("categoriaId")
    private Long categoriaId;
    @SerializedName("categoria")
    private String categoria;

    @SerializedName("urlVideo")
    private String urlVideo;

    @SerializedName("urlImagen")
    private String urlImagen;

    public EntrenamientoResponseDto() {
    }

    // Getters
    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public Integer getDuracion() { return duracion; }
    public String getIntensidad() { return intensidad; }
    public String getCategoria() { return categoria; }
    public String getUrlVideo() { return urlVideo; }
    public String getUrlImagen() { return urlImagen; }

    // Setters (Opcionales pero recomendados)
    public void setId(Long id) { this.id = id; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public void setDuracion(Integer duracion) { this.duracion = duracion; }
    public void setIntensidad(String intensidad) { this.intensidad = intensidad; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public void setUrlVideo(String urlVideo) { this.urlVideo = urlVideo; }
    public void setUrlImagen(String urlImagen) { this.urlImagen = urlImagen; }
}