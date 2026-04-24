//Paquete
package com.backend.mapper;

//Imports
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.backend.domain.Reserva;
import com.backend.dto.ReservaResponseDto;

/**
 * Mapper de MapStruct para la entidad {@link Reserva}.
 * <p>
 * Convierte entre la entidad Reserva y sus DTOs de respuesta.
 * Incluye mapeos personalizados para extraer información del usuario y horario
 * asociados.
 * </p>
 *
 * @author Backend Team
 * @version 1.0
 * @since 2026
 */
@Mapper(componentModel = "spring")
public interface ReservaMapper {

    /**
     * Convierte una entidad Reserva a DTO de respuesta.
     * Extrae username, nombre de actividad, día de la semana y hora de inicio
     * navegando por las relaciones usuario y horario.
     *
     * @param reserva la entidad a convertir
     * @return el DTO de respuesta con datos enriquecidos
     */
    @Mapping(target = "username", source = "usuario.username")
    @Mapping(target = "nombreActividad", source = "horario.actividad.nombre")
    @Mapping(target = "diaSemana", source = "horario.diaSemana")
    @Mapping(target = "horaInicio", source = "horario.horaInicio")
    ReservaResponseDto toDto(Reserva reserva);

    /**
     * Convierte una lista de entidades Reserva a lista de DTOs de respuesta.
     *
     * @param reservas la lista de entidades
     * @return la lista de DTOs
     */
    java.util.List<ReservaResponseDto> toDtoList(java.util.List<Reserva> reservas);
}