package com.example.gymcrush.data.remote.dto;

import com.google.gson.annotations.SerializedName;

/**
 * DTO para recibir la respuesta de horarios desde el Backend.
 * Optimizado para mostrar en RecyclerView (Clases de CrossFit, Yoga, etc.)
 */
public class HorarioResponseDto {

    @SerializedName("id")
    private Long id;

    @SerializedName("diaSemana")
    private String diaSemana;

    @SerializedName("horaInicio")
    private String horaInicio; // Recibido como "HH:mm:ss" o "HH:mm"

    @SerializedName("horaFin")
    private String horaFin;

    @SerializedName("aforoMax")
    private Integer aforoMax;

    @SerializedName("ocupacion")
    private Integer ocupacion;

    @SerializedName("nombreSala")
    private String nombreSala;

    @SerializedName("actividadId")
    private Long actividadId;

    @SerializedName("nombreActividad")
    private String nombreActividad;

    @SerializedName("salaId")
    private Long salaId;

    @SerializedName("nombreEntrenador")
    private String nombreEntrenador;

    // Constructor vacío requerido por Retrofit/GSON
    public HorarioResponseDto() {
    }

    // --- GETTERS ---
    // En Android solemos usar solo Getters para los DTOs de respuesta (inmutabilidad)

    public Long getId() { return id; }

    public String getDiaSemana() { return diaSemana; }

    public String getHoraInicio() {
        // Opcional: Limpiar el formato si viene con segundos (08:00:00 -> 08:00)
        if (horaInicio != null && horaInicio.length() > 5) {
            return horaInicio.substring(0, 5);
        }
        return horaInicio;
    }

    public String getHoraFin() {
        if (horaFin != null && horaFin.length() > 5) {
            return horaFin.substring(0, 5);
        }
        return horaFin;
    }

    public Integer getAforoMax() { return aforoMax; }

    public Integer getOcupacion() { return ocupacion != null ? ocupacion : 0; }

    public String getNombreSala() { return nombreSala; }

    public Long getActividadId() { return actividadId; }

    public String getNombreActividad() { return nombreActividad; }

    public Long getSalaId() { return salaId; }

    public String getNombreEntrenador() { return nombreEntrenador; }
}
