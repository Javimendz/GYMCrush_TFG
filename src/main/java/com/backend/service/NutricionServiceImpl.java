package com.backend.service;

import com.backend.domain.*;
import com.backend.dto.*;
import com.backend.mapper.SugerenciaMapper;
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
    private final SugerenciaMapper sugerenciaMapper;

    @Override
    @Transactional
    public SugerenciaDietaReponseDto generarPlanAutomatico(PerfilFisicoRequest request) {
        // 1. Obtener Usuario y su Perfil para cálculos (Edad/Género)
        Usuario usuario = usuarioRepository.findById(request.getUsuarioId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        Perfil perfil = usuario.getPerfil();
        int edad = Period.between(perfil.getFechaNacimiento(), LocalDate.now()).getYears();

        // 2. Lógica del Motor: Ecuación de Harris-Benedict
        double tmb;
        if (perfil.getGenero().name().equals("MASCULINO")) {
            tmb = (10 * request.getPeso()) + (6.25 * request.getAltura()) - (5 * edad) + 5;
        } else {
            tmb = (10 * request.getPeso()) + (6.25 * request.getAltura()) - (5 * edad) - 161;
        }

        // 3. Ajuste por objetivo y determinar categoría
        int kcalObjetivo;
        String categoriaBuscada;
        
        if ("GANAR".equalsIgnoreCase(request.getObjetivo())) {
            kcalObjetivo = (int) (tmb * 1.15); // Superávit
            categoriaBuscada = "VOLUMEN";
        } else if ("PERDER".equalsIgnoreCase(request.getObjetivo())) {
            kcalObjetivo = (int) (tmb * 0.85); // Déficit
            categoriaBuscada = "DEFINICION";
        } else {
            kcalObjetivo = (int) tmb;
            categoriaBuscada = "MANTENIMIENTO";
        }

        // 4. Buscar una dieta base del catálogo que coincida con la categoría
        Dieta dietaBase = dietaRepository.findByCategoriaDietaNombre(categoriaBuscada).stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No hay dietas base para " + categoriaBuscada));

        // 5. Crear la Sugerencia final usando el Builder de la entidad
        SugerenciaDieta sugerencia = SugerenciaDieta.builder()
                .usuario(usuario)
                .dieta(dietaBase)
                .nombreDieta("Plan " + request.getObjetivo() + " Inteligente")
                .caloriasObjetivo(kcalObjetivo)
                .tipoDieta(dietaBase.getNombre())
                .macronutrientesJson("Cálculo para peso: " + request.getPeso() + "kg. Objetivo: " + kcalObjetivo + " kcal.")
                .fechaGeneracion(LocalDateTime.now())
                .build();

        // 6. Persistir y devolver el DTO
        return sugerenciaMapper.toResponseDto(sugerenciaRepository.save(sugerencia));
    }
}