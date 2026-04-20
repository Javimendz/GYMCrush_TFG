package com.example.gymcrush.data.remote.dto;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class PlanResponseDto {
    @SerializedName("id")
    private Long id;
    @SerializedName("nombre")
    private String nombre;
    @SerializedName("descripcion")
    private String descripcion;
    @SerializedName("objetivo")
    private String objetivo;
    @SerializedName("nivel")
    private String nivel;
    @SerializedName("ejercicios")
    private List<DetallePlanResponseDto> ejercicios;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public String getObjetivo() { return objetivo; }
    public void setObjetivo(String objetivo) { this.objetivo = objetivo; }
    public String getNivel() { return nivel; }
    public void setNivel(String nivel) { this.nivel = nivel; }
    public List<DetallePlanResponseDto> getEjercicios() { return ejercicios; }
    public void setEjercicios(List<DetallePlanResponseDto> ejercicios) { this.ejercicios = ejercicios; }
}