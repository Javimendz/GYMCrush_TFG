//Paquete
package com.backend.domain;

//Imports
import java.time.LocalDateTime;
import jakarta.persistence.*;//Importar todo el paquete
import lombok.*;

@Entity
@Table(name = "entrenamientos")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Entrenamiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fecha", nullable = true)
    private LocalDateTime fecha;

    @Column(name = "hora", nullable = true)
    private LocalDateTime hora;

    @Column(name = "duracion", nullable = true)
    private LocalDateTime duracion;

    @Column(name = "intensidad",length = 150)
    private String intensidad;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuarioId", referencedColumnName = "id", nullable = false, unique = true)
    @ToString.Exclude //Evita que exploten los logs de consola!!!!
    private Usuario usuario;

}
