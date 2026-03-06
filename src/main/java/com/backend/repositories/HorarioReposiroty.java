//Imports
package com.backend.repositories;
//Paquete de repositorio
import org.springframework.data.jpa.repository.JpaRepository;
import com.backend.domain.Horario;
import org.springframework.stereotype.Repository;
//Interfaz de repositorio para la entidad Horario
@Repository
public interface HorarioReposiroty extends JpaRepository<Horario, Long> {  
    
}
