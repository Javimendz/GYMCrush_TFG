package com.example.gymcrush.ui.rutinas;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.gymcrush.data.remote.ApiService;
import com.example.gymcrush.data.remote.RetrofitClient;
import com.example.gymcrush.data.remote.dto.*;
import java.util.Collections;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlanDetalleViewModel extends ViewModel {

    private final ApiService apiService;

    private final MutableLiveData<List<DetallePlanResponseDto>> ejerciciosPlan = new MutableLiveData<>();
    private final MutableLiveData<List<EntrenamientoResponseDto>> catalogoEntrenamientos = new MutableLiveData<>();
    private final MutableLiveData<String> mensaje = new MutableLiveData<>();

    public PlanDetalleViewModel() {
        this.apiService = RetrofitClient.getApiService();
    }

    public LiveData<List<DetallePlanResponseDto>> getEjerciciosPlan() { return ejerciciosPlan; }
    public LiveData<List<EntrenamientoResponseDto>> getCatalogoEntrenamientos() { return catalogoEntrenamientos; }
    public LiveData<String> getMensaje() { return mensaje; }

    // 1. CARGAR LOS EJERCICIOS DEL PLAN (La lista principal)
    public void cargarEjerciciosDelPlan(Long planId) {
        apiService.obtenerEjerciciosDelPlan(planId).enqueue(new Callback<ApiResponseDto<List<DetallePlanResponseDto>>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<List<DetallePlanResponseDto>>> call, Response<ApiResponseDto<List<DetallePlanResponseDto>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getDatos() != null) {
                    List<DetallePlanResponseDto> lista = response.body().getDatos();
                    // Ordenamos por día y luego por orden numérico
                    Collections.sort(lista, (e1, e2) -> {
                        int diaCompare = e1.getDiaSemana().compareTo(e2.getDiaSemana());
                        return (diaCompare != 0) ? diaCompare : e1.getOrden().compareTo(e2.getOrden());
                    });
                    ejerciciosPlan.setValue(lista);
                }
            }
            @Override
            public void onFailure(Call<ApiResponseDto<List<DetallePlanResponseDto>>> call, Throwable t) {
                mensaje.setValue("Error al cargar la estructura del plan");
            }
        });
    }

    // 2. CARGAR EL CATÁLOGO PARA EL SPINNER DEL DIÁLOGO
    public void cargarCatalogoParaSpinner() {
        apiService.listarEntrenamientosCatalogo().enqueue(new Callback<ApiResponseDto<List<EntrenamientoResponseDto>>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<List<EntrenamientoResponseDto>>> call, Response<ApiResponseDto<List<EntrenamientoResponseDto>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    catalogoEntrenamientos.setValue(response.body().getDatos());
                }
            }
            @Override
            public void onFailure(Call<ApiResponseDto<List<EntrenamientoResponseDto>>> call, Throwable t) {
                mensaje.setValue("No se pudo cargar el catálogo de ejercicios");
            }
        });
    }

    // 3. AÑADIR UN NUEVO EJERCICIO AL PLAN
    public void añadirEjercicioAlPlan(Long planId, Long entrenamientoId, int diaSemana, int orden) {
        DetallePlanRequestDto request = new DetallePlanRequestDto(entrenamientoId, diaSemana, orden);

        apiService.añadirEjercicioAlPlan(planId, request).enqueue(new Callback<ApiResponseDto<DetallePlanResponseDto>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<DetallePlanResponseDto>> call, Response<ApiResponseDto<DetallePlanResponseDto>> response) {
                if (response.isSuccessful()) {
                    mensaje.setValue("¡Añadido correctamente!");
                    cargarEjerciciosDelPlan(planId); // Refrescamos la vista automáticamente
                } else {
                    mensaje.setValue("Error: Verifica que no haya duplicados u otro fallo");
                }
            }
            @Override
            public void onFailure(Call<ApiResponseDto<DetallePlanResponseDto>> call, Throwable t) {
                mensaje.setValue("Error de red al guardar");
            }
        });
    }

    // 4. ELIMINAR UN EJERCICIO DEL PLAN
    public void eliminarEjercicioDelPlan(Long detalleId, Long planId) {
        apiService.eliminarEjercicioDelPlan(detalleId).enqueue(new Callback<ApiResponseDto<Void>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<Void>> call, Response<ApiResponseDto<Void>> response) {
                if (response.isSuccessful()) {
                    mensaje.setValue("Ejercicio eliminado del plan");
                    cargarEjerciciosDelPlan(planId);
                }
            }
            @Override
            public void onFailure(Call<ApiResponseDto<Void>> call, Throwable t) {
                mensaje.setValue("Fallo al intentar eliminar");
            }
        });
    }
}