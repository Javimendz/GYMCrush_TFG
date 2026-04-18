package com.example.gymcrush.data.remote.dto;

import com.google.gson.annotations.SerializedName;

public class EntrenamientoResponseDto {
    @SerializedName("id")
    private Long id;
    @SerializedName("nombre")
    private String nombre;
    @SerializedName("descripcion")
    private String descripcion;
    @SerializedName("cantidadEjercicios")
    private Integer cantidadEjercicios;
    @SerializedName("duracion")
    private Integer duracion;
    @SerializedName("intensidad")
    private String intensidad;

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public Integer getCantidadEjercicios() { return cantidadEjercicios; }
    public Integer getDuracion() { return duracion; }
    public String getIntensidad() { return intensidad; }
}