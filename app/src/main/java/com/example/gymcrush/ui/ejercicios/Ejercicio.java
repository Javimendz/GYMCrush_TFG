package com.example.gymcrush.ui.ejercicios;

public class Ejercicio {
    private String nombre;
    private String categoria;
    private String descripcion;
    private String duracion;
    private String urlVideo;
    private int imagenResId;
    private String dificultad;
    private boolean seleccionado;
    
    // Nuevos campos para rutinas personalizadas
    private String series = "0";
    private String repeticiones = "0";

    // CONSTRUCTOR 1
    public Ejercicio(String nombre) {
        this.nombre = nombre;
        this.seleccionado = false;
    }

    // CONSTRUCTOR 2
    public Ejercicio(String nombre, String categoria, String descripcion, String duracion, String urlVideo, int imagenResId, String dificultad) {
        this.nombre = nombre;
        this.categoria = categoria;
        this.descripcion = descripcion;
        this.duracion = duracion;
        this.urlVideo = urlVideo;
        this.imagenResId = imagenResId;
        this.dificultad = dificultad;
        this.seleccionado = false;
    }

    // --- GETTERS Y SETTERS ---
    public String getNombre() { return nombre; }
    public String getCategoria() { return categoria; }
    public String getDescripcion() { return descripcion; }
    public String getDuracion() { return duracion; }
    public String getUrlVideo() { return urlVideo; }
    public int getImagenResId() { return imagenResId; }
    public String getDificultad() { return dificultad; }
    public boolean isSeleccionado() { return seleccionado; }
    public void setSeleccionado(boolean seleccionado) { this.seleccionado = seleccionado; }

    public String getSeries() { return series; }
    public void setSeries(String series) { this.series = series; }
    public String getRepeticiones() { return repeticiones; }
    public void setRepeticiones(String repeticiones) { this.repeticiones = repeticiones; }
}