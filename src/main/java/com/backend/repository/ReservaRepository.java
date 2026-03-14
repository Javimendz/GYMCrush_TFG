package com.backend.repository;

import com.backend.domain.Reserva;
import com.backend.domain.enums.EnumEstado;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    // Listar reservas de un alumno específico
    List<Reserva> findByUsuarioId(Long usuarioId);
    // Listar reservas de un usuario que no estén canceladas
    List<Reserva> findByUsuarioIdAndEstadoNot (Long usuarioId, EnumEstado estado);
    
    // Contar cuántas personas hay en una clase un día concreto
    //long countByHorarioIdAndFecha(Long horarioId, LocalDate fecha);
    
    // 2. Control de Aforo: Cuenta solo los que ocupan plaza real
    // (CONFIRMADA para los que van a ir, ASISTIDA para los que ya están allí)
    long countByHorarioIdAndFechaAndEstadoIn(Long horarioId, LocalDate fecha, List<EnumEstado> estados);
    
    // Evitar que el mismo usuario reserve la misma clase dos veces el mismo día
    boolean existsByUsuarioIdAndHorarioIdAndFecha(Long usuarioId, Long horarioId, LocalDate fecha);
    
}
