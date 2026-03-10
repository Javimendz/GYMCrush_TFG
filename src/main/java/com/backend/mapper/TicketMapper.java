package com.backend.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import com.backend.domain.Ticket;
import com.backend.dto.TicketResponseDto;

@Mapper(componentModel = "spring")
public interface TicketMapper {
    TicketResponseDto ticketToTicketResponseDto(Ticket ticket);
    List<TicketResponseDto> ticketToTicketResponseDtoList(List<Ticket> tickets);
}
