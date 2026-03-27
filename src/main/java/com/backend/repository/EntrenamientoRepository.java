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
 
  /**
     * Recupera todos los entrenamientos asignados a un usuario específico.
     * <p>
     * Se utiliza en el Service para mostrar el historial completo del socio.
     * </p>
     *
     * @param usuarioId ID del usuario
     * @return Lista de entrenamientos ordenados por fecha descendente
     */
    List<Entrenamiento> findByUsuarioIdOrderByFechaDesc(Long usuarioId);

    /**
     * Busca entrenamientos en un rango de fechas para un usuario.
     * <p>
     * Ideal para la vista de "Calendario Semanal" en la App móvil.
     * </p>
     *
     * @param usuarioId ID del usuario
     * @param inicio Fecha de inicio del rango
     * @param fin Fecha de fin del rango
     * @return Lista de sesiones programadas en ese periodo
     */
    List<Entrenamiento> findByUsuarioIdAndFechaBetween(Long usuarioId, LocalDateTime inicio, LocalDateTime fin);

    /**
     * Cuenta cuántas sesiones de entrenamiento tiene programadas un usuario.
     * Útil para mostrar estadísticas en el perfil (ej: "Llevas 12 sesiones este mes").
     */
    long countByUsuarioId(Long usuarioId);

        List<Entrenamiento> findByUsuarioId( Long usuarioId);

    /**
     * Busca los entrenamientos más recientes de un usuario con un límite.
     * (Spring Data permite usar Top para limitar resultados).
     */
    List<Entrenamiento> findTop5ByUsuarioIdOrderByFechaDesc(Long usuarioId);
}
