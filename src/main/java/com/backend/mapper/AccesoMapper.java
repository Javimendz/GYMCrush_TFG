package com.backend.mapper;

import com.backend.domain.Acceso;
import com.backend.dto.AccesoResponseDto;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class AccesoMapper {

    /**
     * Convierte una Entidad acceso a un dto de respuesta
     */
    public AccesoResponseDto toDto(Acceso acceso) {
        if (acceso == null) return null;

        return AccesoResponseDto.builder()
                .id(acceso.getId())
                .username(acceso.getUsuario().getUsername()) // 👈 Extraemos el nombre del objeto Usuario
                .fechaHoraEntrada(acceso.getFechaHoraEntrada())
                .fechaHoraSalida(acceso.getFechaHoraSalida())
                .tipo(acceso.getTipo())
                .build();
    }

    /**
     * Convierte una lista de Entidades a una lista de dtos
     */
    public List<AccesoResponseDto> toDtoList(List<Acceso> accesos) {
        return accesos.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}