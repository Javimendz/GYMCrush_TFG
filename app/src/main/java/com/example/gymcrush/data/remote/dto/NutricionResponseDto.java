package com.example.gymcrush.data.remote.dto;

import com.google.gson.annotations.SerializedName;
import com.google.gson.Gson;
import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

public class NutricionResponseDto implements Serializable {
    @SerializedName("id")
    private Long id;

    @SerializedName("tipoDieta")
    private String tipoDieta;

    @SerializedName("fechaGeneracion")
    private String fechaGeneracion;

    @SerializedName("nombreDieta")
    private String nombreDieta;

    @SerializedName("caloriasObjetivo")
    private Integer caloriasObjetivo;

    @SerializedName("macronutrientes")
    private Object macronutrientes;

    @SerializedName("dietas")
    private List<ComidaDiariaResponseDto> dietas;

    @SerializedName("comidas")
    private List<ComidaDiariaResponseDto> comidas;

    // Getters
    public Long getId() { return id; }
    public String getTipoDieta() { return tipoDieta; }
    public String getFechaGeneracion() { return fechaGeneracion; }
    public String getNombreDieta() { return nombreDieta; }
    public Integer getCaloriasObjetivo() { return caloriasObjetivo; }
    public Object getMacronutrientes() { return macronutrientes; }
    public List<ComidaDiariaResponseDto> getDietas() { return dietas; }
    public List<ComidaDiariaResponseDto> getComidas() { return comidas; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setTipoDieta(String tipoDieta) { this.tipoDieta = tipoDieta; }
    public void setFechaGeneracion(String fechaGeneracion) { this.fechaGeneracion = fechaGeneracion; }
    public void setNombreDieta(String nombreDieta) { this.nombreDieta = nombreDieta; }
    public void setCaloriasObjetivo(Integer caloriasObjetivo) { this.caloriasObjetivo = caloriasObjetivo; }
    public void setMacronutrientes(Object macronutrientes) { this.macronutrientes = macronutrientes; }
    public void setDietas(List<ComidaDiariaResponseDto> dietas) { this.dietas = dietas; }
    public void setComidas(List<ComidaDiariaResponseDto> comidas) { this.comidas = comidas; }

    /**
     * Devuelve las sugerencias del plan. 
     * Prioriza 'dietas' (si existe en el JSON) y luego 'comidas' (donde el backend suele enviarlas).
     */
    public List<ComidaDiariaResponseDto> getSugerencias() {
        if (dietas != null && !dietas.isEmpty()) return dietas;
        if (comidas != null && !comidas.isEmpty()) return comidas;
        return new ArrayList<>();
    }

    /**
     * Parsea los macronutrientes del plan.
     * El backend los envía como un JSON string o un objeto con campos "proteinas", "carbohidratos", "grasas".
     */
    public ParsedMacros getParsedMacros() {
        if (macronutrientes == null) {
            // Si macronutrientes es null, intentamos sumar los de las sugerencias/comidas
            ParsedMacros sum = new ParsedMacros();
            List<ComidaDiariaResponseDto> sugerencias = getSugerencias();
            if (sugerencias != null && !sugerencias.isEmpty()) {
                double p = 0, c = 0, g = 0;
                for (ComidaDiariaResponseDto item : sugerencias) {
                    if (item.getProteina() != null) p += item.getProteina();
                    if (item.getCarbohidratos() != null) c += item.getCarbohidratos();
                    if (item.getGrasas() != null) g += item.getGrasas();
                }
                sum.proteinas = String.valueOf(p);
                sum.carbohidratos = String.valueOf(c);
                sum.grasas = String.valueOf(g);
            }
            return sum;
        }
        
        try {
            String json;
            if (macronutrientes instanceof String) {
                json = (String) macronutrientes;
            } else {
                json = new Gson().toJson(macronutrientes);
            }
            return new Gson().fromJson(json, ParsedMacros.class);
        } catch (Exception e) {
            return new ParsedMacros();
        }
    }

    public static class ParsedMacros implements Serializable {
        @SerializedName("proteinas")
        public String proteinas;
        @SerializedName("carbohidratos")
        public String carbohidratos;
        @SerializedName("grasas")
        public String grasas;

        public float getProteinasValue() { return parseValue(proteinas); }
        public float getCarbohidratosValue() { return parseValue(carbohidratos); }
        public float getGrasasValue() { return parseValue(grasas); }

        private float parseValue(String val) {
            if (val == null) return 0;
            try {
                return Float.parseFloat(val.replaceAll("[^0-9.]", ""));
            } catch (Exception e) {
                return 0;
            }
        }
    }
}
