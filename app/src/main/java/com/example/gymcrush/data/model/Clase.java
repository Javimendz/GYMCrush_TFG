package com.example.gymcrush.data.model;

public class Clase {
    private Long id;
    private String nombre;
    private String hora;
    private String sala;
    private int cuposDisponibles;
    private String diaSemana;

    public Clase(Long id, String nombre, String hora, String sala, int cuposDisponibles, String diaSemana) {
        this.id = id;
        this.nombre = nombre;
        this.hora = hora;
        this.sala = sala;
        this.cuposDisponibles = cuposDisponibles;
        this.diaSemana = diaSemana;
    }

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public String getHora() { return hora; }
    public String getSala() { return sala; }
    public int getCuposDisponibles() { return cuposDisponibles; }
    public String getDiaSemana() { return diaSemana; }
}