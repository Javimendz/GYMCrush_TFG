//Imports
package com.backend.repository;

//Paquete
import org.springframework.data.jpa.repository.JpaRepository;
import com.backend.domain.Entrenamiento;

import org.springframework.stereotype.Repository;

@Repository
public interface EntrenamientoRepository extends JpaRepository<Entrenamiento, Long> {
 

}
