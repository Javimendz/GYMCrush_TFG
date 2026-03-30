package com.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.domain.Nutricion;


public interface NutricionRepository extends JpaRepository<Nutricion, Long>{
    
    public Optional<Nutricion> findFirstByUsuarioIdOrderByIdDesc(Long usuarioId);

    public   Optional<Nutricion> findById(Long id);
}
