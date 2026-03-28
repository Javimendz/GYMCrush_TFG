//Paquete de repositorio
package com.backend.repository;
import java.util.List;

//Imports
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.backend.domain.Dieta;

@Repository
public interface DietaRepository extends JpaRepository<Dieta, Long> {
    
    // Crucial para el motor: busca dietas que encajen con el objetivo del socio
    List<Dieta> findByCategoria(String categoria);
    
    // Para validaciones
    boolean existsByNombre(String nombre);
}
