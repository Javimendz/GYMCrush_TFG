package com.backend.mapper;

import com.backend.domain.ComidaDiaria;
import com.backend.domain.Nutricion;
import com.backend.domain.SugerenciaDieta;
import com.backend.dto.ComidaDiariaRequestDto;
import com.backend.dto.ComidaDiariaResponseDto;
import com.backend.dto.NutricionResponseDto;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;



@Component
@RequiredArgsConstructor
public class NutricionMapper {

    private final ObjectMapper objectMapper;
    private final ComidaDiariaMapper comidaMapper; // ahora sí se usa

    public NutricionResponseDto toResponseDto(SugerenciaDieta entity) {
        if (entity == null) return null;

        NutricionResponseDto dto = new NutricionResponseDto();
        dto.setId(entity.getId());
        dto.setNombreDieta(entity.getNombreDieta());
        dto.setCaloriasObjetivo(entity.getCaloriasObjetivo());
        dto.setTipoDieta(entity.getTipoDieta());
        dto.setFechaGeneracion(entity.getFechaGeneracion());
        dto.setSuccess(true);

        // Validamos que el JSON sea parseable antes de asignarlo
        if (entity.getMacronutrientesJson() != null && !entity.getMacronutrientesJson().isBlank()) {
            try {
                objectMapper.readTree(entity.getMacronutrientesJson()); // solo validación
                dto.setMacronutrientes(entity.getMacronutrientesJson());
            } catch (Exception e) {
                System.err.println("JSON de macronutrientes inválido: " + e.getMessage());
                dto.setMacronutrientes(null);
            }
        }

        return dto;
    }

    //  Delegamos en ComidaDiariaMapper en lugar de duplicar lógica
    public ComidaDiariaResponseDto toComidaResponse(ComidaDiaria entity) {
        return comidaMapper.toResponseDto(entity);
    }

    public ComidaDiaria toEntity(ComidaDiariaRequestDto request, Nutricion plan) {
        return comidaMapper.toEntity(request, plan);
    }
}