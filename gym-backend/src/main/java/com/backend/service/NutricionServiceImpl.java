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
    public NutricionResponseDto generarPlanAutomatico(NutricionRequestDto request) { // 1. Validaciones previas
        validarDatosFisicos(request);

        Usuario usuario = usuarioRepository.findById(request.getUsuarioId())
                .orElseThrow(
                        () -> new ResourceNotFoundException("Usuario no encontrado con ID: " + request.getUsuarioId()));

        // 2. Determinar edad y género (fallback si el perfil no existe)
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

        // 3. Cálculos Nutricionales (Cálculo de kcal con corrección de CM)
        int kcalObjetivo = calcularCalorias(request, edad, genero);
        String categoriaNombre = resolverCategoria(EnumObjetivo.valueOf(request.getObjetivo().toUpperCase()));
        double proteinasGramos = request.getPeso() * 2.0;
        double grasasGramos = request.getPeso() * 0.8;
        double caloriasRestantes = kcalObjetivo - (proteinasGramos * 4) - (grasasGramos * 9);
        double carbosGramos = Math.max(0, caloriasRestantes / 4.0);

        // 4. Obtener sugerencia de la API externa (FatSecret)
        String macrosJsonFallback = buildMacrosJson(proteinasGramos, carbosGramos, grasasGramos);
        String menuJsonRaw = obtenerMenu(kcalObjetivo, proteinasGramos, carbosGramos, grasasGramos, macrosJsonFallback);

        // 5. Extraer nombre e imagen de la primera receta usando tus DTOs
        String nombreSugerido = "Sugerencia según macros";
        String imagenSugerida = "https://images.unsplash.com/photo-1490645935967-10de6ba17061?q=80&w=500";

        try {
            // Convertimos el String de FatSecret al objeto DTO para extraer datos
            // fácilmente
            FatSecretResponseDto fsData = objectMapper.readValue(menuJsonRaw, FatSecretResponseDto.class);
            if (fsData.getRecipes() != null && fsData.getRecipes().getRecipe() != null
                    && !fsData.getRecipes().getRecipe().isEmpty()) {
                FatSecretRecipeDto primeraReceta = fsData.getRecipes().getRecipe().get(0);
                nombreSugerido = primeraReceta.getRecipeName();
                imagenSugerida = primeraReceta.getRecipeImage();
            }
        } catch (Exception e) {
            log.warn("No se pudo mapear el DTO de FatSecret, usando valores por defecto: {}", e.getMessage());
        }

        // 6. Obtener la dieta base del catálogo (para el tipo de dieta)
        Dieta dietaBase = dietaRepository.findByCategoriaDietaNombre(categoriaNombre)
                .stream().findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Catálogo vacío para: " + categoriaNombre));

        // 7. Construir y guardar el Plan de Nutrición
        Nutricion nuevoPlan = Nutricion.builder()
                .usuario(usuario)
                .nombreDieta("Plan " + categoriaNombre)
                .tipoDieta(dietaBase.getNombre())
                .caloriasObjetivo(kcalObjetivo)
                .macronutrientesJson(menuJsonRaw) // Se guarda como String en la DB
                .fechaGeneracion(LocalDateTime.now())
                .build();

        // 8. Crear las Comidas Diarias (Desayuno 30%, Almuerzo 40%, Cena 30%)
        double pDesayunoCena = 0.30;
        double pAlmuerzo = 0.40;

        ComidaDiaria desayuno = crearComida("Desayuno", nombreSugerido, imagenSugerida, kcalObjetivo, proteinasGramos,
                carbosGramos, grasasGramos, pDesayunoCena, nuevoPlan);
        ComidaDiaria almuerzo = crearComida("Almuerzo", nombreSugerido, imagenSugerida, kcalObjetivo, proteinasGramos,
                carbosGramos, grasasGramos, pAlmuerzo, nuevoPlan);
        ComidaDiaria cena = crearComida("Cena", nombreSugerido, imagenSugerida, kcalObjetivo, proteinasGramos,
                carbosGramos, grasasGramos, pDesayunoCena, nuevoPlan);

        nuevoPlan.setComidas(List.of(desayuno, almuerzo, cena));

        // 9. Persistir y Retornar
        Nutricion planGuardado = nutricionRepository.save(nuevoPlan);
        log.info("Plan generado y guardado ID: {} para usuario: {}", planGuardado.getId(), usuario.getId());

        return nutricionMapper.toResponseDto(planGuardado);
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
}