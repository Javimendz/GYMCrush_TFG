package com.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.backend.domain.Tutorial;

@Repository
public interface TutorialRepository extends JpaRepository<Tutorial, Long> {    
        // Buscar todos los tutoriales de una categoría específica, ej "Yoga"
    List<Tutorial> findByCategoriaId(Long categoriaId);
    
    // Buscar por título, por si se añade un buscador en Android
    List<Tutorial> findByTituloContainingIgnoreCase(String titulo);
}
