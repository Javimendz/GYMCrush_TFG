package com.example.gymcrush.data.remote.dto;

import com.google.gson.annotations.SerializedName;
import java.time.LocalDate;

public class RutinaResponseDto {
    @SerializedName("id")
    private Long id;
    @SerializedName("fechaAsignacion")
    private String fechaAsignacion;
    @SerializedName("orden")
    private Integer orden;
    @SerializedName("completado")
    private Boolean completado;
    @SerializedName("entrenamientoId")
    private Long entrenamientoId;
    @SerializedName("nombreEntrenamiento")
    private String nombreEntrenamiento;
    @SerializedName("duracion")
    private Integer duracion;
    @SerializedName("intensidad")
    private String intensidad;
    @SerializedName("urlVideo")
    private String urlVideo;
    @SerializedName("planNombre")
    private String planNombre;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getFechaAsignacion() { return fechaAsignacion; }
    public void setFechaAsignacion(String fechaAsignacion) { this.fechaAsignacion = fechaAsignacion; }
    public Integer getOrden() { return orden; }
    public void setOrden(Integer orden) { this.orden = orden; }
    public Boolean getCompletado() { return completado; }
    public void setCompletado(Boolean completado) { this.completado = completado; }
    public Long getEntrenamientoId() { return entrenamientoId; }
    public void setEntrenamientoId(Long entrenamientoId) { this.entrenamientoId = entrenamientoId; }
    public String getNombreEntrenamiento() { return nombreEntrenamiento; }
    public void setNombreEntrenamiento(String nombreEntrenamiento) { this.nombreEntrenamiento = nombreEntrenamiento; }
    public Integer getDuracion() { return duracion; }
    public void setDuracion(Integer duracion) { this.duracion = duracion; }
    public String getIntensidad() { return intensidad; }
    public void setIntensidad(String intensidad) { this.intensidad = intensidad; }
    public String getUrlVideo() { return urlVideo; }
    public void setUrlVideo(String urlVideo) { this.urlVideo = urlVideo; }
    public String getPlanNombre() { return planNombre; }
    public void setPlanNombre(String planNombre) { this.planNombre = planNombre; }
}