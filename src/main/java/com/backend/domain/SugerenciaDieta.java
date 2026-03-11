<<<<<<< HEAD
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
=======
package com.backend.domain;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "sugerenciasDieta") 
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
>>>>>>> f4570e441899ef30c12df4e8042f8e8f12d93eac
public class SugerenciaDieta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

<<<<<<< HEAD
    @Column(name = "nombre_dieta", length = 80)
=======
    @Column(name = "nombre_dieta", length = 30)
>>>>>>> f4570e441899ef30c12df4e8042f8e8f12d93eac
    private String nombreDieta;

    @Column(name = "calorias_objetivo")
    private Integer caloriasObjetivo;

<<<<<<< HEAD
    @Column(name = "macronutrientes_json",columnDefinition = "TEXT") // Para JSON sin límites
    private String macronutrientesJson; 

    @Column(name = "tipo_dieta", length = 50 )
    private String tipoDieta;

    @Column(name = "fecha_generacion", nullable = false, updatable = false)
    private LocalDateTime fechaGeneracion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuarioId", referencedColumnName = "id")
    @ToString.Exclude // Para evitar la recursión infinita en el toString
=======

    @Column(name = "macronutrientes_json")
    private String macronutrientesJson; 

    @Column(name = "tipo_dieta", length = 50)
    private String tipoDieta;

    @Column(name = "fecha_generacion")
    private LocalDateTime fechaGeneracion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuarioId", referencedColumnName = "id", nullable = false)
>>>>>>> f4570e441899ef30c12df4e8042f8e8f12d93eac
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dieta_id", referencedColumnName = "id")
<<<<<<< HEAD
    @ToString.Exclude // Para evitar la recursión infinita en el toString
    private Dieta dieta;

    @PrePersist// Para que se ejecute antes de guardar en la base de datos
    protected void onCreate() {
        this.fechaGeneracion = LocalDateTime.now();
    }
=======
    private Dieta dieta;
>>>>>>> f4570e441899ef30c12df4e8042f8e8f12d93eac
}