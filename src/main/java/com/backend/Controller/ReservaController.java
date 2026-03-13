package com.backend.controller;

import com.backend.dto.ReservaResponseDto;
import com.backend.service.IReservaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/reservas")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ReservaController {

    private final IReservaService reservaService;

    @PostMapping("/usuario/{uId}/horario/{hId}")
    public ResponseEntity<?> reservar(@PathVariable Long uId, @PathVariable Long hId) {
        try {
            return ResponseEntity.ok(reservaService.crearReserva(uId, hId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/usuario/{id}")
    public ResponseEntity<List<ReservaResponseDto>> listarMisReservas(@PathVariable Long id) {
        return ResponseEntity.ok(reservaService.obtenerReservasUsuario(id));
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<Void> cancelar(@PathVariable Long id) {
        reservaService.cancelarReserva(id);
        return ResponseEntity.noContent().build();
    }
}