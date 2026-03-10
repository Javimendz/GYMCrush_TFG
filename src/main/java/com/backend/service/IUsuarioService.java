//Paquete
package com.backend.service;
//Imorts
import java.util.List;
import com.backend.domain.Usuario;
import com.backend.dto.UsuarioRequestDto;
//Interfaz para el servicio de la entidad Usuario
public interface IUsuarioService {
 

    Usuario save(Usuario usuario);

    List<Usuario> getAllUsuarios();

    //Boolean existsByCorreo(String correo);

    Usuario generarTokenAcceso(Long id);

    Usuario findById(Long id);

    void deleteById(Long id);

    void update(UsuarioRequestDto usuarioRequestDto, Long id);

}
