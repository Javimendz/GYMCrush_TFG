package com.backend.domain;

import java.time.LocalDate;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "chat")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Chat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; 

    @Column(name = "mensaje", nullable = false)
    private String mensaje;

    @Column(name = "fecha_hora", nullable = false)
    private LocalDate fechaHora; 

    @Column(name = "leido")
    private Boolean leido; 

 
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuarioId", referencedColumnName = "id", nullable = false, unique = true)
    @ToString.Exclude
    private Usuario usuario;
}
