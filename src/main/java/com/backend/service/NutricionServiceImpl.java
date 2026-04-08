package com.backend.service;

import com.backend.domain.*;
import com.backend.domain.enums.EnumObjetivo;
import com.backend.dto.*;
import com.backend.exceptions.ResourceNotFoundException;
import com.backend.mapper.NutricionMapper;
import com.backend.repository.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NutricionServiceImpl implements INutricionService {

    private final UsuarioRepository usuarioRepository;
    private final DietaRepository dietaRepository;

    private final NutricionRepository nutricionRepository;

    private final NutricionMapper nutricionMapper;

    private final FatSecretService fatSecretService;

    @Override
    @Transactional
    public NutricionResponseDto generarPlanAutomatico(PerfilFisicoRequest request) {
        validarDatosFisicos(request);

        Usuario usuario = usuarioRepository.findById(request.getUsuarioId())
                .orElseThrow(
                        () -> new ResourceNotFoundException("Usuario no encontrado con ID: " + request.getUsuarioId()));

        int edad = 25;
        String genero = "MASCULINO";

        if (usuario.getPerfil() != null) {
            if (usuario.getPerfil().getFechaNacimiento() != null) {
                edad = Period.between(usuario.getPerfil().getFechaNacimiento(), LocalDate.now()).getYears();
            }
            if (usuario.getPerfil().getGenero() != null) {
                genero = usuario.getPerfil().getGenero().name();
            }
        }

        int kcalObjetivo = calcularCalorias(request, edad, genero);
        String categoriaNombre = resolverCategoria(request.getObjetivo());

        double proteinasGramos = request.getPeso() * 2.0;
        double grasasGramos = request.getPeso() * 0.8;

        double caloriasRestantes = kcalObjetivo - (proteinasGramos * 4) - (grasasGramos * 9);
        double carbosGramos = Math.max(0, caloriasRestantes / 4.0);

        String macrosJson = buildMacrosJson(proteinasGramos, carbosGramos, grasasGramos);
        String menuFinal = obtenerMenu(kcalObjetivo, proteinasGramos, carbosGramos, grasasGramos, macrosJson);

        // --- EXTRACCIÓN DE DATOS DE FATSECRET PARA NOMBRE E IMAGEN ---
        String nombreSugerido = "Sugerencia según macros";
        String imagenSugerida = "https://images.unsplash.com/photo-1490645935967-10de6ba17061?q=80&w=500";

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(menuFinal);
            // Intentamos obtener la primera receta del array
            JsonNode firstRecipe = root.path("recipes").path("recipe").get(0);
            
            if (firstRecipe != null && !firstRecipe.isMissingNode()) {
                nombreSugerido = firstRecipe.path("recipe_name").asText(nombreSugerido);
                imagenSugerida = firstRecipe.path("recipe_image").asText(imagenSugerida);
            }
        } catch (Exception e) {
            log.warn("No se pudo parsear el nombre/imagen de la receta, usando valores por defecto: {}", e.getMessage());
        }

        Dieta dietaBase = dietaRepository.findByCategoriaDietaNombre(categoriaNombre)
                .stream().findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Catálogo vacío para: " + categoriaNombre));

        Nutricion nuevoPlan = Nutricion.builder()
                .usuario(usuario)
                .nombreDieta("Plan " + categoriaNombre)
                .tipoDieta(dietaBase.getNombre())
                .caloriasObjetivo(kcalObjetivo)
                .macronutrientesJson(menuFinal)
                .fechaGeneracion(LocalDateTime.now())
                .build();

        double pDesayuno = 0.30;
        double pAlmuerzo = 0.40;
        double pCena = 0.30;

        ComidaDiaria desayuno = ComidaDiaria.builder()
                .momento("Desayuno")
                .nombreAlimento(nombreSugerido)
                .imagenUrl(imagenSugerida)
                .calorias((double) Math.round(kcalObjetivo * pDesayuno)) // Redondeamos pero mantenemos el tipo double
                .proteina((double) Math.round(proteinasGramos * pDesayuno))
                .carbohidratos((double) Math.round(carbosGramos * pDesayuno))
                .grasas((double) Math.round(grasasGramos * pDesayuno))
                .plan(nuevoPlan)
                .build();

        ComidaDiaria almuerzo = ComidaDiaria.builder()
                .momento("Almuerzo")
                .nombreAlimento(nombreSugerido)
                .imagenUrl(imagenSugerida)
                .calorias((double) Math.round(kcalObjetivo * pAlmuerzo))
                .proteina((double) Math.round(proteinasGramos * pAlmuerzo))
                .carbohidratos((double) Math.round(carbosGramos * pAlmuerzo))
                .grasas((double) Math.round(grasasGramos * pAlmuerzo))
                .plan(nuevoPlan)
                .build();

        ComidaDiaria cena = ComidaDiaria.builder()
                .momento("Cena")
                .nombreAlimento(nombreSugerido)
                .imagenUrl(imagenSugerida)
                .calorias((double) Math.round(kcalObjetivo * pCena))
                .proteina((double) Math.round(proteinasGramos * pCena))
                .carbohidratos((double) Math.round(carbosGramos * pCena))
                .grasas((double) Math.round(grasasGramos * pCena))
                .plan(nuevoPlan)
                .build();

        nuevoPlan.setComidas(java.util.List.of(desayuno, almuerzo, cena));

        Nutricion planGuardado = nutricionRepository.save(nuevoPlan);
        log.info("Plan de nutrición generado exitosamente para el usuario ID: {}", usuario.getId());

        // Mapeamos a NutricionResponseDto
        return nutricionMapper.toResponseDto(planGuardado);
    }

    @Override
    public NutricionResponseDto obtenerUltimoPlanPorUsuario(Long usuarioId) {

        return nutricionRepository.findFirstByUsuarioIdOrderByIdDesc(usuarioId)
                .map(nutricionMapper::toResponseDto)
                .orElseThrow(() -> new ResourceNotFoundException("No hay planes para el usuario: " + usuarioId));
    }

    @Override
    @Transactional(readOnly = true)

    public List<NutricionResponseDto> obtenerHistorial(Long usuarioId) {
        if (!usuarioRepository.existsById(usuarioId)) {
            throw new ResourceNotFoundException("Usuario no encontrado con ID: " + usuarioId);
        }

        return nutricionMapper.toResponseDtoList(
                nutricionRepository.findByUsuarioIdOrderByFechaGeneracionDesc(usuarioId));
    }

    private void validarDatosFisicos(PerfilFisicoRequest request) {
        if (request.getPeso() <= 0 || request.getAltura() <= 0) {
            throw new IllegalArgumentException("Peso y altura deben ser mayores que cero");
        }
    }

    private int calcularCalorias(PerfilFisicoRequest request, int edad, String genero) {
        boolean esHombre = genero.matches("(?i)HOMBRE|MASCULINO");

        double tmb = (10 * request.getPeso()) + (6.25 * request.getAltura()) - (5 * edad) + (esHombre ? 5 : -161);
        double tdee = tmb * request.getNivelActividad().getFactor();

        return switch (request.getObjetivo()) {
            case GANAR -> (int) (tdee * 1.10);
            case PERDER -> (int) (tdee * 0.80);
            case MANTENER -> (int) tdee;
        };
    }

    private String resolverCategoria(EnumObjetivo objetivo) {
        return switch (objetivo) {
            case GANAR -> "VOLUMEN";
            case PERDER -> "DEFINICION";
            case MANTENER -> "MANTENIMIENTO";
        };
    }

    private String buildMacrosJson(double proteinas, double carbos, double grasas) {
        return String.format(
                "{\"proteinas\": \"%.1fg\", \"carbohidratos\": \"%.1fg\", \"grasas\": \"%.1fg\"}",
                proteinas, carbos, grasas);
    }

    private String obtenerMenu(int kcal, double prot, double carbos, double grasas, String fallback) {
        try {
            String resultado = fatSecretService.buscarRecetasPorMacros(kcal, (int) prot, (int) carbos, (int) grasas);
            if (resultado == null || resultado.contains("\"error\"")) {
                log.warn("FatSecret devolvió error, usando fallback de macros");
                return fallback;
            }
            return resultado;
        } catch (Exception e) {
            log.error("Error al llamar a FatSecret: {}", e.getMessage());
            return fallback;
        }
    }
}