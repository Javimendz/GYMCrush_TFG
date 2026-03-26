package com.backend.mapper;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

import com.backend.domain.Perfil;
import com.backend.domain.Role;
import com.backend.domain.Usuario;
import com.backend.domain.enums.EnumGenero;
import com.backend.dto.PerfilSummaryDto;
import com.backend.dto.UsuarioRequestDto;
import com.backend.dto.UsuarioResponseDto;
import com.backend.exceptions.*;
import com.backend.repository.RoleRepository;
import com.backend.security.dto.RegisterDto;


/**
 * Mapper abstracto de MapStruct para la entidad {@link Usuario}.
 * <p>
 * Convierte entre la entidad Usuario y sus DTOs de request/response.
 * Incluye métodos personalizados para mapear roles, perfiles y conversiones
 * de tipos de datos complejos.
 * </p>
 * <p>
 * Utiliza {@link RoleMapper} para conversiones de roles y deshabilita
 * el uso de builders para mayor control sobre la creación de objetos.
 * </p>
 *
 * @author Backend Team
 * @version 1.0
 * @since 2026
 */
@Mapper(componentModel = "spring",
        unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE,
        uses = RoleMapper.class,
        builder = @org.mapstruct.Builder(disableBuilder = true))
public abstract class UsuarioMapper {

    @Autowired
    protected RoleRepository roleRepository;

    /**
     * Convierte un RegisterDto a entidad Usuario para registro de nuevos usuarios.
     * Mapea los strings de roles a entidades Role y crea el perfil asociado.
     *
     * @param registerDto el DTO con los datos de registro
     * @return la nueva entidad Usuario lista para guardar
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "rol", source = "rol", qualifiedByName = "mapStringsToRoles")
    @Mapping(target = "perfil", source = ".", qualifiedByName = "mapRegisterDtoToPerfil")
    public abstract Usuario registerDtoToUser(RegisterDto registerDto);

    /**
     * Convierte una entidad Usuario a DTO de respuesta completo.
     * Incluye todos los campos del perfil asociado.
     *
     * @param usuario la entidad a convertir
     * @return el DTO de respuesta con datos completos
     */
    @Mapping(target = "nombre", source = "perfil.nombre")
    @Mapping(target = "apellidos", source = "perfil.apellidos")
    @Mapping(target = "dni", source = "perfil.dni")
    @Mapping(target = "telefono", source = "perfil.telefono")
    @Mapping(target = "foto", source = "perfil.foto")
    @Mapping(target = "bio", source = "perfil.bio")
    @Mapping(target = "direccion", source = "perfil.direccion")
    @Mapping(target = "ciudad", source = "perfil.ciudad")
    @Mapping(target = "pais", source = "perfil.pais")
    @Mapping(target = "codigoPostal", source = "perfil.codigoPostal")
    public abstract UsuarioResponseDto toUsuarioResponseDto(Usuario usuario);

    /**
     * Convierte una entidad Usuario a DTO resumen (solo campos esenciales).
     *
     * @param usuario la entidad a convertir
     * @return el DTO resumen con datos básicos
     */
    @Mapping(target = "username", source = "username")
    @Mapping(target = "nombre", source = "perfil.nombre")
    @Mapping(target = "apellidos", source = "perfil.apellidos")
    @Mapping(target = "foto", source = "perfil.foto")
    public abstract PerfilSummaryDto toSummaryDto(Usuario usuario);

    /**
     * Convierte una lista de entidades Usuario a lista de DTOs resumen.
     *
     * @param usuarios la lista de entidades
     * @return la lista de DTOs resumen
     */
    public abstract List<PerfilSummaryDto> toSummaryDtoList(List<Usuario> usuarios);

    /**
     * Convierte un UsuarioRequestDto a nueva entidad Usuario.
     * Crea el perfil embebido a partir de los campos del request.
     *
     * @param dto el DTO con los datos del nuevo usuario
     * @return la nueva entidad Usuario
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "rol", source = "rol", qualifiedByName = "mapStringsToRoles")
    @Mapping(target = "perfil.nombre", source = "nombre")
    @Mapping(target = "perfil.apellidos", source = "apellidos")
    @Mapping(target = "perfil.dni", source = "dni")
    @Mapping(target = "perfil.telefono", source = "telefono")
    @Mapping(target = "perfil.direccion", source = "direccion")
    @Mapping(target = "perfil.ciudad", source = "ciudad")
    @Mapping(target = "perfil.pais", source = "pais")
    @Mapping(target = "perfil.codigoPostal", source = "codigoPostal")
    @Mapping(target = "perfil.fechaNacimiento", source = "fechaNacimiento")
    @Mapping(target = "perfil.genero", source = "genero")
    @Mapping(target = "perfil", source = ".", qualifiedByName = "mapRequestDtoToPerfil")

    public abstract Usuario toEntity(UsuarioRequestDto dto);

    /**
     * Actualiza una entidad Usuario existente con datos del DTO.
     * Preserva el ID original y la contraseña existente.
     *
     * @param dto el DTO con los datos actualizados
     * @param usuario la entidad a actualizar
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "rol", source = "rol", qualifiedByName = "mapStringsToRoles")
    @Mapping(target = "perfil.nombre", source = "nombre")
    @Mapping(target = "perfil.apellidos", source = "apellidos")
    @Mapping(target = "perfil.dni", source = "dni")
    @Mapping(target = "perfil.telefono", source = "telefono")
    @Mapping(target = "perfil.direccion", source = "direccion")
    @Mapping(target = "perfil.ciudad", source = "ciudad")
    @Mapping(target = "perfil.pais", source = "pais")
    @Mapping(target = "perfil.codigoPostal", source = "codigoPostal")
    @Mapping(target = "perfil.fechaNacimiento", source = "fechaNacimiento")
    @Mapping(target = "perfil.genero", source = "genero")
    public abstract void updateUsuarioFromDto(UsuarioRequestDto dto, @MappingTarget Usuario usuario);

    /**
     * Convierte una lista de entidades Usuario a lista de DTOs de respuesta.
     *
     * @param usuarios la lista de entidades
     * @return la lista de DTOs completos
     */
    public abstract List<UsuarioResponseDto> toUsuarioResponseDtoList(List<Usuario> usuarios);

    /**
     * Mapea un RegisterDto a entidad Perfil.
     * Método auxiliar para crear el perfil durante el registro.
     *
     * @param dto el DTO de registro
     * @return la nueva entidad Perfil
     */
    @Named("mapRegisterDtoToPerfil")
    protected Perfil mapRegisterDtoToPerfil(RegisterDto dto) {
        if (dto == null) return null;
        Perfil perfil = new Perfil();
        perfil.setNombre(dto.getNombre());
        perfil.setApellidos(dto.getApellidos());
        perfil.setDni(dto.getDni());                          // ← campo que causaba el error
        perfil.setTelefono(dto.getTelefono());
        perfil.setDireccion(dto.getDireccion());
        perfil.setCiudad(dto.getCiudad());
        perfil.setPais(dto.getPais());
        perfil.setCodigoPostal(dto.getCodigoPostal());
        perfil.setFechaNacimiento(dto.getFechaNacimiento());
        perfil.setGenero(dto.getGenero());
        return perfil;
    }

    /**
     * Mapea un UsuarioRequestDto a entidad Perfil.
     * Método auxiliar para crear/actualizar el perfil.
     *
     * @param dto el DTO de request
     * @return la nueva entidad Perfil
     */
    @Named("mapRequestDtoToPerfil")
    protected Perfil mapToPerfil(UsuarioRequestDto dto) {
        if (dto == null) return null;
        Perfil perfil = new Perfil();
        perfil.setNombre(dto.getNombre());
        perfil.setApellidos(dto.getApellidos());
        perfil.setDni(dto.getDni());
        perfil.setTelefono(dto.getTelefono());
        perfil.setDireccion(dto.getDireccion());
        perfil.setCiudad(dto.getCiudad());
        perfil.setPais(dto.getPais());
        perfil.setCodigoPostal(dto.getCodigoPostal());
        perfil.setFechaNacimiento(dto.getFechaNacimiento());
        perfil.setGenero(dto.getGenero());
        return perfil;
    }

    /**
     * Convierte un Set de strings (nombres de rol) a Set de entidades Role.
     * Busca cada rol en la base de datos o asigna ROLE_USUARIO por defecto.
     *
     * @param roleNames el conjunto de nombres de rol
     * @return el conjunto de entidades Role
     * @throws ResourceNotFoundException si algún rol no existe en la BD
     */
    @Named("mapStringsToRoles")
    public Set<Role> mapStringsToRoles(Set<String> roleNames) {
        if (roleNames == null || roleNames.isEmpty()) {
            return roleRepository.findByName("ROLE_USUARIO")
                    .map(Collections::singleton)
                    .orElseThrow(() -> new ResourceNotFoundException("Error: Rol por defecto no encontrado"));
        }
        return roleNames.stream()
                .map(name -> roleRepository.findByName(name)
                        .orElseThrow(() -> new ResourceNotFoundException("Role no encontrado: " + name)))
                .collect(Collectors.toSet());
    }

    /**
     * Convierte un string a EnumGenero.
     * Realiza conversión case-insensitive.
     *
     * @param genero el string a convertir
     * @return el valor del enum o null si no es válido
     */
    @Named("stringToEnumGenero")
    public EnumGenero stringToEnumGenero(String genero) {
        if (genero == null) return null;
        try {
            return EnumGenero.valueOf(genero.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}