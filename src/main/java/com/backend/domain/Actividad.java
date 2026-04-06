package com.backend.domain;

import java.util.List;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Entidad que representa una actividad deportiva del gimnasio.
 * <p>
 * Almacena información sobre disciplinas deportivas ofrecidas,
 * incluyendo duración, precio, ubicación y horarios asociados.
 * </p>
 *
 * @author Backend Team
 * @version 1.0
 * @since 2026
 */
@Entity
@Table(name = "actividades")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@RequestMapping("/api/v1/actividades")
@CrossOrigin(origins = "*")
public class Actividad {
    /**
     * Identificador único de la actividad.
     * <p>
     * Generado automáticamente por la base de datos.
     * </p>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;   

    /**
     * Nombre de la actividad deportiva.
     * <p>
     * Obligatorio y con longitud máxima de 20 caracteres.
     * </p>
     */
    @Column(name = "nombre", nullable = false, length = 20)
    private String nombre;

    /**
     * Descripción detallada de la actividad.
     * <p>
     * Obligatorio y con longitud máxima de 100 caracteres.
     * </p>
     */
    @Column(name = "descripcion", nullable = false, length = 100)
    private String descripcion;

    /**
     * Duración de la actividad en minutos.
     * <p>
     * Obligatorio, representa el tiempo total de la sesión.
     * </p>
     */
    @Column(name = "duracion", nullable = false)
    private Integer duracion;

    /**
     * Ubicación donde se realiza la actividad.
     * <p>
     * Obligatorio y con longitud máxima de 50 caracteres.
     * </p>
     */
    @Column(name = "sala", nullable = false, length = 50)
    private String sala;

    /**
     * Precio de la actividad en la moneda local.
     * <p>
     * Obligatorio, representa el costo por sesión o mensualidad.
     * </p>
     */
    @Column(name = "precio", nullable = false)
    private Integer precio;

    /**
     * Lista de horarios asociados a esta actividad.
     * <p>
     * Relación uno-a-muchos con carga eager para obtener
     * los horarios automáticamente al cargar la actividad.
     * </p>
     */
    @OneToMany(mappedBy = "actividad", fetch = FetchType.EAGER)
    @ToString.Exclude
    private List<Horario> horarios;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id")
    private
    Categoria categoria;
}
