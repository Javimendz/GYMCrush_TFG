package com.backend.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Entity
@Data
@RequiredArgsConstructor
@Table(name = "comidas_diarias")
public class ComidaDiaria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String momento; // "Desayuno", "Almuerzo", "Cena"
    private String nombreAlimento; // Lo que viene de FatSecret
    private Double cantidadGramos;
    private Double calorias;
    private String proteina; // Lo que viene de FatSecret
    private String carbohidratos; // Lo que viene de FatSecret
    private String grasas; // Lo que viene de FatSecret
    private String imagenUrl; // URL de la imagen del alimento, si quieres mostrarla en el frontend
    @ManyToOne
    @JoinColumn(name = "plan_id")
    private Nutricion plan; // Para saber a qué plan pertenece este plato
}