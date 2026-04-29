package com.example.gymcrush.ui.rutinas;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.gymcrush.R;
import com.example.gymcrush.data.remote.RetrofitClient;
import com.example.gymcrush.data.remote.dto.ApiResponseDto;
import com.example.gymcrush.data.remote.dto.PlanResponseDto;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlanesFragment extends Fragment {

    private RecyclerView rvPlanes;
    private FloatingActionButton fabAddPlan;
    private PlanesAdapter adapter;
    private long userId;
    private boolean isAdmin = false;

    public interface OnPlanSubscriptionListener {
        void onPlanSubscribed();
    }
    private OnPlanSubscriptionListener subscriptionListener;

    public void setOnPlanSubscriptionListener(OnPlanSubscriptionListener listener) {
        this.subscriptionListener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_planes_entrenamiento, container, false);

        rvPlanes = view.findViewById(R.id.rvPlanesEntrenamiento);
        fabAddPlan = view.findViewById(R.id.fabAddPlan);
        rvPlanes.setLayoutManager(new LinearLayoutManager(getContext()));

        SharedPreferences prefs = requireContext().getSharedPreferences("DatosUsuario", Context.MODE_PRIVATE);
        userId = prefs.getLong("user_id", -1);

        Set<String> roles = prefs.getStringSet("roles", new HashSet<>());
        isAdmin = roles.contains("ROLE_ADMIN") || roles.contains("ADMIN");

        if (isAdmin) {
            fabAddPlan.setVisibility(View.VISIBLE);
            fabAddPlan.setOnClickListener(v -> startActivity(new Intent(requireContext(), CrearPlanActivity.class)));

            View btnNuevoPlanToolbar = view.findViewById(R.id.btnNuevoPlanToolbar);
            if (btnNuevoPlanToolbar != null) {
                btnNuevoPlanToolbar.setVisibility(View.VISIBLE);
                btnNuevoPlanToolbar.setOnClickListener(v -> startActivity(new Intent(requireContext(), CrearPlanActivity.class)));
            }
        }

        adapter = new PlanesAdapter(new ArrayList<>(), new PlanesAdapter.OnPlanClickListener() {
            @Override
            public void onSuscribir(PlanResponseDto plan) {
                suscribirUsuario(plan.getId());
            }

            @Override
            public void onEditar(PlanResponseDto plan) {
                // Aquí SÍ tiene sentido gestionar los ejercicios porque es un PLAN
                Intent intent = new Intent(requireContext(), GestionarEjerciciosPlanActivity.class);
                intent.putExtra("PLAN_ID", plan.getId());
                intent.putExtra("PLAN_NOMBRE", plan.getNombre());
                startActivity(intent);
            }

            @Override
            public void onEliminar(PlanResponseDto plan) {
                new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                        .setTitle("Eliminar Plan")
                        .setMessage("¿Estás seguro de que quieres eliminar el plan '" + plan.getNombre() + "'?")
                        .setPositiveButton("Eliminar", (dialog, which) -> eliminarPlanMaestro(plan.getId()))
                        .setNegativeButton("Cancelar", null)
                        .show();
            }
        });

        adapter.setAdmin(isAdmin);
        rvPlanes.setAdapter(adapter);
        cargarPlanes();

        return view;
    }

    private void suscribirUsuario(Long planId) {
        if (userId == -1) return;

        // ¡LA MAGIA DEL BACKEND!
        // Llamamos al endpoint y el backend automáticamente llena la tabla 'rutinas' del usuario
        RetrofitClient.getApiService().suscribirUsuarioAPlan(planId, userId).enqueue(new Callback<ApiResponseDto<Void>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<Void>> call, Response<ApiResponseDto<Void>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "¡Plan activado con éxito! Revisa tu agenda.", Toast.LENGTH_LONG).show();
                    if (subscriptionListener != null) {
                        subscriptionListener.onPlanSubscribed();
                    }
                } else {
                    Toast.makeText(getContext(), "Error al activar el plan", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponseDto<Void>> call, Throwable t) {
                Toast.makeText(getContext(), "Error de red", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void eliminarPlanMaestro(Long planId) {
        RetrofitClient.getApiService().eliminarPlanMaestro(planId).enqueue(new Callback<ApiResponseDto<Void>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<Void>> call, Response<ApiResponseDto<Void>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Plan eliminado", Toast.LENGTH_SHORT).show();
                    cargarPlanes();
                }
            }
            @Override
            public void onFailure(Call<ApiResponseDto<Void>> call, Throwable t) {}
        });
    }

    private void cargarPlanes() {
        RetrofitClient.getApiService().getPlanes().enqueue(new Callback<ApiResponseDto<List<PlanResponseDto>>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<List<PlanResponseDto>>> call, Response<ApiResponseDto<List<PlanResponseDto>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adapter.updateList(response.body().getDatos());
                }
            }
            @Override
            public void onFailure(Call<ApiResponseDto<List<PlanResponseDto>>> call, Throwable t) {}
        });
    }
}