package com.backend.mapper;

import com.backend.domain.ComidaDiaria;
import com.backend.domain.Nutricion;
import com.backend.domain.SugerenciaDieta;
import com.backend.dto.ComidaDiariaRequestDto;
import com.backend.dto.ComidaDiariaResponseDto;
import com.backend.dto.NutricionResponseDto;
import com.backend.dto.SugerenciaDietaReponseDto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class NutricionMapper {

   // INYECTA el ObjectMapper de Spring, no hagas "new"
    private final ObjectMapper objectMapper; 
    private final ComidaDiariaMapper comidaMapper;

    public NutricionResponseDto toResponseDto(SugerenciaDieta entity) {
        if (entity == null) return null;

        NutricionResponseDto dto = new NutricionResponseDto();
        dto.setId(entity.getId());
        dto.setNombreDieta(entity.getNombreDieta());
        dto.setCaloriasObjetivo(entity.getCaloriasObjetivo());
        dto.setTipoDieta(entity.getTipoDieta());
        dto.setFechaGeneracion(entity.getFechaGeneracion());
        dto.setSuccess(true);

        try {
            if (entity.getMacronutrientesJson() != null && !entity.getMacronutrientesJson().isBlank()) {
                // Leemos el String y lo convertimos en un JsonNode real
                JsonNode node = objectMapper.readTree(entity.getMacronutrientesJson());
                dto.setMacronutrientes(entity.getMacronutrientesJson());
            }
        } catch (Exception e) {
            System.err.println("Error Jackson: " + e.getMessage());
            dto.setMacronutrientes(null);
        }

        return dto;
    }

  
  

    /**
     * Mapper individual para cada plato de comida
     */
    public ComidaDiariaResponseDto toComidaResponse(ComidaDiaria entity) {
        if (entity == null)
            return null;

        ComidaDiariaResponseDto dto = new ComidaDiariaResponseDto();
        dto.setId(entity.getId());
        dto.setMomento(entity.getMomento());
        dto.setNombreAlimento(entity.getNombreAlimento());
        dto.setCantidadGramos(entity.getCantidadGramos());
        dto.setCalorias(entity.getCalorias());
        dto.setProteina(entity.getProteina());
        dto.setCarbohidratos(entity.getCarbohidratos());
        dto.setGrasas(entity.getGrasas());

        if (entity.getImagenUrl() == null || entity.getImagenUrl().isEmpty()) {
            dto.setImagenUrl("https://images.unsplash.com/photo-1490645935967-10de6ba17061?q=80&w=500");
        } else {
            dto.setImagenUrl(entity.getImagenUrl());
        }

        return dto;
    }

    /**
     * Convierte un Request (del sistema o del usuario) en una Entidad para guardar
     * en DB
     */
    public ComidaDiaria toEntity(ComidaDiariaRequestDto request, Nutricion plan) {
        if (request == null)
            return null;

        ComidaDiaria entity = new ComidaDiaria();
        entity.setMomento(request.getMomento());
        entity.setNombreAlimento(request.getNombreAlimento());
        entity.setCantidadGramos(request.getCantidadGramos());
        entity.setCalorias(request.getCalorias());
        entity.setProteina(request.getProteina());
        entity.setCarbohidratos(request.getCarbohidratos());
        entity.setGrasas(request.getGrasas());
        entity.setPlan(plan); // Establecemos la relación de la clave foránea

        return entity;
    }
}