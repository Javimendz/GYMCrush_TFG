package com.example.gymcrush.data.remote.dto;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class ComidaDiariaRequestDto implements Serializable {
    @SerializedName("momento")
    private String momento;

    @SerializedName("nombreAlimento")
    private String nombreAlimento;

    @SerializedName("calorias")
    private Double calorias;

    @SerializedName("proteina")
    private Double proteina;

    @SerializedName("carbohidratos")
    private Double carbohidratos;

    @SerializedName("grasas")
    private Double grasas;

    @SerializedName("imagenUrl")
    private String imagenUrl;

    @SerializedName("planNutricionalId")
    private Long planNutricionalId;

    // Getters and Setters
    public String getMomento() { return momento; }
    public void setMomento(String momento) { this.momento = momento; }

    public String getNombreAlimento() { return nombreAlimento; }
    public void setNombreAlimento(String nombreAlimento) { this.nombreAlimento = nombreAlimento; }

    public Double getCalorias() { return calorias; }
    public void setCalorias(Double calorias) { this.calorias = calorias; }

    public Double getProteina() { return proteina; }
    public void setProteina(Double proteina) { this.proteina = proteina; }

    public Double getCarbohidratos() { return carbohidratos; }
    public void setCarbohidratos(Double carbohidratos) { this.carbohidratos = carbohidratos; }

    public Double getGrasas() { return grasas; }
    public void setGrasas(Double grasas) { this.grasas = grasas; }

    public String getImagenUrl() { return imagenUrl; }
    public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }

    public Long getPlanNutricionalId() { return planNutricionalId; }
    public void setPlanNutricionalId(Long planNutricionalId) { this.planNutricionalId = planNutricionalId; }
}
