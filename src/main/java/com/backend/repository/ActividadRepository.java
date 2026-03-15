package com.backend.repository;

import com.backend.domain.Actividad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ActividadRepository extends JpaRepository<Actividad, Long> {

    // 1. Buscador para la App (Android)
    // Permite que el usuario busque "Yoga" o "Zumba" y lo encuentre aunque escriba en minúsculas
    List<Actividad> findByNombreContainingIgnoreCase(String nombre);

    // 2. Filtro por Lugar (Gestión de Salas)
    // Útil si el gimnasio quiere ver qué actividades se dan en la "Sala 1" o "Piscina"
    List<Actividad> findByLugar(String lugar);

    // 3. Filtro por Precio (Para el "Servicio de Calidad")
    // Permite mostrar, por ejemplo, solo las actividades gratuitas o incluidas en la cuota (precio = 0)
    List<Actividad> findByPrecioLessThanEqual(Integer precioMax);

    // 4. Búsqueda por Duración
    // Para usuarios que buscan clases rápidas (ej. menos de 45 min)
    List<Actividad> findByDuracionLessThanEqual(Integer minutos);

    // 5. Query personalizada: Buscar actividades que tienen horarios hoy
    // Esto es avanzado y muy útil para la pantalla principal de Android
    @Query("SELECT DISTINCT a FROM Actividad a JOIN a.horarios h WHERE h.diaSemana = :dia")
    List<Actividad> findActividadesDisponiblesPorDia(@Param("dia") String dia);
}
