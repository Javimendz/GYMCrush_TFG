package com.backend.repository;

import com.backend.domain.SugerenciaDieta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SugerenciaDietaRepository extends JpaRepository<SugerenciaDieta, Long> {
    // Obtener las dietas de un usuario específico ordenadas por la más reciente
    List<SugerenciaDieta> findByUsuarioIdOrderByFechaGeneracionDesc(Long usuarioId);
    void deleteByUsuarioId(Long usuarioId);

}
    
