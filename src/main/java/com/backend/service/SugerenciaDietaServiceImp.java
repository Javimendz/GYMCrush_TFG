package com.backend.service;

import com.backend.domain.SugerenciaDieta;
import com.backend.dto.SugerenciaDietaReponseDto;
import com.backend.dto.SugerenciaDietaRequestDto;
import com.backend.exceptions.ResourceNotFoundException;
import com.backend.mapper.SugerenciaDietaMapper;
import com.backend.repository.DietaRepository;
import com.backend.repository.SugerenciaDietaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SugerenciaDietaServiceImp implements ISugerenciaDietaService {

    private final SugerenciaDietaRepository sugerenciaRepository;
    private final SugerenciaDietaMapper sugerenciaMapper;
    private final DietaRepository dietaRepository;

    @Override
@Transactional
public SugerenciaDietaReponseDto guardarAsignacion(SugerenciaDietaRequestDto dto) {
    // 1. Buscamos la dieta base (la número 5 en tu caso) para obtener sus datos
    var dietaBase = dietaRepository.findById(dto.getDietaId())
            .orElseThrow(() -> new RuntimeException("La dieta con ID " + dto.getDietaId() + " no existe"));

    // 2. Mapeamos el DTO a la entidad inicial
    SugerenciaDieta entidad = sugerenciaMapper.toEntity(dto);
    
    // 3. RELLENAMOS los campos que faltan (esto evita el Error 500)
    entidad.setNombreDieta(dietaBase.getNombre());
    entidad.setCaloriasObjetivo(dietaBase.getObjetivoCalorico());
    entidad.setTipoDieta(dietaBase.getTipo() != null ? dietaBase.getTipo() : "PERSONALIZADA");
    entidad.setFechaGeneracion(LocalDateTime.now());
    entidad.setEsFavorita(false); // Por defecto no es favorita
    
    // Si tu DTO tiene notas, las guardamos en el campo detalles (o el que uses)
    //entidad.setDetalles(dto.getNotas());

    // 4. Guardamos y devolvemos la respuesta
    return sugerenciaMapper.toResponseDto(sugerenciaRepository.save(entidad));
}
    @Override
    @Transactional(readOnly = true)
    public List<SugerenciaDietaReponseDto> obtenerHistorial(Long usuarioId) {
        // Obtenemos el historial ordenado por fecha descendente (más reciente primero)
        return sugerenciaMapper.toResponseDtoList(
                sugerenciaRepository.findByUsuarioIdOrderByFechaGeneracionDesc(usuarioId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<SugerenciaDietaReponseDto> obtenerFavoritas(Long usuarioId) {
        // Usamos el nuevo método del repositorio
        List<SugerenciaDieta> favoritas = sugerenciaRepository.findByUsuarioIdAndEsFavoritaTrue(usuarioId);
    
        // Convertimos la lista de entidades a lista de DTOs usando tu mapper
        return sugerenciaMapper.toResponseDtoList(favoritas);
    }

    @Override
    @Transactional
    public SugerenciaDietaReponseDto marcarComoFavorita(Long sugerenciaId) {
        SugerenciaDieta sugerencia = sugerenciaRepository.findById(sugerenciaId)
                .orElseThrow(() -> new ResourceNotFoundException("Sugerencia no encontrada"));
    
    // Cambiamos el estado (Toggle)
        sugerencia.setEsFavorita(!sugerencia.isEsFavorita());
    
        return sugerenciaMapper.toResponseDto(sugerenciaRepository.save(sugerencia));
    }

    

}