//Imports
package com.backend.repository;
import java.time.LocalDateTime;
import java.util.List;

//Paquete
import org.springframework.data.jpa.repository.JpaRepository;
import com.backend.domain.Entrenamiento;
import org.springframework.stereotype.Repository;

@Repository
public interface EntrenamientoRepository extends JpaRepository<Entrenamiento, Long> {
    
    List<Entrenamiento> findByUsuarioId(Long usuarioId);
    List<Entrenamiento> findByUsuarioAndFecha(LocalDateTime fecha);
    List<Entrenamiento> findByUsuarioIdAndIntensidad(Long usuarioId, String intensidad);
}
