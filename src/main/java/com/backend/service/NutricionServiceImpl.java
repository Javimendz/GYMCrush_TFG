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
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;

@Slf4j
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
        validarDatosFisicos(request);

        Usuario usuario = usuarioRepository.findById(request.getUsuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + request.getUsuarioId()));

        int kcalObjetivo = calcularCalorias(request, usuario.getPerfil());
        String categoriaNombre = resolverCategoria(request.getObjetivo());

        double proteinasGramos = request.getPeso() * 2.0;
        double grasasGramos    = request.getPeso() * 0.8;
        double carbosGramos    = (kcalObjetivo - (proteinasGramos * 4) - (grasasGramos * 9)) / 4;

        String macrosJson = buildMacrosJson(proteinasGramos, carbosGramos, grasasGramos);
        String menuFinal  = obtenerMenu(kcalObjetivo, proteinasGramos, carbosGramos, grasasGramos, macrosJson);

        Dieta dietaBase = dietaRepository.findByCategoriaDietaNombre(categoriaNombre)
                .stream().findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Catálogo vacío para: " + categoriaNombre));

        SugerenciaDieta sugerencia = SugerenciaDieta.builder()
                .usuario(usuario)
                .dieta(dietaBase)
                .nombreDieta("Plan " + categoriaNombre)
                .caloriasObjetivo(kcalObjetivo)
                .tipoDieta(dietaBase.getNombre())
                .macronutrientesJson(menuFinal)
                .fechaGeneracion(LocalDateTime.now())
                .build();

        return sugerenciaMapper.toResponseDto(sugerenciaRepository.save(sugerencia));
    }

    @Override
    public NutricionResponseDto obtenerUltimoPlanPorUsuario(Long usuarioId) {
        return sugerenciaRepository.findFirstByUsuarioIdOrderByIdDesc(usuarioId)
                .map(nutricionMapper::toResponseDto)
                .orElseThrow(() -> new ResourceNotFoundException("No hay planes para el usuario: " + usuarioId));
    }

    // -----------------------------------------------------------------------
    // Métodos privados
    // -----------------------------------------------------------------------

    private void validarDatosFisicos(PerfilFisicoRequest request) {
        if (request.getPeso() <= 0 || request.getAltura() <= 0) {
            throw new IllegalArgumentException("Peso y altura deben ser mayores que cero");
        }
    }

    private int calcularCalorias(PerfilFisicoRequest request, Perfil perfil) {
        int edad = Period.between(perfil.getFechaNacimiento(), LocalDate.now()).getYears();
        boolean esHombre = perfil.getGenero().name().matches("(?i)HOMBRE|MASCULINO");

        double tmb  = (10 * request.getPeso()) + (6.25 * request.getAltura()) - (5 * edad) + (esHombre ? 5 : -161);
        double tdee = tmb * request.getNivelActividad().getFactor();

        return switch (request.getObjetivo()) {
            case GANAR  -> (int) (tdee * 1.10);
            case PERDER -> (int) (tdee * 0.80);
            case MANTENER -> (int) tdee;
            
        };
    }

    private String resolverCategoria(EnumObjetivo objetivo) {
        return switch (objetivo) {
            case GANAR    -> "VOLUMEN";
            case PERDER   -> "DEFINICION";
            case MANTENER -> "MANTENIMIENTO";
        };
    }

    private String buildMacrosJson(double proteinas, double carbos, double grasas) {
        return String.format(
            "{\"proteinas\": \"%.1fg\", \"carbohidratos\": \"%.1fg\", \"grasas\": \"%.1fg\"}",
            proteinas, carbos, grasas
        );
    }

    private String obtenerMenu(int kcal, double prot, double carbos, double grasas, String fallback) {
        try {
            String resultado = fatSecretService.buscarRecetasPorMacros(kcal, (int) prot, (int) carbos, (int) grasas);
            //   FatSecretService lanza excepción si hay error, pero por si acaso validamos también el contenido de la respuesta
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