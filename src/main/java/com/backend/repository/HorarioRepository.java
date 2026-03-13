//Imports
package com.backend.repository;
//Paquete de repositorio
import org.springframework.data.jpa.repository.JpaRepository;
import com.backend.domain.Horario;
import org.springframework.stereotype.Repository;
//Interfaz de repositorio para la entidad Horario
@Repository
public interface HorarioRepository extends JpaRepository<Horario, Long> {  
    
}
