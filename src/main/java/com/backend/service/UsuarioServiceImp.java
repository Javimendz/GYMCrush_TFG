//Paquete
package com.backend.service;

import java.time.LocalDateTime;
//Imports
import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.domain.Usuario;
import com.backend.dto.UsuarioRequestDto;
import com.backend.exceptions.ResourceNotFoundException;
import com.backend.mapper.UsuarioMapper;
import com.backend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementación del servicio para la gestión de usuarios del sistema.
 * <p>
 * Esta clase maneja las operaciones CRUD para usuarios, incluyendo
 * la generación de tokens QR para acceso físico al gimnasio.
 * </p>
 *
 * @author Backend Team
 * @version 1.0
 * @since 2026
 */
@RequiredArgsConstructor
@Slf4j
@Transactional
@Service
public class UsuarioServiceImp implements IUsuarioService {

    /** Repositorio para el acceso a datos de usuarios */
    private final UsuarioRepository usuarioRepo;
    
    /** Mapper para convertir entre entidades y DTOs de usuarios */
    private final UsuarioMapper usuarioMapper;
    
    /** Codificador de contraseñas para seguridad */
    private final PasswordEncoder passwordEncoder;

    /**
     * Guarda un nuevo usuario en la base de datos.
     * <p>
     * Codifica la contraseña antes de guardarla por seguridad.
     * </p>
     *
     * @param usuario Entidad Usuario con todos los datos a guardar
     * @return Entidad Usuario guardada con ID asignado
     */
    @Override
    public Usuario save(Usuario usuario) {
        log.info("Guardando usuario: {}", usuario.getUsername());
        return usuarioRepo.save(usuario);
    }

    /**
     * Genera un token QR temporal para acceso físico al gimnasio.
     * <p>
     * Crea un UUID único como token con validez de 30 segundos,
     * útil para control de acceso mediante código QR en la entrada.
     * </p>
     *
     * @param id ID del usuario que solicita el token de acceso
     * @return Entidad Usuario actualizada con el token QR generado
     * @throws ResourceNotFoundException si el usuario no existe
     */
    @Override
    public Usuario generarTokenAcceso(Long id) {
        log.info("Generando token de acceso para usuario ID: {}", id);
        
        // Buscar al usuario o lanzar excepción si no existe
        Usuario usuario = usuarioRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));
        
        // Generar token UUID único y establecer expiración
        usuario.setQrToken(java.util.UUID.randomUUID().toString());
        usuario.setTokenExpiraEn(LocalDateTime.now().plusSeconds(30));
        
        log.debug("Token generado para usuario {} expira en: {}", usuario.getUsername(), usuario.getTokenExpiraEn());
        
        // Guardar el usuario con el token actualizado
        return usuarioRepo.save(usuario);
    }
    /**
     * Obtiene todos los usuarios del sistema.
     * <p>
     * Retorna una lista completa de todos los usuarios registrados.
     * </p>
     *
     * @return Lista de todos los usuarios
     */
    @Override
    public List<Usuario> getAllUsuarios() {
        log.info("Obteniendo todos los usuarios");
        List<Usuario> usuarios = usuarioRepo.findAll();
        log.debug("Total de usuarios encontrados: {}", usuarios.size());
        return usuarios;
    }
    /**
     * Busca un usuario por su ID.
     * <p>
     * Retorna el usuario con el ID especificado o lanza una excepción si no existe.
     * </p>
     *
     * @param id ID del usuario a buscar
     * @return Usuario encontrado
     * @throws ResourceNotFoundException si el usuario no existe
     */
    @Override
    public Usuario findById(Long id) {
        log.info("Buscando usuario por ID: {}", id);
        Usuario usuario = usuarioRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));
        log.debug("Usuario encontrado: {}", usuario.getUsername());
        return usuario;
    }
    /**
     * Elimina un usuario por su ID.
     * <p>
     * Elimina el usuario con el ID especificado o lanza una excepción si no existe.
     * </p>
     *
     * @param id ID del usuario a eliminar
     * @throws ResourceNotFoundException si el usuario no existe
     */
    @Override
    public void deleteById(Long id) {
        log.info("Eliminando usuario con ID: {}", id);
        Usuario usuario = this.findById(id);
        usuarioRepo.delete(usuario);
        log.info("Usuario {} eliminado exitosamente", usuario.getUsername());
    }
    /**
     * Valida un token QR para un usuario.
     * <p>
     * Valida el token QR para el usuario con el ID especificado.
     * </p>
     *
     * @param token Token QR a validar
     * @param userId ID del usuario a validar
     * @return true si el token es válido, false en caso contrario
     */
    public boolean validarTokenQR(String token, Long userId) {
        log.info("Validando token QR para usuario ID: {}", userId);
        Usuario usuario = findById(userId);
        if (usuario.getQrToken() == null || usuario.getTokenExpiraEn() == null) {
            log.warn("No hay token generado para usuario ID: {}", userId);
            return false; // No hay token generado
        }
        boolean valido = token.equals(usuario.getQrToken()) &&
                LocalDateTime.now().isBefore(usuario.getTokenExpiraEn());
        log.debug("Token QR {} para usuario {}", valido ? "válido" : "inválido", userId);
        return valido;
    }
    /**
     * Actualiza un usuario existente.
     * <p>
     * Actualiza el usuario con el ID especificado.
     * </p>
     *
     * @param usuarioRequestDto Datos del usuario a actualizar
     * @param id ID del usuario a actualizar
     */
    @Override
    public void update(UsuarioRequestDto usuarioRequestDto, Long id) {
        log.info("Actualizando usuario con ID: {}", id);
        Usuario existente = this.findById(id);

        if (usuarioRequestDto.getPassword() != null && !usuarioRequestDto.getPassword().isEmpty()) {
            log.debug("Actualizando contraseña para usuario: {}", existente.getUsername());
            existente.setPassword(passwordEncoder.encode(usuarioRequestDto.getPassword()));
        } // Mapeamos los cambios del DTO al objeto

        usuarioMapper.updateUsuarioFromDto(usuarioRequestDto, existente);
        // Guardamos los cambios
        usuarioRepo.save(existente);
        log.info("Usuario {} actualizado exitosamente", existente.getUsername());
    }
}
