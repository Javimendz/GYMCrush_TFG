package com.backend.domain;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entidad que representa un tutorial educativo del gimnasio.
 * <p>
 * Almacena información sobre videos instructivos,
 * incluyendo título, descripción y categoría temática.
 * </p>
 *
 * @author Backend Team
 * @version 1.0
 * @since 2026
 */
@Entity
@Table(name = "tutoriales")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tutorial {
    /**
     * Identificador único del tutorial.
     * <p>
     * Generado automáticamente por la base de datos.
     * </p>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Título del tutorial.
     * <p>
     * Obligatorio, describe brevemente el contenido.
     * </p>
     */
    @Column(nullable = false)
    private String titulo;
    
    /**
     * Descripción detallada del tutorial.
     * <p>
     * Opcional, permite texto largo para explicar el contenido.
     * </p>
     */
    @Column(columnDefinition = "TEXT")
    private String descripcion;
    
    /**
     * URL del video del tutorial.
     * <p>
     * Opcional, enlace al video instructivo.
     * </p>
     */
    private String urlVideo;
    
    /**
     * Duración del tutorial en minutos.
     * <p>
     * Opcional, indica el tiempo total del video.
     * </p>
     */
    private Integer duracionMin;

    /**
     * Categoría temática a la que pertenece el tutorial.
     * <p>
     * Relación muchos-a-uno con carga eager para obtener
     * información completa de la categoría.
     * </p>
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "categoria_id")
    private CategoriaTutorial categoria;
}
