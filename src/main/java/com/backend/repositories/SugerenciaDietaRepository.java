//Paquete
package com.backend.repositories;
//Imports
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.backend.domain.SugerenciaDieta;

@Repository
public interface SugerenciaDietaRepository extends JpaRepository <SugerenciaDieta, Long>{

}
    
