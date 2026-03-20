//Paquete
package com.backend.domain;
import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;

//Imports
import jakarta.persistence.*; //Importo todo el paquete
import lombok.*;

@Entity // Indica que esta clase es una entidad JPA
@Table(name = "actividades") // Nombre de la tabla en la base de datos
@Data // Lombok para generar getters, setters, toString, equals, y hashCode
@AllArgsConstructor // Lombok para generar un constructor con todos los argumentos
@NoArgsConstructor // Lombok para generar un constructor sin argumentos
@RequestMapping("/api/v1/actividades")
@Builder // Lombok para generar un constructor con argumentos usando el patrón Builder
@CrossOrigin(origins = "*") // Permite que Android se conecte sin bloqueos de CORS
public class Actividad {
     
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;   

    @Column(name = "nombre", nullable = false, length = 20)
    private String nombre; //Para el nombre de la actividad

    @Column(name = "descripcion", nullable = false, length = 100)
    private String descripcion; //Para la descripcion de la actividad

    @Column(name = "duracion", nullable = false)
    private Integer duracion; //Para la duracion de la actividad

    @Column(name = "lugar", nullable = false, length = 50)
    private String lugar; //Para el lugar de la actividad

    @Column(name = "precio", nullable = false)
    private Integer precio; //Para el precio de la actividad

    @OneToMany(mappedBy = "actividad", fetch = FetchType.LAZY)
    @ToString.Exclude // Importante para evitar errores de recursividad
    private List<Horario> horarios;
}