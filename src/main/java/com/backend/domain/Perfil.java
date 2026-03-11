//Paquete
package com.backend.domain;
//Imports
import java.time.LocalDate;
<<<<<<< HEAD
import com.backend.domain.enums.EnumGenero;
=======

import com.backend.domain.enums.EnumGenero;

>>>>>>> f4570e441899ef30c12df4e8042f8e8f12d93eac
import jakarta.persistence.*; // Importo todos los paquetes 
import lombok.*; //Importo todos los paquetes
// Indica que esta clase es una entidad JPA
@Entity
<<<<<<< HEAD
@Table(name = "perfiles") // Nombre de la tabla en la base de datos
@Data// Lombok para generar getters, setters, toString, equals, y hashCode
@AllArgsConstructor // Lombok para generar un constructor con todos los argumentos
@NoArgsConstructor // Lombok para generar un constructor sin argumentos
@Builder// Lombok para generar un constructor con argumentos usando el patrón Builder
=======
@Table(name = "perfiles") 
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
>>>>>>> f4570e441899ef30c12df4e8042f8e8f12d93eac
public class Perfil {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre", nullable = false, length = 80)
    private String nombre; 

    @Column(name = "apellidos", length = 150)
    private String apellidos;

    @Column(name = "telefono", length = 20, unique = true, nullable = false)
    private String telefono;

    @Column(name = "dni", unique = true, length = 20, nullable = false)
    private String dni;

    @Column(name = "fecha_nacimiento", nullable = false)
    private LocalDate fechaNacimiento;

    @Column(name = "genero", length = 50, nullable = false)
<<<<<<< HEAD
    @Enumerated(EnumType.STRING) //Indicamos que es un enum de tipo string
=======
>>>>>>> f4570e441899ef30c12df4e8042f8e8f12d93eac
    private EnumGenero genero;

    @Column(name = "foto", length = 255, nullable = true)
    private String foto;

    @OneToOne(fetch = FetchType.LAZY)
<<<<<<< HEAD
    @JoinColumn(name = "usuarioId", referencedColumnName = "id")
=======
    @JoinColumn(name = "usuarioId", referencedColumnName = "id", nullable = false, unique = true)
>>>>>>> f4570e441899ef30c12df4e8042f8e8f12d93eac
    @ToString.Exclude //Evita que exploten los logs de consola!!!!
    private Usuario usuario;
}