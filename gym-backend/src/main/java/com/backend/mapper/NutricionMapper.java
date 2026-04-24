package com.backend.mapper;

import com.backend.domain.ComidaDiaria;
import com.backend.domain.Nutricion;
import com.backend.dto.ComidaDiariaRequestDto;
import com.backend.dto.ComidaDiariaResponseDto;
import com.backend.dto.NutricionResponseDto;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
import com.backend.dto.FatSecretResponseDto;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class NutricionMapper {

    private final ObjectMapper objectMapper;
    private final ComidaDiariaMapper comidaMapper;

    public NutricionResponseDto toResponseDto(Nutricion entity) {
        if (entity == null)
            return null;

        NutricionResponseDto dto = new NutricionResponseDto();
        dto.setId(entity.getId());
        dto.setCaloriasObjetivo(entity.getCaloriasObjetivo());
        dto.setFechaGeneracion(entity.getFechaGeneracion());
        dto.setNombreDieta(entity.getNombreDieta());
        dto.setTipoDieta(entity.getTipoDieta());

        // Mapear la lista de comidas individuales (Desayuno, Almuerzo, Cena)
        if (entity.getComidas() != null) {
            dto.setComidas(entity.getComidas().stream()
                    .map(comidaMapper::toResponseDto)
                    .collect(Collectors.toList()));
        }

        if (entity.getMacronutrientesJson() != null && !entity.getMacronutrientesJson().isBlank()) {
            try {
                // Usamos el objectMapper inyectado para transformar el String de la DB en el
                // DTO
                FatSecretResponseDto fsDto = objectMapper.readValue(
                        entity.getMacronutrientesJson(),
                        FatSecretResponseDto.class);
                dto.setMacronutrientes(fsDto);
            } catch (Exception e) {
                log.error("Error al deserializar JSON de FatSecret en Nutricion ID {}: {}", entity.getId(),
                        e.getMessage());
                dto.setMacronutrientes(null);
            }
        }
        return dto;
    }

    public List<NutricionResponseDto> toResponseDtoList(List<Nutricion> entities) {
        if (entities == null || entities.isEmpty()) {
            return List.of();
        }
        return entities.stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    public ComidaDiariaResponseDto toComidaResponse(ComidaDiaria entity) {
        return comidaMapper.toResponseDto(entity);
    }

    public ComidaDiaria toEntity(ComidaDiariaRequestDto request, Nutricion plan) {
        return comidaMapper.toEntity(request, plan);
    }
}