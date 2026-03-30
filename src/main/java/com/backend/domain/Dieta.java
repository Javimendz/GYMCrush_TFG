package com.backend.domain;

import java.util.List;
import jakarta.persistence.*;
import lombok.*;

/**
 * Entidad que representa un plan dietético personalizado.
 * <p>
 * Almacena información nutricional detallada incluyendo
 * macronutrientes y categorías para planes alimenticios.
 * </p>
 *
 * @author Backend Team
 * @version 1.0
 * @since 2026
 */
@Entity
@Table(name = "dietas")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Dieta {
    /**
     * Identificador único del plan dietético.
     * <p>
     * Generado automáticamente por la base de datos.
     * </p>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nombre descriptivo del plan dietético.
     * <p>
     * Obligatorio y con longitud máxima de 80 caracteres.
     * </p>
     */
    @Column(name = "nombre", nullable = false, length = 80)
    private String nombre;

    @Column(name = "objetivo_calorico", nullable = true)
    private Integer objetivoCalorico;

    /**
     * Tipo de dieta o plan alimenticio.
     * <p>
     * Opcional, clasifica el plan (ej: keto, vegana, etc.).
     * </p>
     */
    @Column(name = "tipo", length = 50, nullable = true)
    private String tipo;

    /**
     * Cantidad de proteínas en gramos.
     * <p>
     * Opcional, información nutricional detallada.
     * </p>
     */
    @Column(name = "cantidad_p", nullable = true)
    private Integer cantidadProteinas;

    /**
     * Cantidad de carbohidratos en gramos.
     * <p>
     * Opcional, información nutricional detallada.
     * </p>
     */
    @Column(name = "cantidad_c", nullable = true)
    private Integer cantidadCarbohidratos;

    /**
     * Cantidad de grasas en gramos.
     * <p>
     * Opcional, información nutricional detallada.
     * </p>
     */
    @Column(name = "cantidad_g", nullable = true)
    private Integer cantidadGrasas;

    @Column
    private String descripcion;

   @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_dieta_id")
    @ToString.Exclude
    private CategoriaDieta categoriaDieta;

    /**
     * Lista de sugerencias asociadas a este plan dietético.
     * <p>
     * Relación uno-a-muchos con cascada completa para
     * gestionar las recomendaciones nutricionales.
     * </p>
     */
    @OneToMany(mappedBy = "dieta", cascade = CascadeType.ALL)
    private List<SugerenciaDieta> sugerencias;
}