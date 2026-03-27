package com.backend.dto;

import java.time.LocalDateTime;
import java.time.LocalTime;

import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Data Transfer Object (DTO) para respuestas de entrenamientos.
 * <p>
 * Esta clase representa la estructura de datos devuelta al cliente
 * cuando se solicita información sobre una sesión de entrenamiento
 * programada o completada. Incluye detalles sobre la fecha, hora,
 * duración e intensidad del entrenamiento.
 * </p>
 * 
 * <p>
 * <b>Usos principales:</b>
 * <ul>
 * <li>Confirmación de reservas de entrenamiento</li>
 * <li>Historial de sesiones completadas</li>
 * <li>Planificación de futuras sesiones</li>
 * <li>Seguimiento del progreso deportivo</li>
 * </ul>
 * </p>
 * 
 * <p>
 * <b>Nota sobre tipos de datos:</b>
 * Los campos de tiempo usan LocalDateTime para mayor precisión,
 * aunque en la práctica solo se utilizarán las partes relevantes
 * (fecha, hora, duración).
 * </p>
 *
 * @author Backend Team
 * @version 1.0
 * @since 2026
 */
@Data // Genera automáticamente getters, setters, toString, equals y hashCode
@Schema(description = "DTO para la respuesta de entrenamiento")
public class EntrenamientoResponseDto {

    /**
     * Identificador único del entrenamiento en la base de datos.
     * <p>
     * Es generado automáticamente cuando se programa la sesión
     * y sirve como referencia única para todas las operaciones
     * relacionadas con este entrenamiento específico.
     * </p>
     */
    @Schema(description = "ID único del entrenamiento", example = "1")
    private Long id;

    /**
     * Fecha en que está programado el entrenamiento.
     * <p>
     * Representa el día calendario en que se realizará la sesión.
     * Se utiliza para la planificación y organización del calendario
     * de entrenamientos del usuario.
     * </p>
     * 
     * <p>
     * <b>Formato:</b> LocalDateTime, pero solo se utiliza la parte de fecha.
     * <b>Ejemplo:</b> 2023-12-31T00:00:00 (solo importa 2023-12-31)
     * </p>
     */
    @Schema(description = "Fecha del entrenamiento", example = "2023-12-31")
    private LocalDateTime fecha;

    /**
     * Hora de inicio del entrenamiento.
     * <p>
     * Especifica la hora exacta en que comenzará la sesión.
     * Se utiliza para la programación diaria y para evitar conflictos
     * de horario con otras actividades.
     * </p>
     * 
     * <p>
     * <b>Formato:</b> LocalDateTime, pero solo se utiliza la parte de hora.
     * <b>Ejemplo:</b> 1970-01-01T18:00:00 (solo importa 18:00)
     * </p>
     */
    @Schema(description = "Hora del entrenamiento", example = "18:00")
    private LocalTime hora;

    /**
     * Duración estimada del entrenamiento.
     * <p>
     * Representa el tiempo total que durará la sesión de entrenamiento.
     * Se utiliza para calcular la hora de fin y para la planificación
     * de actividades posteriores.
     * </p>
     * 
     * <p>
     * <b>Formato:</b> LocalDateTime representando la duración desde el inicio.
     * <b>Ejemplo:</b> 1970-01-01T01:00:00 (representa 60 minutos)
     * </p>
     */
    @Schema(description = "Duración del entrenamiento", example = "60")
    private Integer duracion;

    /**
     * Nivel de intensidad del entrenamiento.
     * <p>
     * Clasifica el esfuerzo requerido para la sesión, ayudando
     * a los usuarios a seleccionar entrenamientos apropiados
     * para su nivel de condición física y objetivos.
     * </p>
     * 
     * <p>
     * <b>Niveles disponibles:</b>
     * <ul>
     * <li><b>Baja</b>: Ideal para principiantes o días de recuperación</li>
     * <li><b>Media</b>: Nivel intermedio para mantenimiento general</li>
     * <li><b>Alta</b>: Para usuarios avanzados y mejora de rendimiento</li>
     * </ul>
     * </p>
     */
    @Schema(description = "Intensidad del entrenamiento (baja, media, alta)", example = "Media")
    private String intensidad;

    private String nombreUsuario;
    @Schema(description = "Número de ejercicios en esta sesión")
    private Integer cantidadEjercicios;
    private Long usuarioId;
}
