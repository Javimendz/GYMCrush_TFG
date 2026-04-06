package com.backend.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.backend.domain.ComidaDiaria;

public interface ComidaDiariaRepository  extends JpaRepository<ComidaDiaria, Long> {
    
/**
     * Busca el último plan generado para un usuario.
     * Spring Data JPA interpreta el nombre: 
     * findFirst (el primero) + ByUsuarioId (filtrado por ID de usuario) + OrderByIdDesc (ordenado de más nuevo a más viejo)
     */
    
   List<ComidaDiaria> findByPlanId(Long planId);

  //List<ComidaDiaria> findByPlanNutricionalId(Long planId);
}
