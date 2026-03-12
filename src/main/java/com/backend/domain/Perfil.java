//Paquete
package com.backend.domain;
//Imports
import java.time.LocalDate;
import com.backend.domain.enums.EnumGenero;
import jakarta.persistence.*; // Importo todos los paquetes 
import lombok.*; //Importo todos los paquetes
// Indica que esta clase es una entidad JPA
@Entity
@Table(name = "perfiles") // Nombre de la tabla en la base de datos
@Getter 
@Setter
@AllArgsConstructor // Lombok para generar un constructor con todos los argumentos
@NoArgsConstructor // Lombok para generar un constructor sin argumentos
@Builder// Lombok para generar un constructor con argumentos usando el patrón Builder
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

    @Column(name= "direccion", nullable = false)
    private String direccion;
    @Column(name="ciudad", nullable= false)
    private String ciudad;
    @Column(name="pais", nullable=false)
    private String pais;
    @Column(name="codigo_postal", nullable=false)
    private String codigoPostal;

    @Column(columnDefinition = "TEXT") // Para descripciones largas
    private String bio;

    @Column(name = "genero", length = 50, nullable = false)
    @Enumerated(EnumType.STRING) //Indicamos que es un enum de tipo string
    private EnumGenero genero;

    @Column(name = "foto", length = 255, nullable = true)
    private String foto;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", referencedColumnName = "id")
    @ToString.Exclude //Evita que exploten los logs de consola!!!!
    @EqualsAndHashCode.Exclude // Evita errores de hashcode
    private Usuario usuario;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Perfil perfil)) return false;
        return getId() != null && getId().equals(perfil.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

}