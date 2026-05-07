package com.example.gymcrush.ui.ejercicios;

public class Ejercicio {
    private Long id;
    private String nombre;
    private String categoria;
    private String descripcion;
    private String duracion;
    private String urlVideo;
    private int imagenResId;
    private String dificultad;
    private boolean seleccionado;
    private boolean esGlobal;
    
    // Nuevos campos para rutinas personalizadas
    private String series = "0";
    private String repeticiones = "0";

    // CONSTRUCTOR 1
    public Ejercicio(Long id, String nombre) {
        this.id = id;
        this.nombre = nombre;
        this.seleccionado = false;
    }

    public Ejercicio(Long id, String nombre, String categoria, String descripcion, String duracion, String urlVideo, int imagenResId, String dificultad) {
        this.id = id;
        this.nombre = nombre;
        this.categoria = categoria;
        this.descripcion = descripcion;
        this.duracion = duracion;
        this.urlVideo = urlVideo;
        this.imagenResId = imagenResId;
        this.dificultad = dificultad;
        this.seleccionado = false;
    }

    public Ejercicio(String nombre, String categoria, String descripcion, String duracion, String urlVideo, int imagenResId, String dificultad) {
        this(null, nombre, categoria, descripcion, duracion, urlVideo, imagenResId, dificultad);
    }

    // --- GETTERS Y SETTERS ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public String getCategoria() { return categoria; }
    public String getDescripcion() { return descripcion; }
    public String getDuracion() { return duracion; }
    public String getUrlVideo() { return urlVideo; }
    public int getImagenResId() { return imagenResId; }
    public String getDificultad() { return dificultad; }
    public boolean isSeleccionado() { return seleccionado; }
    public void setSeleccionado(boolean seleccionado) { this.seleccionado = seleccionado; }
    public boolean isEsGlobal() { return esGlobal; }
    public void setEsGlobal(boolean esGlobal) { this.esGlobal = esGlobal; }

    public String getSeries() { return series; }
    public void setSeries(String series) { this.series = series; }
    public String getRepeticiones() { return repeticiones; }
    public void setRepeticiones(String repeticiones) { this.repeticiones = repeticiones; }
}