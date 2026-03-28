package com.backend.domain;

import java.time.LocalDate;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entidad que representa un mensaje de chat del gimnasio.
 * <p>
 * Almacena comunicaciones entre usuarios y administradores,
 * incluyendo estado de lectura y timestamps de envío.
 * </p>
 *
 * @author Backend Team
 * @version 1.0
 * @since 2026
 */
@Entity
@Table(name = "chat")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Chat {

    /**
     * Identificador único del mensaje de chat.
     * <p>
     * Generado automáticamente por la base de datos.
     * </p>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Contenido del mensaje enviado.
     * <p>
     * Obligatorio, almacena el texto de la comunicación.
     * </p>
     */
    @Column(name = "mensaje", nullable = false)
    private String mensaje;

    /**
     * Fecha y hora de envío del mensaje.
     * <p>
     * Obligatorio, registra cuándo se envió el mensaje.
     * </p>
     */
    @Column(name = "fecha_hora", nullable = false)
    private LocalDate fechaHora;

    /**
     * Estado de lectura del mensaje.
     * <p>
     * Indica si el mensaje ha sido leído por el receptor.
     * </p>
     */
    @Column(name = "leido")
    private Boolean leido;

    /**
     * Usuario que envió el mensaje.
     * <p>
     * Relación muchos-a-uno con carga eager para obtener
     * información del usuario automáticamente.
     * </p>
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "usuarioId", referencedColumnName = "id", nullable = false, unique = true)
    @ToString.Exclude
    private Usuario usuario;
}
