//Imports
package com.backend.repository;
import java.util.List;

//Paquete
import org.springframework.data.jpa.repository.JpaRepository;
import com.backend.domain.Entrenamiento;

import org.springframework.stereotype.Repository;

@Repository
public interface EntrenamientoRepository extends JpaRepository<Entrenamiento, Long> {
    
    //Buscar entrenamientos por usuario
    List<Entrenamiento> findByUsuarioId(Long id);
    
}
