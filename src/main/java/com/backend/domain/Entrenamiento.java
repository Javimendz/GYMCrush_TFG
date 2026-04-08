//Paquete
package com.backend.domain;

import java.time.LocalDate;
//Imports
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;//Importar todo el paquete
import lombok.*;

/**
 * Entidad que representa una sesión de entrenamiento personalizado.
 * <p>
 * Almacena información sobre rutinas de ejercicio,
 * incluyendo intensidad, duración y fechas programadas.
 * </p>
 *
 * @author Backend Team
 * @version 1.0
 * @since 2026
 */
@Entity
@Table(name = "entrenamientos")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Entrenamiento {

    /**
     * Identificador único de la sesión de entrenamiento.
     * <p>
     * Generado automáticamente por la base de datos.
     * </p>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Fecha programada para el entrenamiento.
     * <p>
     * Opcional, indica cuándo se realizará la sesión.
     * </p>
     */
    @Column(name = "fecha", nullable = true)
    private LocalDate fecha;


     /**
     * Descripción detallada de la sesión de entrenamiento.
     * <p>
     * Opcional, puede incluir información sobre el enfoque
     * o los objetivos específicos de esta sesión.
     * </p>
     */
    private Integer cantidadEjercicios;

    /**
     * Hora de inicio del entrenamiento.
     * <p>
     * Opcional, especifica la hora exacta de comienzo.
     * </p>
     */
    @Column(name = "hora", nullable = true)
    private LocalTime hora;

    /**
     * Duración total de la sesión de entrenamiento.
     * <p>
     * Opcional, representa el tiempo total en minutos.
     * </p>
     */
    @Column(name = "duracion", nullable = true)
    private Integer duracion;

    /**
     * Nivel de intensidad del entrenamiento.
     * <p>
     * Obligatorio, clasifica la dificultad (ej: baja, media, alta).
     * </p>
     */
    @Column(name = "intensidad", length = 150)
    private String intensidad;

    /**
     * Usuario que realiza el entrenamiento.
     * <p>
     * Relación muchos-a-uno con carga eager para obtener
     * información del usuario automáticamente.
     * </p>
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "usuario_id", referencedColumnName = "id", nullable = false)
    @ToString.Exclude
    private Usuario usuario;


    @OneToMany(mappedBy = "entrenamiento", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    @ToString.Exclude
    private List<Rutina> rutinas = new ArrayList<>();
}
