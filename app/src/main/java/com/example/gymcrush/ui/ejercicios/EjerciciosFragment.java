package com.example.gymcrush.ui.ejercicios;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gymcrush.R;
import com.example.gymcrush.data.remote.RetrofitClient;
import com.example.gymcrush.data.remote.dto.ApiResponseDto;
import com.example.gymcrush.data.remote.dto.CategoriaTutorialRequestDto;
import com.example.gymcrush.data.remote.dto.CategoriaTutorialResponseDto;
import com.example.gymcrush.data.remote.dto.ExerciseApiRequestDto;
import com.example.gymcrush.data.remote.dto.TutorialRequestDto;
import com.example.gymcrush.data.remote.dto.TutorialResponseDto;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EjerciciosFragment extends Fragment {

    private RecyclerView rv;
    private EjercicioAdapter adapter;
    private List<TutorialResponseDto> listaCompleta = new ArrayList<>();
    private List<TutorialResponseDto> listaFiltrada = new ArrayList<>();
    private ChipGroup chipGroup;
    private FloatingActionButton fabAdd;
    private List<CategoriaTutorialResponseDto> categoriasBackend = new ArrayList<>();
    private boolean isAdmin = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.ejercicio_fragment_biblioteca, container, false);

        // 1. Configurar RecyclerView
        rv = root.findViewById(R.id.rvEjerciciosBiblio);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        
        checkAdminRole();
        
        adapter = new EjercicioAdapter(listaFiltrada, isAdmin, new EjercicioAdapter.OnEjercicioAdminListener() {
            @Override
            public void onEdit(TutorialResponseDto tutorial) {
                mostrarDialogoEditarEjercicio(tutorial);
            }

            @Override
            public void onDelete(TutorialResponseDto tutorial) {
                confirmarEliminarEjercicio(tutorial);
            }
        });
        rv.setAdapter(adapter);

        // 2. Chips
        chipGroup = root.findViewById(R.id.chipGroupCategorias);

        // 3. FAB para Admin
        fabAdd = root.findViewById(R.id.fabAddExercise);
        if (isAdmin) {
            fabAdd.setVisibility(View.VISIBLE);
            fabAdd.setOnClickListener(v -> mostrarOpcionesCrear());
        } else {
            fabAdd.setVisibility(View.GONE);
        }

        // 4. Cargar datos desde el Backend
        cargarTutoriales();
        cargarCategorias();

        return root;
    }

    private void checkAdminRole() {
        SharedPreferences prefs = requireContext().getSharedPreferences("DatosUsuario", Context.MODE_PRIVATE);
        Set<String> roles = prefs.getStringSet("roles", new HashSet<>());
        isAdmin = roles.contains("ROLE_ADMIN") || roles.contains("ADMIN");
    }

    private void mostrarOpcionesCrear() {
        String[] opciones = {"Buscar en Biblioteca Externa", "Crear Manualmente"};
        new com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
                .setTitle("Nuevo Ejercicio")
                .setItems(opciones, (dialog, which) -> {
                    if (which == 0) mostrarDialogoBusquedaExterna();
                    else mostrarDialogoNuevoEjercicioManual();
                })
                .show();
    }

    private void mostrarDialogoNuevoEjercicioManual() {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_admin_nuevo_ejercicio, null);
        AlertDialog dialog = new AlertDialog.Builder(requireContext(), R.style.CustomDialogTheme).setView(dialogView).create();

        TextInputEditText etNombre = dialogView.findViewById(R.id.etNombreEjercicio);
        TextInputEditText etDesc = dialogView.findViewById(R.id.etDescripcionEjercicio);
        TextInputEditText etCat = dialogView.findViewById(R.id.etCategoriaEjercicio);
        TextInputEditText etDur = dialogView.findViewById(R.id.etDuracionEjercicio);
        TextInputEditText etEquipamiento = dialogView.findViewById(R.id.etIntensidadEjercicio); // Usamos el campo de intensidad para equipamiento
        View btnGuardar = dialogView.findViewById(R.id.btnGuardarEjercicio);

        btnGuardar.setOnClickListener(v -> {
            String nombre = etNombre.getText().toString().trim();
            String desc = etDesc.getText().toString().trim();
            String catNombre = etCat.getText().toString().trim();
            String durS = etDur.getText().toString().trim();
            String equipamiento = etEquipamiento.getText().toString().trim();

            if (nombre.isEmpty() || catNombre.isEmpty()) {
                Toast.makeText(getContext(), "Nombre y categoría obligatorios", Toast.LENGTH_SHORT).show();
                return;
            }

            int duracion = durS.isEmpty() ? 0 : Integer.parseInt(durS);

            // Buscar si la categoría existe
            CategoriaTutorialResponseDto categoriaEncontrada = null;
            for (CategoriaTutorialResponseDto c : categoriasBackend) {
                if (c.getNombre().equalsIgnoreCase(catNombre)) {
                    categoriaEncontrada = c;
                    break;
                }
            }

            if (categoriaEncontrada != null) {
                enviarTutorialBackend(nombre, desc, duracion, equipamiento, categoriaEncontrada.getId(), dialog);
            } else {
                // Crear categoría primero
                crearCategoriaYGuardarTutorial(nombre, desc, duracion, equipamiento, catNombre, dialog);
            }
        });

        dialog.show();
    }

    private void mostrarDialogoEditarEjercicio(TutorialResponseDto t) {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_admin_nuevo_ejercicio, null);
        AlertDialog dialog = new AlertDialog.Builder(requireContext(), R.style.CustomDialogTheme).setView(dialogView).create();

        TextInputEditText etNombre = dialogView.findViewById(R.id.etNombreEjercicio);
        TextInputEditText etDesc = dialogView.findViewById(R.id.etDescripcionEjercicio);
        TextInputEditText etCat = dialogView.findViewById(R.id.etCategoriaEjercicio);
        TextInputEditText etDur = dialogView.findViewById(R.id.etDuracionEjercicio);
        TextInputEditText etEquipamiento = dialogView.findViewById(R.id.etIntensidadEjercicio);
        com.google.android.material.button.MaterialButton btnGuardar = dialogView.findViewById(R.id.btnGuardarEjercicio);

        etNombre.setText(t.getTitulo());
        etDesc.setText(t.getDescripcion());
        etCat.setText(t.getNombreCategoria());
        etDur.setText(String.valueOf(t.getDuracionMin()));
        etEquipamiento.setText(t.getEquipamiento() != null ? t.getEquipamiento() : "");
        btnGuardar.setText("ACTUALIZAR EJERCICIO");

        btnGuardar.setOnClickListener(v -> {
            String nombre = etNombre.getText().toString().trim();
            String desc = etDesc.getText().toString().trim();
            String catNombre = etCat.getText().toString().trim();
            String durS = etDur.getText().toString().trim();
            String equipamiento = etEquipamiento.getText().toString().trim();

            if (nombre.isEmpty() || catNombre.isEmpty()) return;

            int duracion = durS.isEmpty() ? 0 : Integer.parseInt(durS);

            TutorialRequestDto request = new TutorialRequestDto();
            request.setTitulo(nombre);
            request.setDescripcion(desc);
            request.setDuracionMin(duracion);
            request.setUrlVideo(t.getUrlVideo());
            request.setEquipamiento(equipamiento);
            request.setMusculoObjetivo(t.getMusculoObjetivo());

            // Buscar categoría
            Long catId = -1L;
            for (CategoriaTutorialResponseDto c : categoriasBackend) {
                if (c.getNombre().equalsIgnoreCase(catNombre)) {
                    catId = c.getId();
                    break;
                }
            }
            request.setCategoriaId(catId);

            if (catId != -1L) {
                RetrofitClient.getApiService().actualizarTutorial(t.getId(), request).enqueue(new Callback<ApiResponseDto<TutorialResponseDto>>() {
                    @Override
                    public void onResponse(Call<ApiResponseDto<TutorialResponseDto>> call, Response<ApiResponseDto<TutorialResponseDto>> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(getContext(), "Actualizado", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            cargarTutoriales();
                        }
                    }
                    @Override
                    public void onFailure(Call<ApiResponseDto<TutorialResponseDto>> call, Throwable th) {}
                });
            } else {
                Toast.makeText(getContext(), "La categoría debe existir para editar", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    private void confirmarEliminarEjercicio(TutorialResponseDto t) {
        new com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
                .setTitle("Eliminar Ejercicio")
                .setMessage("¿Estás seguro de que quieres eliminar '" + t.getTitulo() + "'?")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    RetrofitClient.getApiService().eliminarTutorial(t.getId()).enqueue(new Callback<ApiResponseDto<Void>>() {
                        @Override
                        public void onResponse(Call<ApiResponseDto<Void>> call, Response<ApiResponseDto<Void>> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(getContext(), "Eliminado", Toast.LENGTH_SHORT).show();
                                cargarTutoriales();
                            }
                        }
                        @Override
                        public void onFailure(Call<ApiResponseDto<Void>> call, Throwable th) {}
                    });
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void crearCategoriaYGuardarTutorial(String nombre, String desc, int duracion, String equipamiento, String catNombre, AlertDialog dialog) {
        CategoriaTutorialRequestDto catReq = new CategoriaTutorialRequestDto(catNombre, "Ejercicios de " + catNombre);
        RetrofitClient.getApiService().crearCategoria(catReq).enqueue(new Callback<ApiResponseDto<CategoriaTutorialResponseDto>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<CategoriaTutorialResponseDto>> call, Response<ApiResponseDto<CategoriaTutorialResponseDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    categoriasBackend.add(response.body().getDatos());
                    enviarTutorialBackend(nombre, desc, duracion, equipamiento, response.body().getDatos().getId(), dialog);
                }
            }
            @Override
            public void onFailure(Call<ApiResponseDto<CategoriaTutorialResponseDto>> call, Throwable t) {}
        });
    }

    private void enviarTutorialBackend(String nombre, String desc, int duracion, String equipamiento, Long catId, AlertDialog dialog) {
        TutorialRequestDto request = new TutorialRequestDto();
        request.setTitulo(nombre);
        request.setDescripcion(desc);
        request.setDuracionMin(duracion);
        request.setCategoriaId(catId);
        request.setEquipamiento(equipamiento);
        request.setUrlVideo(""); // Manual no suele tener video al crear así

        RetrofitClient.getApiService().crearTutorial(request).enqueue(new Callback<ApiResponseDto<TutorialResponseDto>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<TutorialResponseDto>> call, Response<ApiResponseDto<TutorialResponseDto>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Ejercicio creado", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    cargarTutoriales();
                }
            }
            @Override
            public void onFailure(Call<ApiResponseDto<TutorialResponseDto>> call, Throwable t) {}
        });
    }

    private void cargarCategorias() {
        RetrofitClient.getApiService().getCategorias().enqueue(new Callback<ApiResponseDto<List<CategoriaTutorialResponseDto>>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<List<CategoriaTutorialResponseDto>>> call, Response<ApiResponseDto<List<CategoriaTutorialResponseDto>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    categoriasBackend = response.body().getDatos();
                }
            }
            @Override
            public void onFailure(Call<ApiResponseDto<List<CategoriaTutorialResponseDto>>> call, Throwable t) {}
        });
    }

    private void mostrarDialogoBusquedaExterna() {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_admin_buscar_ejercicios, null);
        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .create();

        TextInputEditText etSearch = dialogView.findViewById(R.id.etSearchExternal);
        View btnSearch = dialogView.findViewById(R.id.btnSearchExternal);
        ProgressBar pb = dialogView.findViewById(R.id.pbLoadingExternal);
        RecyclerView rvExt = dialogView.findViewById(R.id.rvExternalExercises);
        
        List<ExerciseApiRequestDto> listaExt = new ArrayList<>();
        AdminEjerciciosAdapter extAdapter = new AdminEjerciciosAdapter(listaExt, exercise -> {
            mostrarConfirmacionAgregar(exercise, dialog);
        });
        
        rvExt.setLayoutManager(new LinearLayoutManager(getContext()));
        rvExt.setAdapter(extAdapter);

        btnSearch.setOnClickListener(v -> {
            String query = etSearch.getText().toString().trim();
            if (query.isEmpty()) return;

            pb.setVisibility(View.VISIBLE);
            RetrofitClient.getApiService().buscarEnBiblioteca(query).enqueue(new Callback<ApiResponseDto<List<ExerciseApiRequestDto>>>() {
                @Override
                public void onResponse(Call<ApiResponseDto<List<ExerciseApiRequestDto>>> call, Response<ApiResponseDto<List<ExerciseApiRequestDto>>> response) {
                    pb.setVisibility(View.GONE);
                    if (response.isSuccessful() && response.body() != null) {
                        listaExt.clear();
                        listaExt.addAll(response.body().getDatos());
                        extAdapter.notifyDataSetChanged();
                    }
                }
                @Override
                public void onFailure(Call<ApiResponseDto<List<ExerciseApiRequestDto>>> call, Throwable t) {
                    pb.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        dialog.show();
    }

    private void mostrarConfirmacionAgregar(ExerciseApiRequestDto exercise, AlertDialog parentDialog) {
        List<String> opciones = new ArrayList<>();
        for (CategoriaTutorialResponseDto cat : categoriasBackend) {
            opciones.add(cat.getNombre());
        }
        opciones.add("➕ Crear nueva categoría...");

        new com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
                .setTitle("Seleccionar Categoría")
                .setItems(opciones.toArray(new String[0]), (dialog, which) -> {
                    if (which == opciones.size() - 1) {
                        mostrarDialogoNuevaCategoria(exercise, parentDialog);
                    } else {
                        CategoriaTutorialResponseDto cat = categoriasBackend.get(which);
                        guardarTutorial(exercise, cat, parentDialog);
                    }
                })
                .show();
    }

    private void mostrarDialogoNuevaCategoria(ExerciseApiRequestDto exercise, AlertDialog parentDialog) {
        android.widget.EditText input = new android.widget.EditText(getContext());
        input.setHint("Nombre de la categoría (ej: Fuerza)");
        int padding = (int) (20 * getResources().getDisplayMetrics().density);
        android.widget.FrameLayout container = new android.widget.FrameLayout(requireContext());
        container.addView(input);
        input.setPadding(padding, padding, padding, padding);

        new com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
                .setTitle("Nueva Categoría")
                .setMessage("Introduce el nombre de la nueva categoría")
                .setView(container)
                .setPositiveButton("Crear y Continuar", (dialog, which) -> {
                    String nombreCat = input.getText().toString().trim();
                    if (nombreCat.isEmpty()) return;

                    CategoriaTutorialRequestDto nuevaCatDto = new CategoriaTutorialRequestDto(nombreCat, "Ejercicios de " + nombreCat);
                    RetrofitClient.getApiService().crearCategoria(nuevaCatDto).enqueue(new Callback<ApiResponseDto<CategoriaTutorialResponseDto>>() {
                        @Override
                        public void onResponse(Call<ApiResponseDto<CategoriaTutorialResponseDto>> call, Response<ApiResponseDto<CategoriaTutorialResponseDto>> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                CategoriaTutorialResponseDto catCreada = response.body().getDatos();
                                guardarTutorial(exercise, catCreada, parentDialog);
                                cargarCategorias();
                            } else {
                                Toast.makeText(getContext(), "Error al crear categoría", Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override
                        public void onFailure(Call<ApiResponseDto<CategoriaTutorialResponseDto>> call, Throwable t) {
                            Toast.makeText(getContext(), "Fallo de red", Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void guardarTutorial(ExerciseApiRequestDto exercise, CategoriaTutorialResponseDto cat, AlertDialog parentDialog) {
        TutorialRequestDto request = new TutorialRequestDto();
        request.setTitulo(exercise.getName());
        request.setDescripcion(exercise.getDescription() != null ? exercise.getDescription() : "Ejercicio de " + exercise.getTarget());
        request.setUrlVideo(exercise.getGifUrl());
        request.setDuracionMin(5); // Valor por defecto
        request.setCategoriaId(cat.getId());
        request.setMusculoObjetivo(exercise.getTarget());
        request.setEquipamiento(exercise.getEquipment());

        RetrofitClient.getApiService().crearTutorial(request).enqueue(new Callback<ApiResponseDto<TutorialResponseDto>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<TutorialResponseDto>> call, Response<ApiResponseDto<TutorialResponseDto>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "¡Tutorial añadido!", Toast.LENGTH_SHORT).show();
                    parentDialog.dismiss();
                    cargarTutoriales(); // Recargar lista principal
                } else {
                    Toast.makeText(getContext(), "Error al guardar", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponseDto<TutorialResponseDto>> call, Throwable t) {
                Toast.makeText(getContext(), "Error de red", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cargarTutoriales() {
        RetrofitClient.getApiService().getTutoriales().enqueue(new Callback<ApiResponseDto<List<TutorialResponseDto>>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<List<TutorialResponseDto>>> call, Response<ApiResponseDto<List<TutorialResponseDto>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listaCompleta = response.body().getDatos();
                    listaFiltrada.clear();
                    listaFiltrada.addAll(listaCompleta);
                    adapter.notifyDataSetChanged();
                    setupChips();
                } else {
                    Toast.makeText(getContext(), "Error al cargar ejercicios", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponseDto<List<TutorialResponseDto>>> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupChips() {
        if (getContext() == null) return;
        chipGroup.removeAllViews();
        List<String> categorias = new ArrayList<>();
        categorias.add("Todos");
        
        for (TutorialResponseDto t : listaCompleta) {
            if (t.getNombreCategoria() != null && !categorias.contains(t.getNombreCategoria())) {
                categorias.add(t.getNombreCategoria());
            }
        }

        for (String cat : categorias) {
            Chip chip = new Chip(getContext());
            chip.setText(cat);
            chip.setCheckable(true);
            if (cat.equals("Todos")) chip.setChecked(true);

            chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) filtrar(cat);
            });
            chipGroup.addView(chip);
        }
    }

    private void filtrar(String categoria) {
        listaFiltrada.clear();
        if (categoria.equalsIgnoreCase("Todos")) {
            listaFiltrada.addAll(listaCompleta);
        } else {
            for (TutorialResponseDto t : listaCompleta) {
                if (t.getNombreCategoria().equalsIgnoreCase(categoria)) {
                    listaFiltrada.add(t);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }
}
