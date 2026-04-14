package com.backend.repository;

import com.backend.domain.Tutorial;
import com.backend.domain.Visualizacion;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad Visualizacion.
 * <p>
 * Gestiona el progreso de los usuarios en los videotutoriales.
 * Permite recuperar el estado de reproducción y filtrar por completados.
 * </p>
 *
 * @author Backend Team
 * @version 1.0
 */
@Repository
public interface VisualizacionRepository extends JpaRepository<Visualizacion, Long> {

    /**
     * Busca el registro de visualización de un usuario específico para un tutorial concreto.
     * <p>
     * Es vital para cuando el usuario hace clic en un vídeo: 
     * el sistema busca si ya lo empezó a ver para retomar el 'progresoSegundos'.
     * </p>
     */
    Optional<Visualizacion> findByUsuarioIdAndTutorialId(Long usuarioId, Long tutorialId);

    /**
     * Recupera todos los tutoriales que un usuario ha terminado de ver.
     * Útil para la sección "Mis Logros" o "Vistos".
     */
    List<Visualizacion> findByUsuarioIdAndCompletadoTrue(Long usuarioId);

    /**
     * Recupera los tutoriales que el usuario ha empezado pero no ha terminado.
     * Ideal para la sección "Seguir viendo" de la Home de la App.
     */
    List<Visualizacion> findByUsuarioIdAndCompletadoFalseOrderByFechaVisualizacionDesc(Long usuarioId);

    /**
     * Cuenta cuántos usuarios han visto (o están viendo) un tutorial específico.
     * Útil para estadísticas de popularidad.
     */
    long countByTutorialId(Long tutorialId);

    /**
 * Obtiene los tutoriales con más interacciones (visualizaciones).
 * Agrupa por tutorial y cuenta los registros en la tabla intermedia.
 */
@Query("SELECT v.tutorial FROM Visualizacion v " +
       "GROUP BY v.tutorial " +
       "ORDER BY COUNT(v) DESC")
List<Tutorial> findPopularTutorials(Pageable pageable);
}