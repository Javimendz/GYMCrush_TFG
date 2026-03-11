//Paquete de repositorio
package com.backend.repositories;
//Imports
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.backend.domain.Reserva;
//Interfaz de repositorio para la entidad Reserva
@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    
}
