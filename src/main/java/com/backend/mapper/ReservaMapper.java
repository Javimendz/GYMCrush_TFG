//Paquete
package com.backend.mapper;
//Imports
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.backend.domain.Reserva;
import com.backend.dto.ReservaResponseDto;

@Mapper(componentModel = "spring")
public interface ReservaMapper {

    @Mapping(target = "username", source = "usuario.username")
    @Mapping(target = "nombreActividad", source = "horario.actividad.nombre")
    @Mapping(target = "diaSemana", source = "horario.diaSemana")
    @Mapping(target = "horaInicio", source = "horario.horaInicio")
    ReservaResponseDto toDto(Reserva reserva);

    java.util.List<ReservaResponseDto> toDtoList(java.util.List<Reserva> reservas);
}