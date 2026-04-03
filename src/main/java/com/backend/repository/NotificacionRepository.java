package com.backend.repository;

import com.backend.domain.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {

    /**
     * Obtiene todas las notificaciones de un usuario específico.
     * Ordenadas por fecha de creación descendente (las más nuevas primero).
     */
    List<Notificacion> findByUsuarioIdOrderByFechaCreacionDesc(Long usuarioId);

    /**
     * Cuenta cuántas notificaciones tiene un usuario que aún no han sido leídas.
     * Útil para mostrar el "punto rojo" o contador en la App de Android.
     */
    long countByUsuarioIdAndLeidoFalse(Long usuarioId);

    /**
     * Obtiene solo las notificaciones no leídas de un usuario.
     */
    List<Notificacion> findByUsuarioIdAndLeidoFalse(Long usuarioId);
}