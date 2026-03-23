package com.backend.controller;

import com.backend.dto.ReservaResponseDto;
import com.backend.security.dto.ApiResponseDto;
import com.backend.service.IReservaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reservas")
@RequiredArgsConstructor
public class ReservaController {

    private final IReservaService reservaService;

    // RESERVAR: Cualquier usuario puede apuntarse a un horario
    @PostMapping("/usuario/{uId}/horario/{hId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ApiResponseDto<ReservaResponseDto>> reservar(
            @PathVariable Long uId, 
            @PathVariable Long hId) {
        
        ReservaResponseDto nuevaReserva = reservaService.crearReserva(uId, hId);

        return ResponseEntity.ok(ApiResponseDto.<ReservaResponseDto>builder()
                .mensaje("¡Reserva confirmada! Te esperamos en clase.")
                .success(true)
                .datos(nuevaReserva)
                .build());
    }

    //  MIS RESERVAS: El usuario consulta su historial
    @GetMapping("/usuario/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ApiResponseDto<List<ReservaResponseDto>>> listarMisReservas(@PathVariable Long id) {
        
        List<ReservaResponseDto> misReservas = reservaService.obtenerReservasUsuario(id);

        return ResponseEntity.ok(ApiResponseDto.<List<ReservaResponseDto>>builder()
                .mensaje("Listado de reservas recuperado")
                .success(true)
                .datos(misReservas)
                .build());
    }

    //  CANCELAR: Permite liberar el hueco en la clase
    @PatchMapping("/{id}/cancelar")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ApiResponseDto<Void>> cancelar(@PathVariable Long id) {
        
        reservaService.cancelarReserva(id);

        return ResponseEntity.ok(ApiResponseDto.<Void>builder()
                .mensaje("Reserva cancelada correctamente")
                .success(true)
                .build());
    }
}