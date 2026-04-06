//Paquete de repositorio
package com.backend.repository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

//Imports
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.backend.domain.Reserva;
import com.backend.domain.enums.EnumEstado;
//Interfaz de repositorio para la entidad Reserva
@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {
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
<<<<<<< HEAD

    // Busca reservas por ID de horario, fecha específica y una lista de estados (ej: CONFIRMADA, ASISTIDA)
    List<Reserva> findByHorarioIdAndFechaAndEstadoIn(Long horarioId, LocalDate fecha, List<EnumEstado> estados);

    List<Reserva> findReservasParaRecordatorio(LocalDateTime ahora, LocalDateTime limite);    


    @Query("SELECT r FROM Reserva r WHERE r.fecha = :hoy AND r.horario.horaInicio BETWEEN :ahora AND :limite AND r.estado = 'CONFIRMADA'")
    List<Reserva> findReservasParaRecordatorio(
        @Param("hoy") java.time.LocalDate hoy, 
        @Param("ahora") java.time.LocalTime ahora, 
        @Param("limite") java.time.LocalTime limite
    );
=======
    List<Reserva> findByHorarioIdAndEstado(Long horarioId, EnumEstado estado);
>>>>>>> origin/andrearama
}
