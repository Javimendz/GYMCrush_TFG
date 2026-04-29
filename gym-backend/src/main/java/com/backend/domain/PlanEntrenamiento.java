package com.backend.domain;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "planes_entrenamiento")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlanEntrenamiento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre; // Ej: "Rocoso 2.0"
    private String descripcion;
    private String objetivo; // PERDER_PESO, GANAR_MUSCULO
    private String nivel; // PRINCIPIANTE, INTERMEDIO, AVANZADO

   @OneToMany(mappedBy = "plan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetallePlan> ejercicios;
}