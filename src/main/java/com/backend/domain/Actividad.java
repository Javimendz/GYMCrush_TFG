//Paquete
package com.backend.domain;
//Imports
import jakarta.persistence.*; //Importo todo el paquete
import lombok.*;

@Entity // Indica que esta clase es una entidad JPA
@Table(name = "actividades") // Nombre de la tabla en la base de datos
@Data // Lombok para generar getters, setters, toString, equals, y hashCode
@AllArgsConstructor // Lombok para generar un constructor con todos los argumentos
@NoArgsConstructor // Lombok para generar un constructor sin argumentos
@Builder // Lombok para generar un constructor con argumentos usando el patrón Builder
public class Actividad {
     
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;   
    @Column(name = "nombre", nullable = false, length = 20)
    private String nombre; //Para el nombre de la actividad
}
