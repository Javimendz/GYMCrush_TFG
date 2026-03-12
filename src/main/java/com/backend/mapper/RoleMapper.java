package com.backend.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.backend.domain.Role;
import com.backend.dto.RoleDto;



@Mapper(componentModel = "spring")
public interface RoleMapper {

    RoleDto toDto(Role roleDto);
    Role toEntity(RoleDto roleDto);

    List<RoleDto> toDtoList(List<Role> roles); // Para userResponseDto
    
}
