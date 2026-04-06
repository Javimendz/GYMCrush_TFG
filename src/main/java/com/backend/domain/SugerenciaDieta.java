package com.backend.domain;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entidad que representa una sugerencia de plan dietético.
 * <p>
 * Almacena recomendaciones personalizadas para usuarios,
 * incluyendo objetivos calóricos y macronutrientes en formato JSON.
 * </p>
 *
 * @author Backend Team
 * @version 1.0
 * @since 2026
 */
@Entity
@Table(name = "sugerenciasDieta")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SugerenciaDieta {

    /**
     * Identificador único de la sugerencia.
     * <p>
     * Generado automáticamente por la base de datos.
     * </p>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nombre descriptivo del plan dietético sugerido.
     * <p>
     * Obligatorio y con longitud máxima de 80 caracteres.
     * </p>
     */
    @Column(name = "nombre_dieta", length = 80)
    private String nombreDieta;

    /**
     * Calorías objetivo diarias del plan.
     * <p>
     * Obligatorio, define el meta calórica del usuario.
     * </p>
     */
    @Column(name = "calorias_objetivo")
    private Integer caloriasObjetivo;

    /**
     * Distribución de macronutrientes en formato JSON.
     * <p>
     * Opcional, permite almacenar información nutricional
     * estructurada sin límites de longitud.
     * </p>
     */
    @Column(name = "macronutrientes_json", columnDefinition = "TEXT")
    private String macronutrientesJson;

    /**
     * Tipo de dieta sugerida.
     * <p>
     * Obligatorio, clasifica el plan (ej: keto, vegana, etc.).
     * </p>
     */
    @Column(name = "tipo_dieta", length = 50)
    private String tipoDieta;

    /**
     * Fecha y hora de generación de la sugerencia.
     * <p>
     * Obligatorio, no modificable después de creación.
     * </p>
     */
    @Column(name = "fecha_generacion", nullable = false, updatable = false)
    private LocalDateTime fechaGeneracion;

    private boolean esFavorita;

    /**
     * Usuario que recibe la sugerencia.
     * <p>
     * Relación muchos-a-uno con carga eager para obtener
     * información del usuario automáticamente.
     * </p>
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    @ToString.Exclude
    private Usuario usuario;

    /**
     * Plan dietético asociado a esta sugerencia.
     * <p>
     * Relación muchos-a-uno con carga eager para obtener
     * información completa del plan dietético.
     * </p>
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dieta_id", nullable = false)
    @ToString.Exclude
    private Dieta dieta;

    /**
     * Método callback que se ejecuta antes de persistir la sugerencia.
     * <p>
     * Establece la fecha de generación actual si no se especifica.
     * </p>
     */
    @PrePersist
    protected void onCreate() {
        // Establecer fecha de generación automática
        this.fechaGeneracion = LocalDateTime.now();
    }
}