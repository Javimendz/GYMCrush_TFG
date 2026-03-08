package com.backend.domain;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "sugerenciasDieta") 
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SugerenciaDieta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre_dieta", length = 30)
    private String nombreDieta;

    @Column(name = "calorias_objetivo")
    private Integer caloriasObjetivo;


    @Column(name = "macronutrientes_json")
    private String macronutrientesJson; 

    @Column(name = "tipo_dieta", length = 50)
    private String tipoDieta;

    @Column(name = "fecha_generacion")
    private LocalDateTime fechaGeneracion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuarioId", referencedColumnName = "id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dieta_id", referencedColumnName = "id")
    private Dieta dieta;
}