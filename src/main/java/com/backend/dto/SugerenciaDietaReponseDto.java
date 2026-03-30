package com.backend.dto;

import java.time.LocalDateTime;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * Data Transfer Object (DTO) para respuestas de sugerencias de dieta.
 * <p>
 * Esta clase representa la estructura de datos devuelta al cliente
 * cuando se genera una recomendación dietética personalizada.
 * Incluye información nutricional completa y metadatos
 * de generación para seguimiento y auditoría.
 * </p>
 * 
 * <p>
 * <b>Información nutricional incluida:</b>
 * <ul>
 *   <li><b>Calorías objetivo:</b> Total diario recomendado</li>
 *   <li><b>Macronutrientes:</b> Proteínas, carbohidratos, grasas</li>
 *   <li><b>Tipo de dieta:</b> Categoría (vegana, keto, etc.)</li>
 *   <li><b>Nombre descriptivo:</b> Título de la recomendación</li>
 * </ul>
 * </p>
 * 
 * <p>
 * <b>Características especiales:</b>
 * <ul>
 *   <li>Macronutrientes en formato JSON para flexibilidad</li>
 *   <li>Timestamp de generación para seguimiento</li>
 *   <li>Personalización basada en datos del usuario</li>
 *   <li>Integración con planes de entrenamiento</li>
 * </ul>
 * </p>
 * 
 * <p>
 * <b>Proceso de generación:</b>
 * <ol>
 *   <li>Análisis de datos de salud del usuario (peso, estatura, IMC)</li>
 *   <li>Evaluación de nivel de actividad y objetivos</li>
 *   <li>Cálculo de necesidades calóricas y macronutrientes</li>
 *   <li>Generación de plan dietético personalizado</li>
 *   <li>Almacenamiento como sugerencia para consulta futura</li>
 * </ol>
 * </p>
 * 
 * <p>
 * <b>Usos principales:</b>
 * <ul>
 *   <li>Recomendaciones nutricionales personalizadas</li>
 *   <li>Planes de alimentación para objetivos específicos</li>
 *   <li>Integración con aplicaciones de seguimiento</li>
 *   <li>Consultas históricas de recomendaciones</li>
 * </ul>
 * </p>
 *
 * @author Backend Team
 * @version 1.0
 * @since 2026
 */
@Data // Genera automáticamente getters, setters, toString, equals y hashCode
@Schema(description = "DTO para representar la respuesta de una sugerencia de dieta")
public class SugerenciaDietaReponseDto {
    
    /**
     * Identificador único de la sugerencia de dieta.
     * <p>
     * Es generado automáticamente cuando se crea la recomendación
     * y sirve como referencia única para esta sugerencia específica.
     * </p>
     */
    @Schema(description = "Identificador único de la sugerencia de dieta")
    private Long id;
    
    /**
     * Nombre descriptivo de la dieta sugerida.
     * <p>
     * Título que identifica la recomendación dietética.
     * Debe ser claro y descriptivo para que el usuario
     * entienda rápidamente el propósito del plan.
     * </p>
     * 
     * <p>
     * <b>Ejemplos:</b>
     * <ul>
     *   <li>"Plan de pérdida de peso - 2kg por mes"</li>
     *   <li>"Dieta de mantenimiento para nivel activo"</li>
     *   <li>"Plan de volumen muscular - fase de bulking"</li>
     *   <li>"Dieta vegana para deportistas"</li>
     * </ul>
     * </p>
     */
    @Schema(description = "Nombre de la dieta sugerida")
    private String nombreDieta;
    
@Schema(description = "ID del usuario al que pertenece la sugerencia")
    private Long usuarioId;

    @Schema(description = "ID de la dieta base utilizada para esta sugerencia")
    private Long dietaId;

    /**
     * Calorías objetivo diarias para la dieta sugerida.
     * <p>
     * Total de calorías que el usuario debería consumir
     * diariamente según sus objetivos, peso, estatura
     * y nivel de actividad.
     * </p>
     * 
     * <p>
     * <b>Cálculo base:</b>
     * <ul>
     *   <li>Tasa Metabólica Basal (TMB)</li>
     *   <li>Factor de actividad (1.2-2.0)</li>
     *   <li>Objetivo (déficit/superávit calórico)</li>
     * </ul>
     * </p>
     * 
     * <p>
     * <b>Rangos típicos:</b>
     * <ul>
     *   <li>Pérdida de peso: 1500-2000 kcal</li>
     *   <li>Mantenimiento: 2000-2500 kcal</li>
     *   <li>Volumen: 2500-4000+ kcal</li>
     * </ul>
     * </p>
     */
    @Schema(description = "Calorías objetivo para la dieta sugerida")
    private Integer caloriasObjetivo;
    
    /**
     * Macronutrientes recomendados en formato JSON.
     * <p>
     * Distribución detallada de proteínas, carbohidratos y grasas
     * necesaria para alcanzar los objetivos calóricos y de composición
     * corporal. Formato JSON permite flexibilidad y extensibilidad.
     * </p>
     * 
     * <p>
     * <b>Estructura JSON típica:</b>
     * <pre>
     * {
     *   "proteinas": {
     *     "gramos": 150,
     *     "calorias": 600,
     *     "porcentaje": 30
     *   },
     *   "carbohidratos": {
     *     "gramos": 250,
     *     "calorias": 1000,
     *     "porcentaje": 50
     *   },
     *   "grasas": {
     *     "gramos": 55,
     *     "calorias": 500,
     *     "porcentaje": 20
     *   }
     * }
     * </pre>
     * </p>
     * 
     * <p>
     * <b>Proporciones comunes:</b>
     * <ul>
     *   <li>Pérdida de peso: 40% proteína, 40% carbohidratos, 20% grasas</li>
     *   <li>Mantenimiento: 30% proteína, 50% carbohidratos, 20% grasas</li>
     *   <li>Volumen: 25% proteína, 55% carbohidratos, 20% grasas</li>
     * </ul>
     * </p>
     */
    @Schema(description = "Macronutrientes recomendados para la dieta en formato JSON")
    private String macronutrientesJson;
    
    /**
     * Tipo o categoría de dieta sugerida.
     * <p>
     * Clasificación de la dieta según restricciones alimentarias
     * o filosofía nutricional. Ayuda al usuario a identificar
     * rápidamente si el plan se adapta a sus necesidades.
     * </p>
     * 
     * <p>
     * <b>Tipos comunes:</b>
     * <ul>
     *   <li><b>OMNÍVORA</b>: Incluye todos los grupos de alimentos</li>
     *   <li><b>VEGETARIANA</b>: Sin carne, puede incluir lácteos y huevos</li>
     *   <li><b>VEGANA</b>: Solo productos de origen vegetal</li>
     *   <li><b>KETO</b>: Muy baja en carbohidratos, alta en grasas</li>
     *   <li><b>MEDITERRÁNEA</b>: Rica en grasas saludables y pescado</li>
     *   <li><b>PALEO</b>: Basada en alimentos no procesados</li>
     *   <li><b>SIN GLUTEN</b>: Excluye productos con trigo</li>
     * </ul>
     * </p>
     */
    @Schema(description = "Tipo de dieta sugerida (e.g., vegetariana, vegana, etc.)")
    private String tipoDieta;
    
    /**
     * Fecha y hora en que se generó la sugerencia de dieta.
     * <p>
     * Timestamp automático que registra cuándo se creó esta recomendación.
     * Es útil para seguimiento, auditoría y para identificar
     * la versión más reciente de las sugerencias del usuario.
     * </p>
     * 
     * <p>
     * <b>Formato:</b> ISO 8601 (yyyy-MM-ddTHH:mm:ss)
     * <b>Ejemplo:</b> 2024-12-25T14:30:00
     * </p>
     * 
     * <p>
     * <b>Usos:</b>
     * <ul>
     *   <li>Identificar la recomendación más reciente</li>
     *   <li>Seguimiento de evolución de sugerencias</li>
     *   <li>Análisis de efectividad de planes anteriores</li>
     * </ul>
     * </p>
     */
    @Schema(description = "Fecha en la que se generó la sugerencia de dieta")
    private LocalDateTime fechaGeneracion;
}
