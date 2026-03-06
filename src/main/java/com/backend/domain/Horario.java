//Paquete
package com.backend.domain;
//Imports
import java.time.LocalTime;


import jakarta.persistence.*; //Importo todo el paquete
import lombok.*;


@Entity // Indica que esta clase es una entidad JPA
@Table(name = "horarios") // Nombre de la tabla en la base de datos
@Data // Lombok para generar getters, setters, toString, equals, y hashCode
@AllArgsConstructor // Lombok para generar un constructor con todos los argumentos
@NoArgsConstructor // Lombok para generar un constructor sin argumentos
@Builder // Lombok para generar un constructor con argumentos usando el patrón Builder
public class Horario {
    
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;   
    @Column(name = "dia_semana", nullable = false, length = 20)
    private String diaSemana; //Para el dia de la semana
    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio;
    @Column(name = "hora_fin", nullable = false)
    private LocalTime horaFin;
    @Column(name = "aforo_max", nullable = false)
    private Integer aforoMax;
    
    @ManyToOne(fetch = FetchType.LAZY)   //usamos un lazy para que traiga muchos registros para una actividad
    @JoinColumn(name="actividad_id", referencedColumnName = "id")
    private Actividad actividad;
}
