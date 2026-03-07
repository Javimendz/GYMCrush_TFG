package com.backend.domain;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tickets") 
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "asunto", nullable = false, length = 150)
    private String asunto;

    @Column(name = "mensaje", nullable = false)
    private String mensaje;

    @Column(name = "estado", length = 50)
    private String estado;

    @Column(name = "fecha_resolucion", nullable = false)
    private LocalDateTime fechaResolucion;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion; 


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuarioId", referencedColumnName = "id", nullable = false, unique = true)
    @ToString.Exclude
    private Usuario usuario;
}
