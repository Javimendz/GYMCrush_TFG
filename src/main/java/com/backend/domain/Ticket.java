//Paquete
package com.backend.domain;
//Imports
import java.time.LocalDateTime;
import com.backend.domain.enums.EnumTicketEstado;
import jakarta.persistence.*; //Importar todos los paquetes
import lombok.*; //Importar todos los paquetes de Lombok

@Entity // Indica que esta clase es una entidad JPA
@Table(name = "tickets") // Nombre de la tabla en la base de datos
@Data //Lombok para generar getters, setters, toString, equals, y hashCode
@AllArgsConstructor // Lombok para generar un constructor con todos los argumentos
@NoArgsConstructor // Lombok para generar un constructor sin argumentos
@Builder // Lombok para generar un constructor con argumentos usando el patrón Builder
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "asunto", nullable = true, length = 150)
    private String asunto;

    @Column(name = "mensaje", nullable = false)
    private String mensaje;

    @Column(name = "estado", length = 50, nullable = false)
    @Enumerated(EnumType.STRING) //Indicamos que es un enum de tipo string
    private EnumTicketEstado estado;

    @Column(name = "fecha_resolucion", nullable = true)
    private LocalDateTime fechaResolucion;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion; 


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuarioId", referencedColumnName = "id")
    @ToString.Exclude
    private Usuario usuario;


    @PrePersist //Automatizacion de fecha con la fecha actual
    public void onCreate() {
        this.fechaCreacion = LocalDateTime.now();
        if(this.estado == null) {
            this.estado = EnumTicketEstado.ABIERTO; //Estado por defecto
        }
    }
}
