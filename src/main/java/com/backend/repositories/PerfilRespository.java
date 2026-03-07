package com.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.backend.domain.Perfil;

@Repository
public interface PerfilRespository extends JpaRepository<Perfil, Long> {
    
}
