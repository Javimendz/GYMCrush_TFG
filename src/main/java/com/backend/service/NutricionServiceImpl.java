package com.backend.service;

import com.backend.domain.*;
import com.backend.dto.*;
import com.backend.exceptions.ResourceNotFoundException;
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

   @Override
    @Transactional
    public SugerenciaDietaReponseDto generarPlanAutomatico(PerfilFisicoRequest request) {
        // 1. Obtención de datos básicos
        Usuario usuario = usuarioRepository.findById(request.getUsuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        Perfil perfil = usuario.getPerfil();
        if (perfil == null || perfil.getFechaNacimiento() == null || perfil.getGenero() == null) {
            throw new RuntimeException("Perfil incompleto: Se requiere fecha de nacimiento y género.");
        }

        int edad = Period.between(perfil.getFechaNacimiento(), LocalDate.now()).getYears();

        // 2. Cálculo de Tasa Metabólica Basal (TMB) - Mifflin-St Jeor
        // TMB = (10 * peso) + (6.25 * altura) - (5 * edad) + s (s=+5 masc, s=-161 fem)
        double tmb = (10 * request.getPeso()) + (6.25 * request.getAltura()) - (5 * edad);
        tmb = (perfil.getGenero().name().equalsIgnoreCase("HOMBRE") || 
               perfil.getGenero().name().equalsIgnoreCase("MASCULINO")) ? tmb + 5 : tmb - 161;

        // 3. Cálculo de Gasto Energético Total (TDEE) usando Factor de Actividad dinámico
        double tdee = tmb * request.getNivelActividad().getFactor();

        // 4. Ajuste por objetivo y selección de categoría
        int kcalObjetivo;
        String categoriaNombre;

        switch (request.getObjetivo()) {
            case GANAR:
                kcalObjetivo = (int) (tdee * 1.10); // Superávit del 10%
                categoriaNombre = "VOLUMEN";
                break;
            case PERDER:
                kcalObjetivo = (int) (tdee * 0.80); // Déficit del 20%
                categoriaNombre = "DEFINICION";
                break;
            default: // MANTENER
                kcalObjetivo = (int) tdee;
                categoriaNombre = "MANTENIMIENTO";
                break;
        }

        // 5. Reparto de Macronutrientes (Basado en peso corporal)
        // Proteína: 2g/kg | Grasas: 0.8g/kg | Carbohidratos: El resto
        double proteinasGramos = request.getPeso() * 2.0;
        double grasasGramos = request.getPeso() * 0.8;
        double carbohidratosGramos = (kcalObjetivo - (proteinasGramos * 4) - (grasasGramos * 9)) / 4;

        String macrosJson = String.format(
            "{\"proteinas\": \"%.1fg\", \"carbohidratos\": \"%.1fg\", \"grasas\": \"%.1fg\"}",
            proteinasGramos, carbohidratosGramos, grasasGramos
        );

        // 6. Vinculación con Dieta Base del catálogo
        Dieta dietaBase = dietaRepository.findByCategoriaDietaNombre(categoriaNombre).stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Catálogo vacío: No hay dietas base para " + categoriaNombre));

        // 7. Persistencia de la Sugerencia Personalizada
        SugerenciaDieta sugerencia = SugerenciaDieta.builder()
                .usuario(usuario)
                .dieta(dietaBase)
                .nombreDieta("Plan " + categoriaNombre + " - " + request.getObjetivo())
                .caloriasObjetivo(kcalObjetivo)
                .tipoDieta(dietaBase.getNombre())
                .macronutrientesJson(macrosJson)
                .fechaGeneracion(LocalDateTime.now())
                .build();

        return sugerenciaMapper.toResponseDto(sugerenciaRepository.save(sugerencia));
    }
}