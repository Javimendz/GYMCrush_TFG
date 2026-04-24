package com.backend.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.domain.DetallePlan;
import com.backend.dto.DetallePlanResponseDto;
import com.backend.mapper.DetallePlanMapper;
import com.backend.repository.DetallePlanRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class DetallePlanServiceImp implements IDetallePlanService {

    private final DetallePlanRepository detalleRepository;
    private final DetallePlanMapper detalleMapper;

    @Override
    @Transactional(readOnly = true)
    public List<DetallePlanResponseDto> listarPorPlan(Long planId) {
        log.info("Obteniendo todos los ejercicios del plan {}", planId);

        return detalleRepository.findByPlanIdOrderByDiaSemanaAscOrdenAsc(planId)
                .stream()
                .map(detalleMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void eliminarEjercicio(Long detalleId) {
        log.info("Eliminando ejercicio del detalle ID: {}", detalleId);
        if (!detalleRepository.existsById(detalleId)) {
            throw new RuntimeException("No se encontró el detalle del plan con ID: " + detalleId);
        }
        detalleRepository.deleteById(detalleId);
    }

    @Override
    @Transactional
    public DetallePlanResponseDto actualizarOrden(Long detalleId, Integer nuevoDia, Integer nuevoOrden) {
        log.info("Actualizando orden del detalle {}: Día {}, Orden {}", detalleId, nuevoDia, nuevoOrden);

        DetallePlan detalle = detalleRepository.findById(detalleId)
                .orElseThrow(() -> new RuntimeException("Detalle no encontrado"));

        detalle.setDiaSemana(nuevoDia);
        detalle.setOrden(nuevoOrden);

        DetallePlan guardado = detalleRepository.save(detalle);
        return detalleMapper.toResponseDto(guardado);
    }
}