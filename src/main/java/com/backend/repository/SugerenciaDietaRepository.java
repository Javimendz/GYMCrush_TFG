//Paquete
package com.backend.repository;
import java.util.List;
import java.util.Optional;

//Imports
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.backend.domain.SugerenciaDieta;

@Repository
public interface SugerenciaDietaRepository extends JpaRepository <SugerenciaDieta, Long>{


    List<SugerenciaDieta> findByUsuarioIdOrderByFechaGeneracionDesc(Long usuarioId);
    void deleteByUsuarioId(Long usuarioId);
    Optional<SugerenciaDieta> findFirstByUsuarioIdOrderByIdDesc(Long usuarioId);
}
    
