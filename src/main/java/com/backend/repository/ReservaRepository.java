package com.backend.repository;

import com.backend.domain.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    // Listar reservas de un alumno específico
    List<Reserva> findByUsuarioId(Long usuarioId);
    
    // Contar cuántas personas hay en una clase un día concreto
    long countByHorarioIdAndFecha(Long horarioId, LocalDate fecha);
    
    // Evitar que el mismo usuario reserve la misma clase dos veces el mismo día
    boolean existsByUsuarioIdAndHorarioIdAndFecha(Long usuarioId, Long horarioId, LocalDate fecha);
}
