//Paquete de repositorio
package com.backend.repositories;
//Imports
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.backend.domain.Dieta;

@Repository
public interface DietaRepository extends JpaRepository<Dieta, Long> {

  
} 
