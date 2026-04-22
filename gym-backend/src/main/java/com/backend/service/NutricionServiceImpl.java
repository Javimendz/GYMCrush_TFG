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
import com.backend.domain.enums.EnumNivelActividad;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

@Slf4j
@Service
@RequiredArgsConstructor
public class NutricionServiceImpl implements INutricionService {

    private final UsuarioRepository usuarioRepository;
    private final DietaRepository dietaRepository;

    private final NutricionRepository nutricionRepository;
    private final ObjectMapper objectMapper;
    private final NutricionMapper nutricionMapper;

    private final FatSecretService fatSecretService;

    @Override
    @Transactional
    public NutricionResponseDto generarPlanAutomatico(NutricionRequestDto request) {

        validarDatosFisicos(request);

        Usuario usuario = usuarioRepository.findById(request.getUsuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        int edad = (request.getEdad() != null) ? request.getEdad() : 25;
        String genero = (request.getGenero() != null) ? request.getGenero() : "MASCULINO";

        if (usuario.getPerfil() != null) {
            if (usuario.getPerfil().getFechaNacimiento() != null) {
                edad = Period.between(usuario.getPerfil().getFechaNacimiento(), LocalDate.now()).getYears();
            }
            if (usuario.getPerfil().getGenero() != null) {
                genero = usuario.getPerfil().getGenero().name();
            }
        }

        // 🔥 CÁLCULOS
        int kcalObjetivo = calcularCalorias(request, edad, genero);
        double proteinas = request.getPeso() * 2.0;
        double grasas = request.getPeso() * 0.8;
        double carbs = Math.max(0, (kcalObjetivo - (proteinas * 4 + grasas * 9)) / 4);

        String jsonFatSecret = obtenerMenu(kcalObjetivo, proteinas, carbs, grasas, "{}");

        List<FatSecretRecipeDto> recetas = List.of();

        try {
            FatSecretResponseDto fs = objectMapper.readValue(jsonFatSecret, FatSecretResponseDto.class);
            if (fs.getRecipes() != null) {
                recetas = fs.getRecipes().getRecipe();
            }
        } catch (Exception e) {
            log.warn("Error parseando recetas: {}", e.getMessage());
        }

Set<String> usadas = new HashSet<>();
        FatSecretRecipeDto desayunoReceta = getRecetaAleatoria(recetas, usadas);
        FatSecretRecipeDto almuerzoReceta = getRecetaAleatoria(recetas, usadas);
        FatSecretRecipeDto cenaReceta = getRecetaAleatoria(recetas, usadas);

        Dieta dietaBase = dietaRepository.findByCategoriaDietaNombre(
                resolverCategoria(EnumObjetivo.valueOf(request.getObjetivo().toUpperCase())))
                .stream().findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("No hay dietas"));

        Nutricion plan = Nutricion.builder()
                .usuario(usuario)
                .nombreDieta("Plan personalizado")
                .tipoDieta(dietaBase.getNombre())
                .caloriasObjetivo(kcalObjetivo)
                .macronutrientesJson(jsonFatSecret)
                .fechaGeneracion(LocalDateTime.now())
                .build();

        String defaultImg = "https://images.unsplash.com/photo-1490645935967-10de6ba17061";

        ComidaDiaria desayuno = crearComida(
                "Desayuno",
                desayunoReceta != null ? desayunoReceta.getRecipeName() : "Desayuno saludable",
                desayunoReceta != null ? desayunoReceta.getRecipeImage() : defaultImg,
                kcalObjetivo, proteinas, carbs, grasas, 0.3, plan);

        ComidaDiaria almuerzo = crearComida(
                "Almuerzo",
                almuerzoReceta != null ? almuerzoReceta.getRecipeName() : "Almuerzo equilibrado",
                almuerzoReceta != null ? almuerzoReceta.getRecipeImage() : defaultImg,
                kcalObjetivo, proteinas, carbs, grasas, 0.4, plan);

        ComidaDiaria cena = crearComida(
                "Cena",
                cenaReceta != null ? cenaReceta.getRecipeName() : "Cena ligera",
                cenaReceta != null ? cenaReceta.getRecipeImage() : defaultImg,
                kcalObjetivo, proteinas, carbs, grasas, 0.3, plan);

        plan.setComidas(List.of(desayuno, almuerzo, cena));

        return nutricionMapper.toResponseDto(nutricionRepository.save(plan));
    }

    public NutricionResponseDto generarPlanSemanal(NutricionRequestDto request) {

        validarDatosFisicos(request);

        Usuario usuario = usuarioRepository.findById(request.getUsuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        int edad = (request.getEdad() != null) ? request.getEdad() : 25;
        String genero = (request.getGenero() != null) ? request.getGenero() : "MASCULINO";

        if (usuario.getPerfil() != null) {
            if (usuario.getPerfil().getFechaNacimiento() != null) {
                edad = Period.between(usuario.getPerfil().getFechaNacimiento(), LocalDate.now()).getYears();
            }
            if (usuario.getPerfil().getGenero() != null) {
                genero = usuario.getPerfil().getGenero().name();
            }
        }

        int kcal = calcularCalorias(request, edad, genero);

        double proteinas = request.getPeso() * 2.0;
        double grasas = request.getPeso() * 0.8;
        double carbs = Math.max(0, (kcal - (proteinas * 4 + grasas * 9)) / 4);

        // 🔥 Traemos MUCHAS recetas (clave para semana)
        String json = fatSecretService.buscarRecetasPorMacros(kcal * 2, (int) proteinas, (int) carbs, (int) grasas);

        List<FatSecretRecipeDto> recetas = List.of();

        try {
            FatSecretResponseDto fs = objectMapper.readValue(json, FatSecretResponseDto.class);
            if (fs.getRecipes() != null) {
                recetas = fs.getRecipes().getRecipe();
            }
        } catch (Exception e) {
            log.warn("Error parseando recetas: {}", e.getMessage());
        }

        if (recetas.isEmpty()) {
            throw new RuntimeException("No hay recetas suficientes para generar dieta semanal");
        }

Set<String> usadas = new HashSet<>();
        Dieta dietaBase = dietaRepository.findByCategoriaDietaNombre(
                resolverCategoria(EnumObjetivo.valueOf(request.getObjetivo().toUpperCase())))
                .stream().findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("No hay dietas"));

        Nutricion plan = Nutricion.builder()
                .usuario(usuario)
                .nombreDieta("Plan semanal personalizado")
                .tipoDieta(dietaBase.getNombre())
                .caloriasObjetivo(kcal)
                .macronutrientesJson(json)
                .fechaGeneracion(LocalDateTime.now())
                .build();

        String defaultImg = "https://images.unsplash.com/photo-1490645935967-10de6ba17061";

        List<ComidaDiaria> comidas = new java.util.ArrayList<>();

        String[] dias = { "Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo" };

        for (String dia : dias) {

            FatSecretRecipeDto desayuno = getRecetaAleatoria(recetas, usadas);
            FatSecretRecipeDto almuerzo = getRecetaAleatoria(recetas, usadas);
            FatSecretRecipeDto cena = getRecetaAleatoria(recetas, usadas);

            comidas.add(crearComida(
                    dia + " - Desayuno",
                    getNombre(desayuno, "Desayuno saludable"),
                    getImagen(desayuno, defaultImg),
                    kcal, proteinas, carbs, grasas, 0.3, plan));

            comidas.add(crearComida(
                    dia + " - Almuerzo",
                    getNombre(almuerzo, "Almuerzo equilibrado"),
                    getImagen(almuerzo, defaultImg),
                    kcal, proteinas, carbs, grasas, 0.4, plan));

            comidas.add(crearComida(
                    dia + " - Cena",
                    getNombre(cena, "Cena ligera"),
                    getImagen(cena, defaultImg),
                    kcal, proteinas, carbs, grasas, 0.3, plan));
        }

        plan.setComidas(comidas);

        return nutricionMapper.toResponseDto(nutricionRepository.save(plan));
    }

    private String getNombre(FatSecretRecipeDto r, String def) {
        return (r != null && r.getRecipeName() != null) ? r.getRecipeName() : def;
    }

    private String getImagen(FatSecretRecipeDto r, String def) {
        return (r != null && r.getRecipeImage() != null) ? r.getRecipeImage() : def;
    }

    // Método auxiliar para no repetir código al crear las 3 comidas
    private ComidaDiaria crearComida(String momento, String nombre, String img, int kcal, double p, double c, double g,
            double porcentaje, Nutricion plan) {
        return ComidaDiaria.builder()
                .momento(momento)
                .nombreAlimento(nombre)
                .imagenUrl(img)
                .calorias((double) Math.round(kcal * porcentaje))
                .proteina((double) Math.round(p * porcentaje))
                .carbohidratos((double) Math.round(c * porcentaje))
                .grasas((double) Math.round(g * porcentaje))
                .plan(plan)
                .build();
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

    private void validarDatosFisicos(NutricionRequestDto request) {
        if (request.getPeso() == null || request.getPeso() <= 0 ||
                request.getAltura() == null || request.getAltura() <= 0) {
            throw new IllegalArgumentException("Peso y altura deben ser mayores que cero");
        }
    }

    private int calcularCalorias(NutricionRequestDto request, int edad, String genero) {
        boolean esHombre = genero.matches("(?i)HOMBRE|MASCULINO");

        double alturaEnCm = request.getAltura();
        if (alturaEnCm < 3) {
            alturaEnCm *= 100;
        }

        // 1. Calculamos TMB
        double tmb = (10 * request.getPeso()) + (6.25 * alturaEnCm) - (5 * edad) + (esHombre ? 5 : -161);

        // 2. CORRECCIÓN ERROR 175: Convertimos String a Enum para usar getFactor()
        // Usamos EnumNivelActividad.valueOf(...)
        double factorActividad = EnumNivelActividad.valueOf(request.getNivelActividad().toUpperCase()).getFactor();
        double tdee = tmb * factorActividad;

        // 3. CORRECCIÓN ERRORES 179-181: Convertimos String a Enum para el switch
        EnumObjetivo objetivo = EnumObjetivo.valueOf(request.getObjetivo().toUpperCase());

        return switch (objetivo) {
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

   private FatSecretRecipeDto getRecetaAleatoria(List<FatSecretRecipeDto> recetas, Set<String> usadas) {

    if (recetas == null || recetas.isEmpty()) return null;

    List<FatSecretRecipeDto> disponibles = recetas.stream()
            .filter(r -> r.getRecipeId() != null && !usadas.contains(r.getRecipeId()))
            .toList();

    if (disponibles.isEmpty()) {
        disponibles = recetas;
    }

    FatSecretRecipeDto receta = disponibles.get(new java.util.Random().nextInt(disponibles.size()));

    if (receta.getRecipeId() != null) {
        usadas.add(receta.getRecipeId());
    }

    return receta;
}
}