package com.example.gymcrush.data.repository;

import com.example.gymcrush.data.remote.ApiService;
import com.example.gymcrush.data.remote.RetrofitClient;
import com.example.gymcrush.data.remote.dto.ApiResponseDto;
import com.example.gymcrush.data.remote.dto.ComidaDiariaRequestDto;
import com.example.gymcrush.data.remote.dto.ComidaDiariaResponseDto;
import com.example.gymcrush.data.remote.dto.NutricionResponseDto;
import com.example.gymcrush.data.remote.dto.PerfilFisicoRequest;

import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;

public class NutricionRepository {
    private final ApiService apiService;

    public NutricionRepository() {
        this.apiService = RetrofitClient.getApiService();
    }

    public void generarPlan(PerfilFisicoRequest request, Callback<ApiResponseDto<NutricionResponseDto>> callback) {
        apiService.generarPlanNutricional(request).enqueue(callback);
    }

    public void obtenerUltimoPlan(Long usuarioId, Callback<ApiResponseDto<NutricionResponseDto>> callback) {
        apiService.obtenerUltimoPlan(usuarioId).enqueue(callback);
    }

    public void obtenerHistorial(Long usuarioId, Callback<ApiResponseDto<List<NutricionResponseDto>>> callback) {
        apiService.obtenerHistorialNutricional(usuarioId).enqueue(callback);
    }

    public void añadirComida(ComidaDiariaRequestDto request, Callback<ApiResponseDto<ComidaDiariaResponseDto>> callback) {
        apiService.añadirComida(request).enqueue(callback);
    }

    public void eliminarComida(Long id, Callback<ApiResponseDto<Void>> callback) {
        apiService.eliminarComida(id).enqueue(callback);
    }

    public void guardarMuchasComidas(Long planId, List<ComidaDiariaRequestDto> comidas, Callback<ApiResponseDto<Void>> callback) {
        apiService.guardarMuchasComidas(planId, comidas).enqueue(callback);
    }

    public void buscarAlimentos(String query, Callback<ApiResponseDto<String>> callback) {
        apiService.buscarAlimentos(query).enqueue(callback);
    }
}
