//Paquete
package com.backend.mapper;
//Imports
import java.util.List;
import org.mapstruct.Mapper;
import com.backend.domain.Reserva;
import com.backend.dto.ReservaResponseDto;

@Mapper(componentModel = "spring")
public interface ReservaMapper {

    List<ReservaResponseDto> reservaToReservaResponseDtoList(List<Reserva> reservas);
}
