//Paquete de repositorio
package com.backend.repository;
import java.util.List;

//Imports
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.backend.domain.Actividad;
import org.springframework.stereotype.Repository;
//Interfaz de repositorio para la entidad Actividad
@Repository
public interface ActividadRepository extends JpaRepository<Actividad, Long> {
    
    // Buscar por nombre
    List<Actividad> findByNombreContainingIgnoreCase(String nombre);

    // Filtrar por precio máximo
    List<Actividad> findByPrecioLessThanEqual(Integer precio);

    // Buscar actividades por día de la semana
    // Nota: Asumimos que Actividad tiene una relación con Horario o un campo 'diaSemana'
    @Query("SELECT a FROM Actividad a JOIN a.horarios h WHERE h.diaSemana = :dia")
    List<Actividad> findActividadesDisponiblesPorDia(@Param("dia") String dia);

    //  Buscar por lugar
    List<Actividad> findByLugarIgnoreCase(String lugar);
}
