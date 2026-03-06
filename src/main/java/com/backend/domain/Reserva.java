//Paquete
package com.backend.domain;
//Imports
import java.time.LocalDate;

import com.backend.domain.enums.EnumEstado;

import jakarta.persistence.*; //Importo todo el paquete
import lombok.*;


@Entity // Indica que esta clase es una entidad JPA
@Table(name = "reservas") // Nombre de la tabla en la base de datos
@Data // Lombok para generar getters, setters, toString, equals, y hashCode
@AllArgsConstructor // Lombok para generar un constructor con todos los argumentos
@NoArgsConstructor // Lombok para generar un constructor sin argumentos
@Builder // Lombok para generar un constructor con argumentos usando el patrón Builder
public class Reserva {
    
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;    
    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;
    @Enumerated(EnumType.STRING) //Indicamos que es un enum de tipo string
    @Column(name = "estado", nullable = false, length = 25)
    private EnumEstado estado;

    //@ManyToOne(fetch = FetchType.LAZY) //usamos un lazy para que traiga muchos registros para un usuario
    //@JoinColumn(name="usuario_id", referencedColumnName = "id")
    //private Usuario usuario;
    
    @ManyToOne(fetch = FetchType.LAZY) //usamos un lazy para que traiga muchos registros para un horario
    @JoinColumn(name="horario_id", referencedColumnName = "id")
    private Horario horario;
}
