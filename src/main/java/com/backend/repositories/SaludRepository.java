//Paquete
package com.backend.repositories;
//Imports
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.backend.domain.Salud;

@Repository
public interface SaludRepository extends JpaRepository<Salud, Long>{
    
}
