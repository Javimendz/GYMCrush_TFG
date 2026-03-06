//Paquete de repositorio
package com.backend.repositories;
//Imports
import org.springframework.data.jpa.repository.JpaRepository;
import com.backend.domain.Actividad;
import org.springframework.stereotype.Repository;
//Interfaz de repositorio para la entidad Actividad
@Repository
public interface ActividadRepository extends JpaRepository<Actividad, Long> {
    
}
