package com.backend.domain;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@RequiredArgsConstructor
@Getter
@Setter
@Table(name = "planes_nutricionales")
public class Nutricion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String tipoDieta;
    private java.time.LocalDateTime fechaGeneracion;
    private String nombreDieta; // Ej: "Volumen", "Definición"
    private Integer caloriasObjetivo;
    
    @Column(columnDefinition = "TEXT")
    private String macronutrientesJson; // Aquí guardas el string de proteinas, grasas, etc.

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario; // Relación con tu tabla de usuarios

    @OneToMany(mappedBy = "plan", cascade = CascadeType.ALL)
    private List<ComidaDiaria> comidas; // ESTO ES LO QUE TE FALTA: La conexión con los platos
}