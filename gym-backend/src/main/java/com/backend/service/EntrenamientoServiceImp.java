package com.backend.service;

import com.backend.domain.Entrenamiento;
import com.backend.dto.EntrenamientoRequestDto;
import com.backend.dto.EntrenamientoResponseDto;
import com.backend.repository.EntrenamientoRepository;
import com.backend.mapper.EntrenamientoMapper;
import com.backend.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.backend.repository.TutorialRepository;
import com.backend.domain.Tutorial;
import com.backend.repository.CategoriaTutorialRepository;
import com.backend.domain.CategoriaTutorial;
import com.backend.repository.RutinaRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EntrenamientoServiceImp implements IEntrenamientoService {

    private final EntrenamientoRepository entrenamientoRepository;
    private final EntrenamientoMapper entrenamientoMapper;
    private final TutorialRepository tutorialRepository;
    private final CategoriaTutorialRepository categoriaTutorialRepository;
    private final RutinaRepository rutinaRepository;
        
 @Override
@Transactional
public EntrenamientoResponseDto crear(EntrenamientoRequestDto dto) {
    Entrenamiento entrenamiento = entrenamientoMapper.toEntity(dto);
    log.info("Creando entrenamiento: {}", dto.getNombre());

    // 1. Forzar asignación de campos (Asegúrate de que urlImagen se guarde)
    entrenamiento.setNombre(dto.getNombre());
    entrenamiento.setDescripcion(dto.getDescripcion());
    entrenamiento.setDuracion(dto.getDuracion());
    entrenamiento.setIntensidad(dto.getIntensidad());
    entrenamiento.setCantidadEjercicios(dto.getCantidadEjercicios());
    entrenamiento.setUrlImagen(dto.getUrlImagen()); // Si esto llega NULL desde Android, revisa el JSON de salida en el log

    // 2. CORRECCIÓN CATEGORÍA: Manejar ID o Nombre ("GENERAL")
    if (dto.getCategoriaId() != null) {
        CategoriaTutorial categoria = categoriaTutorialRepository.findById(dto.getCategoriaId())
            .orElseThrow(() -> new RuntimeException("Categoría por ID no encontrada"));
        entrenamiento.setCategoria(categoria);
    } 
    // Si no viene ID pero viene el texto "GENERAL"
    else if (dto.getCategoria() != null && !dto.getCategoria().isEmpty()) {
        CategoriaTutorial categoria = categoriaTutorialRepository.findByNombre(dto.getCategoria())
            .orElseGet(() -> {
                // Si no existe, la creamos para que no sea NULL
                CategoriaTutorial nueva = new CategoriaTutorial();
                nueva.setNombre(dto.getCategoria());
                return categoriaTutorialRepository.save(nueva);
            });
        entrenamiento.setCategoria(categoria);
    }

    Entrenamiento saved = entrenamientoRepository.save(entrenamiento);

    // 3. Tutoriales existentes
    if (dto.getTutorialesIds() != null && !dto.getTutorialesIds().isEmpty()) {
        List<Tutorial> tutoriales = tutorialRepository.findAllById(dto.getTutorialesIds());
        for (Tutorial t : tutoriales) {
            t.setEntrenamiento(saved); 
        }
        tutorialRepository.saveAll(tutoriales);
        saved.setTutoriales(tutoriales);
    }

    // 4. Tutorial NUEVO
    Tutorial nuevoTutorial = null;
    if (dto.getUrlVideo() != null && !dto.getUrlVideo().isEmpty()) {
        nuevoTutorial = new Tutorial();
        nuevoTutorial.setTitulo(dto.getNombre());
        nuevoTutorial.setUrlVideo(dto.getUrlVideo());
        nuevoTutorial.setDescripcion(dto.getDescripcion());
        nuevoTutorial.setEntrenamiento(saved);
        nuevoTutorial = tutorialRepository.save(nuevoTutorial);
    }

    Tutorial tutorialParaVideo = (nuevoTutorial != null) ? nuevoTutorial : 
        (saved.getTutoriales() != null && !saved.getTutoriales().isEmpty() ? saved.getTutoriales().get(0) : null);

    return entrenamientoMapper.toResponseDto(saved, tutorialParaVideo);
}
    @Override
    @Transactional(readOnly = true)
    public List<EntrenamientoResponseDto> listarPorUsuario(Long usuarioId) {
        log.info("Buscando entrenamientos para el usuario ID: {}", usuarioId);

        //  Obtenemos todos los entrenamientos
        List<Entrenamiento> entrenamientos = entrenamientoRepository.findAll();

        //  Mapeamos cada entrenamiento buscando su tutorial correspondiente
        return entrenamientos.stream()
                .map(entrenamiento -> {
                    // Obtenemos el primer tutorial (si existe) para pasárselo al mapper
                    Tutorial tutorial = (entrenamiento.getTutoriales() != null
                            && !entrenamiento.getTutoriales().isEmpty())
                                    ? entrenamiento.getTutoriales().get(0)
                                    : null;

                    // Ahora llamamos al método con el nombre correcto: toResponseDto
                    return entrenamientoMapper.toResponseDto(entrenamiento, tutorial);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public EntrenamientoResponseDto obtenerPorId(Long id) {
        Entrenamiento entrenamiento = entrenamientoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No encontrado"));

        // Sacamos el tutorial
        Tutorial tutorial = (entrenamiento.getTutoriales() != null && !entrenamiento.getTutoriales().isEmpty())
                ? entrenamiento.getTutoriales().get(0)
                : null;

        // Retornamos pasando los dos argumentos
        return entrenamientoMapper.toResponseDto(entrenamiento, tutorial);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EntrenamientoResponseDto> listarTodos() {
        return entrenamientoRepository.findAll().stream()
                .map(entrenamiento -> {
                    // Buscamos el primer tutorial de la lista de este entrenamiento
                    // Si la lista está vacía, enviamos null
                    Tutorial tutorial = (entrenamiento.getTutoriales() != null
                            && !entrenamiento.getTutoriales().isEmpty())
                                    ? entrenamiento.getTutoriales().get(0)
                                    : null;

                    // Ahora llamamos al mapper con los DOS parámetros que pide
                    return entrenamientoMapper.toResponseDto(entrenamiento, tutorial);
                })
                .toList();
    }

    @Override
    @Transactional
    public EntrenamientoResponseDto actualizar(Long id, EntrenamientoRequestDto dto) {
        log.info("Actualizando entrenamiento maestro ID: {}", id);

        Entrenamiento existente = entrenamientoRepository.findById(id)
                .orElseThrow(
                        () -> new ResourceNotFoundException("No se puede actualizar: Entrenamiento no encontrado"));

        //  Actualizamos los datos del ejercicio
        existente.setNombre(dto.getNombre());
        existente.setDescripcion(dto.getDescripcion());
        existente.setDuracion(dto.getDuracion());
        existente.setIntensidad(dto.getIntensidad());
        existente.setCantidadEjercicios(dto.getCantidadEjercicios());

        Entrenamiento guardado = entrenamientoRepository.save(existente);

        // Buscamos el tutorial asociado para pasarlo al mapper
        Tutorial tutorial = (guardado.getTutoriales() != null && !guardado.getTutoriales().isEmpty())
                ? guardado.getTutoriales().get(0)
                : null;

        // Si quieres que al editar el entrenamiento también se edite el
        // video:
        if (tutorial != null) {
            tutorial.setUrlVideo(dto.getUrlVideo());
            // tutorial.setDescripcion(dto.getDescripcion()); // Si quieres sincronizar la
            // descripción
            tutorialRepository.save(tutorial);
        }

        log.info("Entrenamiento {} actualizado correctamente", id);

        //  RETORNO CON LOS DOS PARÁMETROS (¡Esto es lo que pedía la línea 104!)
        return entrenamientoMapper.toResponseDto(guardado, tutorial);
    }

    @Override
   @Transactional
public void eliminar(Long id) {
    if (!entrenamientoRepository.existsById(id)) {
        throw new RuntimeException("Entrenamiento no encontrado");
    }
    entrenamientoRepository.deleteById(id);
}

    private EntrenamientoResponseDto mapearADto(Entrenamiento entrenamiento, Tutorial tutorial) {
        return EntrenamientoResponseDto.builder()
                .id(entrenamiento.getId())
                .nombre(entrenamiento.getNombre())
                .intensidad(entrenamiento.getIntensidad())
                .duracion(entrenamiento.getDuracion())
                .urlVideo(tutorial.getUrlVideo())
                .descripcion(tutorial.getDescripcion())
                .build();
    }
}