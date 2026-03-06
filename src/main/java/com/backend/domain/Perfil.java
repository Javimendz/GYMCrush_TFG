//Paquete
package com.backend.domain;
//Imports
import java.time.LocalDate;

import com.backend.domain.enums.EnumGenero;

import jakarta.persistence.*; // Importo todos los paquetes 
import lombok.*; //Importo todos los paquetes
// Indica que esta clase es una entidad JPA
@Entity
@Table(name = "perfiles") 
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
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
    private EnumGenero genero;

    @Column(name = "foto", length = 255, nullable = true)
    private String foto;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuarioId", referencedColumnName = "id", nullable = false, unique = true)
    @ToString.Exclude //Evita que exploten los logs de consola!!!!
    private Usuario usuario;
}