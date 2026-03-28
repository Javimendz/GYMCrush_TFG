package com.backend.repository;

import com.backend.domain.Rutina;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface RutinaRepository extends JpaRepository<Rutina, Long> {

    /**
     * Busca todas las rutinas asignadas a un usuario para un día concreto.
     * Es la consulta principal para la pantalla "Mi Rutina de Hoy" en la App.
     */
    List<Rutina> findByUsuarioIdAndFechaAsignacionOrderByOrdenAsc(Long usuarioId, LocalDate fecha);

    /**
     * Busca rutinas por entrenamiento.  para ver qué usuarios 
     * tienen asignado un ejercicio específico.
     */
    List<Rutina> findByEntrenamientoId(Long entrenamientoId);

    /**
     * Busca las rutinas pendientes (no completadas) de un usuario.
     */
    List<Rutina> findByUsuarioIdAndCompletadoFalse(Long usuarioId);
}