package com.backend.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.backend.domain.Acceso;
import com.backend.domain.Usuario;
import com.backend.dto.AccesoResponseDto;
import com.backend.mapper.AccesoMapper;
import com.backend.repository.AccesoRepository;
import com.backend.repository.UsuarioRepository;
import com.backend.security.jwt.JwtGenerator;

import jakarta.transaction.Transactional;
import lombok.*;

@Service
@RequiredArgsConstructor
@Builder    
public class AccesoServiceImp  implements IAccesoService{
    
    private final AccesoRepository accesoRepository;
    private final UsuarioRepository usuarioRepository;
    private final JwtGenerator jwtGenerator;
    private final AccesoMapper accesoMapper;

   @Override
    @Transactional
    public void registrarEntrada(String token) {
        if (!jwtGenerator.validateToken(token)) throw new RuntimeException("QR Inválido");

        // Obtener username del token
        String username = jwtGenerator.getUsernameFromToken(token);
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        // Crear registro de entrada
        Acceso entrada = Acceso.builder()
                .usuario(usuario)
                .fechaHoraEntrada(LocalDateTime.now())
                .tipo("ENTRADA")
                .build();

        accesoRepository.save(entrada);
    }

    @Override
    @Transactional
    public void registrarSalida(String token) {
        if (!jwtGenerator.validateToken(token)) throw new RuntimeException("QR Inválido");

        // Obtener username del token
        String username = jwtGenerator.getUsernameFromToken(token);
        
        //  último registro de entrada que no tenga salida aún
        Acceso ultimaEntrada = accesoRepository.findFirstByUsuarioUsernameAndFechaHoraSalidaIsNullOrderByFechaHoraEntradaDesc(username)
                .orElseThrow(() -> new RuntimeException("No se encontró una entrada activa para este usuario"));

        // Actualizar la salida
        ultimaEntrada.setFechaHoraSalida(LocalDateTime.now());
        ultimaEntrada.setTipo("SALIDA");
        
        accesoRepository.save(ultimaEntrada);
    }

    @Override
    public List<AccesoResponseDto> obtenerHistorialUsuario(String username) {
        List<Acceso> listaEntidades = accesoRepository.findByUsuarioUsernameOrderByFechaHoraEntradaDesc(username);
        
        // conversión para el mapper
        return accesoMapper.toDtoList(listaEntidades);
    }

    @Override
    public long obtenerAforoActual(){

        return accesoRepository.countByFechaHoraSalidaIsNull();

    }
}