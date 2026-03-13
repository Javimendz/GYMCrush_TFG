package com.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.backend.domain.Perfil;
import java.util.Optional;

@Repository
public interface PerfilRepository extends JpaRepository<Perfil, Long> {
    // Crucial: busca el perfil vinculado al usuario que está logueado
    Optional<Perfil> findByUsuarioId(Long usuarioId);
}