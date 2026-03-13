package com.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.backend.domain.Perfil;
import java.util.Optional;

@Repository
public interface PerfilRespository extends JpaRepository<Perfil, Long> {
    // Busca el perfil usando la FK usuariold definida en el PDF 
    Optional<Perfil> findByUsuarioId(Long usuarioId);
}
