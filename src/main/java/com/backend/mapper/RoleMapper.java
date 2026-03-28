package com.backend.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.backend.domain.Role;
import com.backend.dto.RoleDto;




@Mapper(componentModel = "spring")
public interface RoleMapper {

    @Mapping(target = "name", source = "name")
    
    RoleDto toDto(Role role);

   @Mapping(target = "name", source = "name")
    @Mapping(target = "usuarios", ignore = true)
    Role toEntity(RoleDto roleDto);

    List<RoleDto> toDtoList(List<Role> roles); // Para userResponseDto
    
}
