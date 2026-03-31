package com.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.backend.domain.Notificacion;





public interface NotificacionRepository extends JpaRepository<Notificacion, Long> { /**
     * Obtiene todo el historial de notificaciones de un usuario específico.
     * Se ordena por fecha de creación de forma descendente (las más nuevas primero).
     *
     * @param usuarioId ID del usuario
     * @return Lista de notificaciones
     */
    List<Notificacion> findByUsuarioIdOrderByFechaCreacionDesc(Long usuarioId);

    /**
     * Obtiene SOLO las notificaciones que el usuario aún no ha leído.
     * Ideal para mostrar un filtro de "Nuevas".
     *
     * @param usuarioId ID del usuario
     * @return Lista de notificaciones no leídas
     */
    List<Notificacion> findByUsuarioIdAndLeidoFalseOrderByFechaCreacionDesc(Long usuarioId);

    /**
     * Cuenta cuántas notificaciones sin leer tiene el usuario.
     * Perfecto para mostrar el "globito rojo" (badge) con el número en el ícono de la app.
     *
     * @param usuarioId ID del usuario
     * @return Número de notificaciones sin leer
     */
    long countByUsuarioIdAndLeidoFalse(Long usuarioId);

    /**
     * Utilidad para marcar de golpe todas las notificaciones de un usuario como leídas.
     * Muy útil si en la app tienes un botón de "Marcar todo como leído".
     *
     * @param usuarioId ID del usuario
     */
    @Modifying
    @Query("UPDATE Notificacion n SET n.leido = true WHERE n.usuario.id = :usuarioId AND n.leido = false")
    void marcarTodasComoLeidasPorUsuarioId(@Param("usuarioId") Long usuarioId);
}