package com.backend.service;

import com.backend.domain.*;
import com.backend.domain.enums.EnumObjetivo;
import com.backend.domain.enums.EnumNivelActividad;
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
    
    
    private final FatSecretService fatSecretService;

    @Override
    @Transactional
    public SugerenciaDietaReponseDto generarPlanAutomatico(PerfilFisicoRequest request) {
        //  Obtención de datos
        Usuario usuario = usuarioRepository.findById(request.getUsuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        Perfil perfil = usuario.getPerfil();
        if (perfil == null || perfil.getFechaNacimiento() == null || perfil.getGenero() == null) {
            throw new RuntimeException("Perfil incompleto: Se requiere fecha de nacimiento y género.");
        }

        int edad = Period.between(perfil.getFechaNacimiento(), LocalDate.now()).getYears();

        //  Tasa Metabólica Basal
        double tmb = (10 * request.getPeso()) + (6.25 * request.getAltura()) - (5 * edad);
        tmb = (perfil.getGenero().name().equalsIgnoreCase("HOMBRE") || 
               perfil.getGenero().name().equalsIgnoreCase("MASCULINO")) ? tmb + 5 : tmb - 161;

        //  Gasto Energético Total (TDEE)
        double tdee = tmb * request.getNivelActividad().getFactor();

        int kcalObjetivo;
        String categoriaNombre;

        //  Lógica del Plan
        switch (request.getObjetivo()) {
            case GANAR: 
                kcalObjetivo = (int) (tdee * 1.10);
                categoriaNombre = "VOLUMEN";
                break;
            case PERDER:
                kcalObjetivo = (int) (tdee * 0.80);
                categoriaNombre = "DEFINICION";
                break;
            default: // MANTENER
                kcalObjetivo = (int) tdee;
                categoriaNombre = "MANTENIMIENTO";
                break;
        }

        //  Macronutrientes Exactos Calculados
        double proteinasGramos = request.getPeso() * 2.0;
        double grasasGramos = request.getPeso() * 0.8;
        double carbohidratosGramos = (kcalObjetivo - (proteinasGramos * 4) - (grasasGramos * 9)) / 4;

        //Tratamos la llamada a la api de fastsecret con un try catch para evitar que si falla la api, el usuario se quede sin dieta
        //  Si la api falla, usamos los macros matemáticos para que no se quede sin dieta   


       String macrosJson = String.format(
            "{\"proteinas\": \"%.1fg\", \"carbohidratos\": \"%.1fg\", \"grasas\": \"%.1fg\"}",
            proteinasGramos, carbohidratosGramos, grasasGramos
        );

        String menuRealJson;
        try {
            // Plan A: Intentamos buscar comida real en internet
            menuRealJson = fatSecretService.buscarRecetasPorMacros(
                    kcalObjetivo, (int) proteinasGramos, (int) carbohidratosGramos, (int) grasasGramos
            );
        } catch (Exception e) {
        // Cambia el System.err por esto para ver TODO el rastro del error
        e.printStackTrace(); 
        System.err.println("⚠️ Detalle del error: " + e.getMessage());
        menuRealJson = macrosJson;
    }

        //  Buscar Dieta Base
        Dieta dietaBase = dietaRepository.findByCategoriaDietaNombre(categoriaNombre).stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Catálogo vacío para " + categoriaNombre));

        //  Guardar Sugerencia
        SugerenciaDieta sugerencia = SugerenciaDieta.builder()
                .usuario(usuario)
                .dieta(dietaBase)
                .nombreDieta("Plan " + categoriaNombre)
                .caloriasObjetivo(kcalObjetivo)
                .tipoDieta(dietaBase.getNombre())
                // GUARDAMOS EL MENÚ REAL DE LA API EN VEZ DE SOLO EL STRING FORMAT
                .macronutrientesJson(menuRealJson) 
                .fechaGeneracion(LocalDateTime.now())
                .build();

        return sugerenciaMapper.toResponseDto(sugerenciaRepository.save(sugerencia));
    }
}