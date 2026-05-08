package com.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.domain.Perfil;
import com.backend.dto.PerfilRequestDto;
import com.backend.dto.PerfilResponseDto;
import com.backend.exceptions.ResourceNotFoundException;
import com.backend.mapper.PerfilMapper;
import com.backend.repository.PerfilRepository;
import com.backend.repository.SaludRepository;
import com.backend.domain.Usuario;
import com.backend.domain.enums.EnumRol;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementación del servicio para la gestión de perfiles de usuario.
 * <p>
 * Esta clase maneja las operaciones CRUD para perfiles, incluyendo
 * la consulta completa con datos de salud más recientes.
 * </p>
 *
 * @author Backend Team
 * @version 1.0
 * @since 2026
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class PerfilServiceImp implements IPerfilService {

    private final PerfilRepository perfilRepository;
    private final SaludRepository saludRepository;
    private final PerfilMapper perfilMapper;

    @Override
    @Transactional(readOnly = true)
    public List<PerfilResponseDto> listarEntrenadores() {
        log.info("Buscando todos los usuarios con rol ENTRENADOR");

        List<Perfil> entrenadores = perfilRepository.findAll().stream()
                .filter(p -> p.getUsuario().getRoles().stream()
                        .anyMatch(rol -> rol.getName().equals("ROLE_ENTRENADOR")))
                .toList(); //

        
        return entrenadores.stream()
                .map(perfil -> {
                    PerfilResponseDto dto = perfilMapper.toDto(perfil);
                    dto.setUsuarioId(perfil.getUsuario().getId()); // Aseguramos el ID para el Spinner
                    return dto;
                })
                .toList(); //
    }

    /**
     * Obtiene todos los perfiles de usuarios del sistema.
     * <p>
     * Retorna una lista completa de todos los perfiles incluyendo
     * los datos de salud más recientes de cada usuario.
     * </p>
     *
     * @return Lista de DTOs con todos los perfiles enriquecidos
     */
    @Override
    @Transactional(readOnly = true)
    public List<PerfilResponseDto> findAll() {
        log.info("Obteniendo todos los perfiles");
        List<Perfil> perfiles = perfilRepository.findAll();

        return perfiles.stream().map(perfil -> {
            // Convertimos a DTO
            PerfilResponseDto dto = perfilMapper.toDto(perfil);

            // Enriquecemos con salud
            saludRepository.findFirstByUsuarioOrderByFechaMedicionDesc(perfil.getUsuario())
                    .ifPresent(salud -> {
                        dto.setPeso(salud.getPeso());
                        dto.setEstatura(salud.getEstatura());
                        dto.setImc(salud.getImc());
                        dto.setNivelActividad(salud.getNivelActividad());
                    });
            return dto;
        }).toList();
    }

    /**
     * Guarda un nuevo perfil de usuario.
     * <p>
     * Crea un nuevo perfil en la base de datos.
     * </p>
     *
     * @param perfil El perfil a guardar
     * @return El perfil guardado
     */
    @Override
    @Transactional
    public Perfil save(Perfil perfil) {

        return perfilRepository.save(perfil);
    }

    @Override
    @Transactional(readOnly = true)
    public PerfilResponseDto findById(Long id) {

        Perfil perfil = perfilRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Perfil no encontrado"));

        PerfilResponseDto dto = perfilMapper.toDto(perfil);

        if (perfil.getUsuario() != null) {
            dto.setCorreo(perfil.getUsuario().getEmail()); // Asegúrate de que PerfilResponseDto tenga setCorreo
            // Si el nombre del perfil está vacío, usa el username como fallback
            if (dto.getNombre() == null || dto.getNombre().isEmpty()) {
                dto.setNombre(perfil.getUsuario().getUsername());
            }
        }
        saludRepository.findFirstByUsuarioOrderByFechaMedicionDesc(perfil.getUsuario())
                .ifPresent(salud -> {
                    dto.setPeso(salud.getPeso());
                    dto.setEstatura(salud.getEstatura());
                    dto.setImc(salud.getImc());
                    dto.setNivelActividad(salud.getNivelActividad());
                });

        return dto;
    }

    /**
     * Elimina un perfil por su ID.
     * <p>
     * Elimina un perfil existente de la base de datos.
     * </p>
     *
     * @param id El ID del perfil a eliminar
     */
    @Override
    @Transactional
    public void deleteById(Long id) {

        if (!perfilRepository.existsById(id)) {
            throw new ResourceNotFoundException("Perfil no encontrado");
        }
        perfilRepository.deleteById(id);
    }

    /**
     * Actualiza un perfil existente.
     * <p>
     * Actualiza los datos de un perfil existente en la base de datos.
     * </p>
     *
     * @param id  El ID del perfil a actualizar
     * @param dto Los datos actualizados del perfil
     * @return El perfil actualizado
     */
@Override
@Transactional
public PerfilResponseDto update(Long id, PerfilRequestDto dto) {
    // Buscamos el perfil actual
    Perfil perfil = perfilRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Perfil no encontrado"));

    String dniOriginal = perfil.getDni();
    log.info("Actualizando Perfil {}. DNI original: {}, DNI recibido: {}", id, dniOriginal, dto.getDni());

    if (dto.getDni() != null && !dto.getDni().trim().isEmpty() && !dto.getDni().equalsIgnoreCase(dniOriginal)) {
        // Quiere cambiar el DNI a uno nuevo. Comprobamos si existe ANTES de mapear.
        if (perfilRepository.existsByDni(dto.getDni())) {
            throw new RuntimeException("El DNI " + dto.getDni() + " ya pertenece a otro usuario.");
        }
    } else {
        // Si el DNI viene null, vacío, o es el mismo que ya tenía, 
        // forzamos el original en el DTO para que el Mapper no lo borre.
        dto.setDni(dniOriginal);
    }

    //  AHORA SÍ, dejamos que el Mapper haga su trabajo con la seguridad de que el DNI es válido
    perfilMapper.updatePerfilFromDto(dto, perfil);

    //  Doble protección: Por si el mapper hace alguna locura con los nulos
    if (perfil.getDni() == null) {
        perfil.setDni(dniOriginal);
    }

    // Guardamos con seguridad
    try {
        Perfil actualizado = perfilRepository.saveAndFlush(perfil);
        return perfilMapper.toDto(actualizado);
    } catch (Exception e) {
        log.error("Error de persistencia: {}", e.getMessage());
        throw new RuntimeException("Error al guardar los cambios: " + e.getMessage());
    }
}

    @Override
    @Transactional
    public void quitarRolEntrenador(Long id) {
        log.info("Quitando rol ENTRENADOR del perfil ID: {}", id);
        Perfil perfil = perfilRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Perfil no encontrado"));

        Usuario usuario = perfil.getUsuario();
        if (usuario != null) {
            String nombreRolAEliminar = "ROLE_" + EnumRol.ENTRENADOR.name();
            // Usamos un iterador o removeIf sobre la colección de roles del usuario
            usuario.getRoles().removeIf(rol -> rol.getName().equalsIgnoreCase(nombreRolAEliminar));
            // Importante: No llamar a delete del perfil, solo guardar el usuario si es
            // necesario,
            // aunque al ser Transactional y estar la entidad gestionada, se guardará solo.
            log.info("Rol ENTRENADOR eliminado para el usuario: {}", usuario.getUsername());
        }
    }
}
