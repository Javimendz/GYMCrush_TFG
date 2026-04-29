package com.example.gymcrush.data.remote.dto;

import com.google.gson.annotations.SerializedName;

public class EntrenamientoRequestDto {
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

    public EntrenamientoRequestDto(String nombre, String descripcion, Integer duracion, String intensidad, String categoria) {
        this(nombre, descripcion, duracion, intensidad, categoria, "", "");
    }

    public EntrenamientoRequestDto(String nombre, String descripcion, Integer duracion, String intensidad, String categoria, String urlVideo) {
        this(nombre, descripcion, duracion, intensidad, categoria, urlVideo, "");
    }

    public EntrenamientoRequestDto(String nombre, String descripcion, Integer duracion, String intensidad, String categoria, String urlVideo, String urlImagen) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.duracion = duracion;
        this.intensidad = intensidad;
        this.categoria = categoria;
        this.urlVideo = urlVideo;
        this.urlImagen = urlImagen;
    }

    // Getters y Setters
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public Integer getDuracion() { return duracion; }
    public void setDuracion(Integer duracion) { this.duracion = duracion; }
    public String getIntensidad() { return intensidad; }
    public void setIntensidad(String intensidad) { this.intensidad = intensidad; }
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public String getUrlVideo() { return urlVideo; }
    public void setUrlVideo(String urlVideo) { this.urlVideo = urlVideo; }
    public String getUrlImagen() { return urlImagen; }
    public void setUrlImagen(String urlImagen) { this.urlImagen = urlImagen; }
}