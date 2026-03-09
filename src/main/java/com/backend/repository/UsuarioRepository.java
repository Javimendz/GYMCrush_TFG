//Paquete de repositorio
package com.backend.repository;
//Imports
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.backend.domain.Usuario;
//Interfaz de repositorio para la entidad Usuario
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    //Metodo personlizado para buscar si existe un usuario con el correo dado, se debe implementar aqui
    
    Boolean existsByCorreo(String correo);
}