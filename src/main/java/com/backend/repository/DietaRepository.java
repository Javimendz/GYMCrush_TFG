package com.backend.repository;

import com.backend.domain.Dieta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DietaRepository extends JpaRepository<Dieta, Long> {
    
    // IMPORTANTE: Busca dietas filtrando por el nombre de su categoría asociada
    // Se usa para mostrar al usuario solo las dietas que encajan con su perfil
    List<Dieta> findByCategoriaDietaNombre(String nombreCategoria);
    
    // Para validaciones de integridad
    boolean existsByNombre(String nombre);
}
