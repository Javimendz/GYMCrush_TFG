package com.example.gymcrush.ui.rutinas;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.gymcrush.R;
import com.example.gymcrush.data.remote.RetrofitClient;
import com.example.gymcrush.data.remote.dto.ApiResponseDto;
import com.example.gymcrush.data.remote.dto.EntrenamientoRequestDto;
import com.example.gymcrush.data.remote.dto.EntrenamientoResponseDto;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RutinasFragment extends Fragment {

    private RecyclerView rvRutinas, rvEjercicios;
    private RutinaGuardadaAdapter rutinasAdapter;
    private EntrenamientoAdapter ejerciciosAdapter;
    private List<EntrenamientoResponseDto> listaEjerciciosCompleta = new ArrayList<>();
    
    private TabLayout tabLayoutPrincipal, tabLayoutCategorias;
    private NestedScrollView containerRutinas, containerEjercicios;
    private LinearLayout layoutVacio;
    private ExtendedFloatingActionButton fabAddEjercicio;
    private boolean isAdmin = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View vista = inflater.inflate(R.layout.rutina_fragment_lista, container, false);

        // Referencias
        rvRutinas = vista.findViewById(R.id.rvMisRutinas);
        rvEjercicios = vista.findViewById(R.id.rvEjerciciosRegistrados);
        MaterialButton btnCrearRutina = vista.findViewById(R.id.btnIrACrear);
        tabLayoutPrincipal = vista.findViewById(R.id.tabLayoutRutinas);
        tabLayoutCategorias = vista.findViewById(R.id.tabLayoutCategorias);
        containerRutinas = vista.findViewById(R.id.containerRutinas);
        containerEjercicios = vista.findViewById(R.id.containerEntrenamientos);
        layoutVacio = vista.findViewById(R.id.layoutEjerciciosVacio);
        fabAddEjercicio = vista.findViewById(R.id.fabAddEjercicioAdmin);

        checkAdminRole();

        // 1. Configurar Rutinas
        rvRutinas.setLayoutManager(new LinearLayoutManager(getContext()));
        rvRutinas.setNestedScrollingEnabled(false);
        rutinasAdapter = new RutinaGuardadaAdapter(RepositorioRutinas.getRutinas());
        rvRutinas.setAdapter(rutinasAdapter);

        // 2. Configurar Entrenamientos
        rvEjercicios.setLayoutManager(new LinearLayoutManager(getContext()));
        rvEjercicios.setNestedScrollingEnabled(false);
        ejerciciosAdapter = new EntrenamientoAdapter(new ArrayList<>());
        ejerciciosAdapter.setAdmin(isAdmin);
        ejerciciosAdapter.setOnItemClickListener(new EntrenamientoAdapter.OnItemClickListener() {
            @Override
            public void onStartWorkout(EntrenamientoResponseDto entrenamiento) {
                // Aquí el usuario (y admin) puede ver los ejercicios o empezar el entreno
                Toast.makeText(getContext(), "Viendo ejercicios de: " + entrenamiento.getNombre(), Toast.LENGTH_SHORT).show();
                // TODO: Abrir pantalla de detalle de ejercicios
            }

            @Override
            public void onManageExercises(EntrenamientoResponseDto entrenamiento) {
                // Solo admin: Botón de la derecha para añadir/gestionar ejercicios
                Toast.makeText(getContext(), "Admin: Gestionando ejercicios de " + entrenamiento.getNombre(), Toast.LENGTH_SHORT).show();
                // TODO: Abrir pantalla de gestión de ejercicios para este entrenamiento
            }
        });
        rvEjercicios.setAdapter(ejerciciosAdapter);

        btnCrearRutina.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), CrearRutinaActivity.class);
            startActivity(intent);
        });

        setupTabs();
        fabAddEjercicio.setOnClickListener(v -> mostrarDialogoNuevoEjercicio());

        cargarEjerciciosDesdeServidor();

        return vista;
    }

    private void checkAdminRole() {
        SharedPreferences prefs = requireContext().getSharedPreferences("DatosUsuario", Context.MODE_PRIVATE);
        Set<String> roles = prefs.getStringSet("roles", new HashSet<>());
        isAdmin = roles.contains("ROLE_ADMIN") || roles.contains("ADMIN");
        if (ejerciciosAdapter != null) ejerciciosAdapter.setAdmin(isAdmin);
        actualizarVisibilidadFab(tabLayoutPrincipal.getSelectedTabPosition());
    }

    private void setupTabs() {
        tabLayoutPrincipal.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                actualizarVisibilidadFab(position);
                if (position == 0) {
                    containerRutinas.setVisibility(View.VISIBLE);
                    containerEjercicios.setVisibility(View.GONE);
                } else {
                    containerRutinas.setVisibility(View.GONE);
                    containerEjercicios.setVisibility(View.VISIBLE);
                }
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });

        tabLayoutCategorias.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                filtrarPorCategoria(tab.getText().toString());
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void actualizarVisibilidadFab(int tabPosition) {
        if (isAdmin && tabPosition == 1) fabAddEjercicio.show();
        else fabAddEjercicio.hide();
    }

    private void cargarEjerciciosDesdeServidor() {
        SharedPreferences prefs = requireContext().getSharedPreferences("DatosUsuario", Context.MODE_PRIVATE);
        long userId = prefs.getLong("user_id", -1);
        if (userId == -1) return;

        RetrofitClient.getApiService().getEntrenamientosUsuario(userId).enqueue(new Callback<ApiResponseDto<List<EntrenamientoResponseDto>>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<List<EntrenamientoResponseDto>>> call, Response<ApiResponseDto<List<EntrenamientoResponseDto>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listaEjerciciosCompleta = response.body().getDatos();
                    configurarFiltrosCategorias();
                }
            }

            @Override
            public void onFailure(Call<ApiResponseDto<List<EntrenamientoResponseDto>>> call, Throwable t) {
                Toast.makeText(getContext(), "Error al cargar entrenamientos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void configurarFiltrosCategorias() {
        tabLayoutCategorias.removeAllTabs();
        tabLayoutCategorias.addTab(tabLayoutCategorias.newTab().setText("TODOS"));

        Set<String> categorias = new HashSet<>();
        for (EntrenamientoResponseDto e : listaEjerciciosCompleta) {
            if (e.getCategoria() != null && !e.getCategoria().isEmpty()) {
                categorias.add(e.getCategoria().toUpperCase());
            }
        }

        for (String cat : categorias) {
            tabLayoutCategorias.addTab(tabLayoutCategorias.newTab().setText(cat));
        }

        filtrarPorCategoria("TODOS");
    }

    private void filtrarPorCategoria(String categoria) {
        List<EntrenamientoResponseDto> filtrada;
        if (categoria.equals("TODOS")) {
            filtrada = new ArrayList<>(listaEjerciciosCompleta);
        } else {
            filtrada = listaEjerciciosCompleta.stream()
                    .filter(e -> e.getCategoria() != null && e.getCategoria().equalsIgnoreCase(categoria))
                    .collect(Collectors.toList());
        }

        ejerciciosAdapter.updateLista(filtrada);
        
        if (filtrada.isEmpty()) {
            layoutVacio.setVisibility(View.VISIBLE);
            rvEjercicios.setVisibility(View.GONE);
        } else {
            layoutVacio.setVisibility(View.GONE);
            rvEjercicios.setVisibility(View.VISIBLE);
        }
    }

    private void mostrarDialogoNuevoEjercicio() {
        Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.dialog_admin_nuevo_ejercicio);
        if (dialog.getWindow() != null) dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextInputEditText etNombre = dialog.findViewById(R.id.etNombreEjercicio);
        TextInputEditText etDesc = dialog.findViewById(R.id.etDescripcionEjercicio);
        TextInputEditText etCat = dialog.findViewById(R.id.etCategoriaEjercicio);
        TextInputEditText etDur = dialog.findViewById(R.id.etDuracionEjercicio);
        TextInputEditText etInt = dialog.findViewById(R.id.etIntensidadEjercicio);
        MaterialButton btnGuardar = dialog.findViewById(R.id.btnGuardarEjercicio);

        btnGuardar.setOnClickListener(v -> {
            String nombre = etNombre.getText().toString().trim();
            String desc = etDesc.getText().toString().trim();
            String cat = etCat.getText().toString().trim();
            String durStr = etDur.getText().toString().trim();
            String intensidad = etInt.getText().toString().trim();

            if (nombre.isEmpty() || durStr.isEmpty() || cat.isEmpty()) {
                Toast.makeText(getContext(), "Por favor, rellena los campos básicos", Toast.LENGTH_SHORT).show();
                return;
            }

            EntrenamientoRequestDto request = new EntrenamientoRequestDto(
                nombre, desc, Integer.parseInt(durStr), intensidad, cat
            );

            RetrofitClient.getApiService().crearEntrenamiento(request).enqueue(new Callback<ApiResponseDto<EntrenamientoResponseDto>>() {
                @Override
                public void onResponse(Call<ApiResponseDto<EntrenamientoResponseDto>> call, Response<ApiResponseDto<EntrenamientoResponseDto>> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(getContext(), "Entrenamiento guardado", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        cargarEjerciciosDesdeServidor();
                    }
                }
                @Override public void onFailure(Call<ApiResponseDto<EntrenamientoResponseDto>> call, Throwable t) {}
            });
        });

        dialog.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (rutinasAdapter != null) rutinasAdapter.notifyDataSetChanged();
    }
}
