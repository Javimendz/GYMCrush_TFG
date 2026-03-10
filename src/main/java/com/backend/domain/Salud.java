//Paquete
package com.backend.domain;
//Imports
import java.math.BigDecimal;
import java.time.LocalDateTime;
import jakarta.persistence.*; // Para usar JPA, importamos todo el paquete
import lombok.*; // Para usar Lombok, importamos todo el paquete

@Entity// Indica que esta clase es una entidad JPA
@Table(name = "salud") // Nombre de la tabla en la base de datos
@Data// Lombok para generar getters, setters, toString, equals, y hashCode
@AllArgsConstructor// Lombok para generar un constructor con todos los argumentos
@NoArgsConstructor// Lombok para generar un constructor sin argumentos
@Builder// Lombok para generar un constructor con argumentos usando el patrón Builder
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

    @Column(name = "comentario", columnDefinition = "TEXT") // Nota detallada extendida
    private String comentario;

    @Column(name = "fecha_medicion", nullable = false, updatable = false)
    private LocalDateTime fechaMedicion; 

    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuarioId", referencedColumnName = "id", nullable = false)
    @ToString.Exclude // Para evitar la recursión infinita en el toString
    private Usuario usuario; 

   @PrePersist
    protected void onCreate() {
        if (this.fechaMedicion == null) {
            this.fechaMedicion = LocalDateTime.now();
        }
    }
}
