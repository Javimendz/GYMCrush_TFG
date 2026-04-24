package com.backend.domain;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.*;

/**
 * Entidad que representa la visualización de un tutorial por parte de un
 * usuario.
 * <p>
 * Actúa como tabla intermedia con datos extra (payload) entre Usuario y
 * Tutorial,
 * permitiendo guardar el progreso exacto del vídeo y si se ha completado.
 * </p>
 *
 * @author Backend Team
 * @version 1.0
 */
@Entity
@Table(name = "visualizaciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Visualizacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fecha_visualizacion", nullable = false)
    private LocalDateTime fechaVisualizacion;

    @Column(nullable = false)
    @Builder.Default
    private Boolean completado = false;

    @Column(name = "progreso_segundos", nullable = false)
    @Builder.Default
    private Integer progresoSegundos = 0;

    // Relación hacia el Usuario (Quién ve el vídeo)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    @ToString.Exclude
    private Usuario usuario;

    // Relación hacia el Tutorial (Qué vídeo está viendo)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tutorial_id", nullable = false)
    @ToString.Exclude
    private Tutorial tutorial;

    /**
     * Se ejecuta automáticamente antes de guardar por primera vez.
     */
    @PrePersist
    protected void onCreate() {
        if (this.fechaVisualizacion == null) {
            this.fechaVisualizacion = LocalDateTime.now();
        }
    }
}