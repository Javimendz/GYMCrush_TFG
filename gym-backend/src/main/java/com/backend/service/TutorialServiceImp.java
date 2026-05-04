
package com.backend.service;

import com.backend.domain.CategoriaTutorial;
import com.backend.domain.Tutorial;
import com.backend.domain.enums.TipoNotificacion;
import com.backend.dto.CategoriaTutorialResponseDto;
import com.backend.dto.TutorialRequestDto;
import com.backend.dto.TutorialResponseDto;
import com.backend.event.TutorialEvent;
import com.backend.exceptions.BusinessException;
import com.backend.mapper.CategoriaTutorialMapper;
import com.backend.mapper.TutorialMapper;
import com.backend.repository.CategoriaTutorialRepository;
import com.backend.repository.TutorialRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.backend.repository.UsuarioRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.backend.domain.Usuario;          
import com.backend.repository.UsuarioRepository;
import java.util.List;

/**
 * Implementación del servicio para la gestión de tutoriales educativos.
 * <p>
 * Esta clase maneja las operaciones CRUD para tutoriales,
 * incluyendo creación, consulta por categoría y búsqueda general.
 * </p>
 *
 * @author Backend Team
 * @version 1.0
 * @since 2026
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TutorialServiceImp implements ITutorialService {

    /** Repositorio para el acceso a datos de tutoriales */
    private final TutorialRepository tutorialRepository;
    private final UsuarioRepository usuarioRepository;
    /** Repositorio para el acceso a datos de categorías */
    private final CategoriaTutorialRepository categoriaRepository;

    /** Mapper para convertir entre entidades y DTOs de tutoriales */
    private final TutorialMapper tutorialMapper;

    /** Mapper para convertir entre entidades y DTOs de categorías */
    private final CategoriaTutorialMapper categoriaMapper;

    private final ApplicationEventPublisher eventPublisher;

    /**
     * Obtiene todos los tutoriales disponibles en el sistema.
     * <p>
     * Retorna una lista completa de todos los tutoriales educativos
     * ordenados por defecto según la configuración de la base de datos.
     * </p>
     *
     * @return Lista de DTOs con todos los tutoriales
     */
    @Override
    @Transactional(readOnly = true)
    public List<TutorialResponseDto> obtenerTodos() {
        log.info("Obteniendo todos los tutoriales");

        // Obtener todos los tutoriales y convertirlos a DTOs
        List<TutorialResponseDto> tutoriales = tutorialRepository.findAll().stream()
                .map(tutorialMapper::toDto)
                .toList();
        log.debug("Total de tutoriales encontrados: {}", tutoriales.size());
        return tutoriales;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TutorialResponseDto> buscarPorTitulo(String titulo) {
        log.info("Buscando tutoriales locales que contengan: {}", titulo);

        return tutorialRepository.findByTituloContainingIgnoreCase(titulo).stream()
                .map(tutorialMapper::toDto)
                .toList();
    }

    /**
     * Obtiene los tutoriales filtrados por categoría específica.
     * <p>
     * Retorna todos los tutoriales que pertenecen a una categoría
     * determinada, útil para navegación y organización de contenido.
     * </p>
     *
     * @param categoriaId ID de la categoría a filtrar
     * @return Lista de DTOs con los tutoriales de la categoría
     */
    @Override
    @Transactional(readOnly = true)
    public List<TutorialResponseDto> obtenerPorCategoria(Long categoriaId) {
        log.info("Buscando tutoriales por categoriaId: {}", categoriaId);

        // Buscar tutoriales por categoría y convertirlos a DTOs
        List<TutorialResponseDto> tutoriales = tutorialRepository.findByCategoriaId(categoriaId).stream()
                .map(tutorialMapper::toDto)
                .toList();
        log.debug("Tutoriales encontrados para categoria {}: {}", categoriaId, tutoriales.size());
        return tutoriales;
    }

    /**
     * Crea un nuevo tutorial en el sistema.
     * <p>
     * Crea un nuevo tutorial con los datos proporcionados en el DTO.
     * </p>
     *
     * @param dto DTO con los datos del tutorial a crear
     * @return DTO con los datos del tutorial creado
     */
    @Override
    @Transactional
    public TutorialResponseDto crear(TutorialRequestDto dto, String username) {
        log.info("Creando tutorial por: {}", username);

        Usuario autor = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException("Usuario no encontrado"));

        // Validaciones de duplicados
        if (tutorialRepository.existsByTituloIgnoreCase(dto.getTitulo().trim())) {
            throw new BusinessException("Ya existe un ejercicio con ese nombre.");
        }

        CategoriaTutorial categoria = categoriaRepository.findById(dto.getCategoriaId())
                .orElseThrow(() -> new BusinessException("Categoría no válida."));

        Tutorial tutorial = tutorialMapper.toEntity(dto);
        tutorial.setCategoria(categoria);

        
        boolean isAdmin = autor.getRoles().stream()
                .anyMatch(rol -> rol.getName().equals("ROLE_ADMIN"));

        if (isAdmin) {
            tutorial.setUsuario(null);
            tutorial.setEsGlobal(true);
        } else {
            tutorial.setUsuario(autor);
            tutorial.setEsGlobal(false);
        }

        return tutorialMapper.toDto(tutorialRepository.save(tutorial));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TutorialResponseDto> listarParaUsuario(Long usuarioId) {
        return tutorialRepository.findGlobalesYDelUsuario(usuarioId).stream()
                .map(tutorialMapper::toDto)
                .toList();
    }
    /**
     * Elimina un tutorial por su ID.
     * <p>
     * Elimina un tutorial de la base de datos.
     * </p>
     *
     * @param id ID del tutorial a eliminar
     */
    @Override
    @Transactional
    public void eliminar(Long id) {
        log.info("Eliminando tutorial con ID: {}", id);
        if (!tutorialRepository.existsById(id)) {
            log.warn("No se puede eliminar: El tutorial con ID {} no existe", id);
            throw new RuntimeException("No se puede eliminar: El tutorial no existe");
        }
        tutorialRepository.deleteById(id);
        log.info("Tutorial con ID {} eliminado exitosamente", id);
    }

    /**
     * Lista todas las categorías de tutoriales.
     * <p>
     * Retorna una lista de categorías de tutoriales.
     * </p>
     *
     * @return lista de categorías de tutoriales
     */
    @Override
    @Transactional(readOnly = true)
    public List<CategoriaTutorialResponseDto> listarCategorias() {
        log.info("Listando todas las categorías de tutoriales");
        List<CategoriaTutorialResponseDto> categorias = categoriaRepository.findAll().stream()
                .map(categoriaMapper::toDto)
                .toList();
        log.debug("Total de categorías encontradas: {}", categorias.size());
        return categorias;
    }
}