//Paquete
package com.backend.domain;
//Imports
import java.time.LocalDateTime;
import jakarta.persistence.*;//Importamos todo el paquete
import lombok.*; //Importamos todo el paquete

@Entity// Indica que esta clase es una entidad JPA
@Table(name = "sugerenciasDieta") // Nombre de la tabla en la base de datos
@Data// Lombok para generar getters, setters, toString, equals, y hashCode
@AllArgsConstructor// Lombok para generar un constructor con todos los argumentos
@NoArgsConstructor// Lombok para generar un constructor sin argumentos
@Builder// Lombok para generar un constructor con argumentos usando el patrón Builder
public class SugerenciaDieta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre_dieta", length = 80)
    private String nombreDieta;

    @Column(name = "calorias_objetivo")
    private Integer caloriasObjetivo;

    @Column(name = "macronutrientes_json",columnDefinition = "TEXT") // Para JSON sin límites
    private String macronutrientesJson; 

    @Column(name = "tipo_dieta", length = 50 )
    private String tipoDieta;

    @Column(name = "fecha_generacion", nullable = false, updatable = false)
    private LocalDateTime fechaGeneracion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuarioId", referencedColumnName = "id")
    @ToString.Exclude // Para evitar la recursión infinita en el toString
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dieta_id", referencedColumnName = "id")
    @ToString.Exclude // Para evitar la recursión infinita en el toString
    private Dieta dieta;

    @PrePersist// Para que se ejecute antes de guardar en la base de datos
    protected void onCreate() {
        this.fechaGeneracion = LocalDateTime.now();
    }
}