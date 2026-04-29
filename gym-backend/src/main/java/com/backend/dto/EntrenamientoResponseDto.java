package com.backend.dto;

import lombok.Builder; 
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
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EntrenamientoResponseDto {

    /**
     * Identificador único del entrenamiento en la base de datos.
     * <p>
     * Es generado automáticamente cuando se programa la sesión
     * y sirve como referencia única para todas las operaciones
     * relacionadas con este entrenamiento específico.
     * </p>
     */
    /**
     * Identificador único del entrenamiento en el catálogo.
     */
    @Schema(description = "ID único del entrenamiento en el catálogo", example = "1")
    private Long id;

    /**
     * Nombre comercial o técnico del entrenamiento.
     */
    @Schema(description = "Nombre del entrenamiento", example = "Día de Pecho y Tríceps")
    private String nombre;

    /**
     * Descripción detallada de lo que incluye el entrenamiento.
     */
    @Schema(description = "Descripción detallada", example = "Enfocado en hipertrofia muscular. 4 series por ejercicio.")
    private String descripcion;

    /**
     * Duración estimada del entrenamiento.
     */
    @Schema(description = "Duración del entrenamiento en minutos", example = "60")
    private Integer duracion;

    /**
     * Nivel de intensidad del entrenamiento.
     */
    @Schema(description = "Intensidad del entrenamiento (baja, media, alta)", example = "ALTA")
    private String intensidad;

    /**
     * Cantidad de ejercicios distintos que componen esta rutina base.
     */
    @Schema(description = "Número de ejercicios en esta sesión maestra", example = "6")
    private Integer cantidadEjercicios;

    @Schema(description = "URL del video demostrativo del entrenamiento", example = "https://www.youtube.com/watch?v=example")
    private String urlVideo; 
  
}