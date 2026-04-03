package com.backend.mapper;

import com.backend.domain.ComidaDiaria;
import com.backend.domain.Nutricion;
import com.backend.dto.ComidaDiariaRequestDto;
import com.backend.dto.ComidaDiariaResponseDto;
import com.backend.dto.NutricionResponseDto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class NutricionMapper {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ComidaDiariaMapper comidaMapper; // Inyectas el otro mapper

    /**
     * Convierte la Entidad principal del Plan a un Response DTO completo.
     */
   public NutricionResponseDto toResponse(Nutricion entity) {
        if (entity == null) return null;

        NutricionResponseDto response = new NutricionResponseDto();
        response.setId(entity.getId());
        response.setNombreDieta(entity.getNombreDieta());
        response.setCaloriasObjetivo(entity.getCaloriasObjetivo());
        
        // SIN JACKSON: Pasamos el String tal cual está en la base de datos
        response.setMacronutrientes(entity.getMacronutrientesJson());

        response.setSuccess(true);
        response.setMensaje("¡Plan generado con éxito!");

        if (entity.getComidas() != null) {
            response.setComidas(entity.getComidas().stream()
                .map(comidaMapper::toResponse)
                .collect(Collectors.toList()));
        }

        return response;
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