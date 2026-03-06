package com.backend.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "salud") // Nombre exacto en el script SQL [cite: 455]
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Salud {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "peso", precision = 5, scale = 2)
    private BigDecimal peso; 

    @Column(name = "estatura", precision = 4, scale = 2)
    private BigDecimal estatura;

    @Column(name = "nivel_actividad", length = 50)
    private String nivelActividad;

    @Column(name = "comentario", columnDefinition = "TEXT")
    private String comentario;

    @Column(name = "fecha_medicion", insertable = false, updatable = false)
    private LocalDateTime fechaMedicion; 

    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuarioId", referencedColumnName = "id", nullable = false)
    private Usuario usuario; 
}
