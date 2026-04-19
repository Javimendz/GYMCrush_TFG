package com.example.gymcrush.data.repository;

import com.example.gymcrush.data.remote.ApiService;
import com.example.gymcrush.data.remote.RetrofitClient;
import com.example.gymcrush.data.remote.dto.ApiResponseDto;
import com.example.gymcrush.data.remote.dto.DietaResponseDto;

import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;

public class DietaRepository {
    private final ApiService apiService;

    public DietaRepository() {
        this.apiService = RetrofitClient.getApiService();
    }

    public void listarDietas(Callback<ApiResponseDto<List<DietaResponseDto>>> callback) {
        apiService.listarDietas().enqueue(callback);
    }

    public void obtenerDietaPorId(Long id, Callback<ApiResponseDto<DietaResponseDto>> callback) {
        apiService.obtenerDietaPorId(id).enqueue(callback);
    }
}
