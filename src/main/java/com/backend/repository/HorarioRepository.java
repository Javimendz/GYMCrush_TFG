//Imports
package com.backend.repository;
import java.util.List;

//Paquete de repositorio
import org.springframework.data.jpa.repository.JpaRepository;
import com.backend.domain.Horario;
import org.springframework.stereotype.Repository;
//Interfaz de repositorio para la entidad Horario
@Repository
public interface HorarioRepository extends JpaRepository<Horario, Long> {  
    List<Horario> findByActividadId(Long actividadId);
    List<Horario> findByDiaSemanaIgnoreCase(String diaSemana);
    List<Horario> findByActividadNombreContainingIgnoreCase(String nombre);
}
