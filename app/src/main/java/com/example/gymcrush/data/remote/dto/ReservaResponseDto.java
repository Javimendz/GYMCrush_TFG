package com.example.gymcrush.data.remote.dto;

import com.google.gson.annotations.SerializedName;

public class ReservaResponseDto {
    @SerializedName("id")
    private Long id;
    
    @SerializedName("fecha")
    private String fecha;
    
    @SerializedName("estado")
    private String estado;
    
    @SerializedName("confirmado")
    private Boolean confirmado;
    
    @SerializedName("horarioId")
    private Long horarioId;

    @SerializedName("usuarioId")
    private Long usuarioId;
    
    @SerializedName("nombreActividad")
    private String nombreActividad;
    
    @SerializedName("nombreSala")
    private String nombreSala;
    
    @SerializedName("horaInicio")
    private String horaInicio;

    @SerializedName("username")
    private String username;

    @SerializedName("diaSemana")
    private String diaSemana;

    public Long getId() { return id; }
    public String getFecha() { return fecha; }
    public String getEstado() { return estado; }
    public Boolean getConfirmado() { return confirmado; }
    public Long getHorarioId() { return horarioId; }
    public Long getUsuarioId() { return usuarioId; }
    public String getNombreActividad() { return nombreActividad; }
    public String getNombreSala() { return nombreSala; }
    public String getHoraInicio() { return horaInicio; }
    public String getUsername() { return username; }
    public String getDiaSemana() { return diaSemana; }
}