package com.backend.service;

import com.backend.domain.*;
import com.backend.domain.enums.EnumObjetivo;
import com.backend.domain.enums.EnumNivelActividad;
import com.backend.dto.*;
import com.backend.exceptions.ResourceNotFoundException;
import com.backend.mapper.NutricionMapper;
import com.backend.mapper.SugerenciaDietaMapper;
import com.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;

@Service
@RequiredArgsConstructor
public class NutricionServiceImpl implements INutricionService {

    private final UsuarioRepository usuarioRepository;
    private final DietaRepository dietaRepository;
    private final SugerenciaDietaRepository sugerenciaRepository;
    private final SugerenciaDietaMapper sugerenciaMapper;
    private final NutricionMapper nutricionMapper;

    private final FatSecretService fatSecretService;

    @Override
@Transactional
public SugerenciaDietaReponseDto generarPlanAutomatico(PerfilFisicoRequest request) {
    // 1. Validación de seguridad básica
    if (request.getPeso() <= 0 || request.getAltura() <= 0) {
        throw new RuntimeException("Datos físicos inválidos");
    }

    Usuario usuario = usuarioRepository.findById(request.getUsuarioId())
            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

    Perfil perfil = usuario.getPerfil();
    int edad = Period.between(perfil.getFechaNacimiento(), LocalDate.now()).getYears();

    // 2. Cálculo Metabólico (Mifflin-St Jeor)
    boolean esHombre = perfil.getGenero().name().matches("(?i)HOMBRE|MASCULINO");
    double tmb = (10 * request.getPeso()) + (6.25 * request.getAltura()) - (5 * edad) + (esHombre ? 5 : -161);
    double tdee = tmb * request.getNivelActividad().getFactor();

    int kcalObjetivo;
    String categoriaNombre;

    switch (request.getObjetivo()) {
        case GANAR: 
            kcalObjetivo = (int) (tdee * 1.10);
            categoriaNombre = "VOLUMEN";
            break;
        case PERDER:
            kcalObjetivo = (int) (tdee * 0.80);
            categoriaNombre = "DEFINICION";
            break;
        default:
            kcalObjetivo = (int) tdee;
            categoriaNombre = "MANTENIMIENTO";
            break;
    }

    // 3. Macronutrientes (2g Prot, 0.8g Grasa, resto CH)
    double proteinasGramos = request.getPeso() * 2.0;
    double grasasGramos = request.getPeso() * 0.8;
    double carbohidratosGramos = (kcalObjetivo - (proteinasGramos * 4) - (grasasGramos * 9)) / 4;

    String macrosJson = String.format(
        "{\"proteinas\": \"%.1fg\", \"carbohidratos\": \"%.1fg\", \"grasas\": \"%.1fg\"}",
        proteinasGramos, carbohidratosGramos, grasasGramos
    );

    // 4. Obtención de Menú (Con validación de error de FatSecret)
    String menuFinal;
    try {
        menuFinal = fatSecretService.buscarRecetasPorMacros(
                kcalObjetivo, (int) proteinasGramos, (int) carbohidratosGramos, (int) grasasGramos
        );
        
        // Si la respuesta de la API es un error (como el de la IP), usamos el fallback
        if (menuFinal.contains("\"error\"")) {
            menuFinal = macrosJson;
        }
    } catch (Exception e) {
        e.printStackTrace();
        menuFinal = macrosJson;
    }

    Dieta dietaBase = dietaRepository.findByCategoriaDietaNombre(categoriaNombre).stream()
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Catálogo vacío para " + categoriaNombre));

    // 5. Persistencia
    SugerenciaDieta sugerencia = SugerenciaDieta.builder()
            .usuario(usuario)
            .dieta(dietaBase)
            .nombreDieta("Plan " + categoriaNombre)
            .caloriasObjetivo(kcalObjetivo)
            .tipoDieta(dietaBase.getNombre())
            .macronutrientesJson(menuFinal) 
            .fechaGeneracion(LocalDateTime.now())
            .build();

    // Usamos el Mapper que ya sabe manejar JsonNode
    return sugerenciaMapper.toResponseDto(sugerenciaRepository.save(sugerencia));
}
 

@Override
public NutricionResponseDto obtenerUltimoPlanPorUsuario(Long usuarioId) {
    // 1. Llamamos al repo, que devuelve Optional<SugerenciaDieta>
    // 2. Usamos .map() directamente sobre el Optional
    // 3. .orElse(null) extrae el resultado o devuelve null si no existe
    return sugerenciaRepository.findFirstByUsuarioIdOrderByIdDesc(usuarioId)
            .map(nutricionMapper::toResponseDto) 
            .orElse(null);
}

}