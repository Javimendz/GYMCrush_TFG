package com.backend.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import com.backend.domain.Chat;
import com.backend.dto.ChatResponseDto;

@Mapper(componentModel = "spring")
public interface ChatMapper {
    List<ChatResponseDto> chatToChatResponseDtoList(List<Chat> chats);
}
