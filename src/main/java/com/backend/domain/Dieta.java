//Paquete
package com.backend.domain;
//Imports
import java.util.List;

import jakarta.persistence.*; // Importo todos los paquetes 
import lombok.*; //Importo todos los paquetes
// Indica que esta clase es una entidad JPA

@Entity
@Table(name = "dietas") 
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Dieta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre", nullable = false, length = 80)
    private String nombre;

    @Column(name = "tipo", length = 50, nullable = true)
    private String tipo;

    @Column(name = "cantidad_p", nullable = true)
    private Integer cantidadProteinas;

    @Column(name = "cantidad_c", nullable = true)
    private Integer cantidadCarbohidratos; 

    @Column(name = "cantidad_g", nullable = true)
    private Integer cantidadGrasas; 

    @Column(name = "candidad", nullable = true)
    private Integer cantidadTotal;

    @Column(name = "categoria", length = 100, nullable = true)
    private String categoria;

    @OneToMany(mappedBy = "dieta", cascade = CascadeType.ALL)
    private List<SugerenciaDieta> sugerencias;
}