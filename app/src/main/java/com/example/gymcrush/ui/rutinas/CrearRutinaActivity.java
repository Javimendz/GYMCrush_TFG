package com.example.gymcrush.ui.rutinas;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gymcrush.R;
import com.example.gymcrush.data.remote.RetrofitClient;
import com.example.gymcrush.data.remote.dto.ApiResponseDto;
import com.example.gymcrush.data.remote.dto.EntrenamientoResponseDto;
import com.example.gymcrush.data.remote.dto.RutinaRequestDto;
import com.example.gymcrush.data.remote.dto.RutinaResponseDto;
import com.example.gymcrush.ui.ejercicios.Ejercicio;
import com.example.gymcrush.ui.ejercicios.RepositorioEjercicios;

import com.example.gymcrush.data.remote.dto.ExerciseApiRequestDto;
import com.example.gymcrush.data.remote.dto.TutorialRequestDto;
import com.example.gymcrush.data.remote.dto.TutorialResponseDto;
import com.google.android.material.textfield.TextInputEditText;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CrearRutinaActivity extends AppCompatActivity {

    private EditText etNombreRutina;
    private TextInputEditText etBuscarBiblioteca;
    private Button btnGuardar, btnBuscarBiblioteca;
    private RecyclerView rvEjercicios;
    private EjercicioSeleccionAdapter adapter;
    private List<Ejercicio> listaEjercicios = new ArrayList<>();
    private Long defaultCategoriaId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rutina_activity_crear);

        // 1. Inicializar vistas
        etNombreRutina = findViewById(R.id.etNombreRutina);
        rvEjercicios = findViewById(R.id.rvSeleccionEjercicios);
        btnGuardar = findViewById(R.id.btnGuardarRutina);
        etBuscarBiblioteca = findViewById(R.id.etBuscarBiblioteca);
        btnBuscarBiblioteca = findViewById(R.id.btnBuscarBiblioteca);

        // 2. Configurar el RecyclerView con el Adapter (vacío inicialmente)
        rvEjercicios.setLayoutManager(new LinearLayoutManager(this));
        adapter = new EjercicioSeleccionAdapter(listaEjercicios);
        rvEjercicios.setAdapter(adapter);

        // 3. Cargar ejercicios desde el servidor (los que el admin añadió)
        cargarEjerciciosDelServidor();
        cargarCategorias();

        // 4. Configurar búsqueda en biblioteca externa
        btnBuscarBiblioteca.setOnClickListener(v -> buscarEnBibliotecaExterna());

        // 5. Configurar el botón Guardar
        btnGuardar.setOnClickListener(v -> {
            guardarRutina();
        });
    }

    private void cargarCategorias() {
        RetrofitClient.getApiService().getCategorias().enqueue(new retrofit2.Callback<com.example.gymcrush.data.remote.dto.ApiResponseDto<List<com.example.gymcrush.data.remote.dto.CategoriaTutorialResponseDto>>>() {
            @Override
            public void onResponse(Call<com.example.gymcrush.data.remote.dto.ApiResponseDto<List<com.example.gymcrush.data.remote.dto.CategoriaTutorialResponseDto>>> call, Response<com.example.gymcrush.data.remote.dto.ApiResponseDto<List<com.example.gymcrush.data.remote.dto.CategoriaTutorialResponseDto>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getDatos() != null && !response.body().getDatos().isEmpty()) {
                    defaultCategoriaId = response.body().getDatos().get(0).getId();
                }
            }

            @Override
            public void onFailure(Call<com.example.gymcrush.data.remote.dto.ApiResponseDto<List<com.example.gymcrush.data.remote.dto.CategoriaTutorialResponseDto>>> call, Throwable t) {
                // Silencioso, se usará el fallback si es necesario
            }
        });
    }

    private void buscarEnBibliotecaExterna() {
        String query = etBuscarBiblioteca.getText().toString().trim();
        if (query.isEmpty()) {
            Toast.makeText(this, "Escribe algo para buscar", Toast.LENGTH_SHORT).show();
            return;
        }

        RetrofitClient.getApiService().buscarEnBiblioteca(query).enqueue(new Callback<ApiResponseDto<List<ExerciseApiRequestDto>>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<List<ExerciseApiRequestDto>>> call, Response<ApiResponseDto<List<ExerciseApiRequestDto>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getDatos() != null) {
                    List<ExerciseApiRequestDto> resultados = response.body().getDatos();
                    if (resultados.isEmpty()) {
                        Toast.makeText(CrearRutinaActivity.this, "No se encontraron ejercicios", Toast.LENGTH_SHORT).show();
                    } else {
                        // Convertir resultados de la API a objetos Ejercicio y añadirlos a la lista
                        for (ExerciseApiRequestDto dto : resultados) {
                            // Verificamos si ya está en la lista para no duplicar (comparación segura)
                            boolean existe = listaEjercicios.stream()
                                    .anyMatch(e -> dto.getName() != null && dto.getName().equalsIgnoreCase(e.getNombre()));
                            if (!existe) {
                                Ejercicio nuevo = new Ejercicio(
                                        null, // No tiene ID de nuestro server aún
                                        dto.getName(),
                                        dto.getBodyPart(),
                                        dto.getTarget() + " - " + dto.getEquipment(),
                                        "N/A",
                                        dto.getGifUrl(),
                                        R.drawable.ic_launcher_foreground, // Placeholder
                                        dto.getDifficulty() != null ? dto.getDifficulty() : "Media"
                                );
                                listaEjercicios.add(0, nuevo); // Añadir al principio
                            }
                        }
                        adapter.notifyDataSetChanged();
                        Toast.makeText(CrearRutinaActivity.this, "Se añadieron " + resultados.size() + " ejercicios de la biblioteca", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(CrearRutinaActivity.this, "Error en la búsqueda", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponseDto<List<ExerciseApiRequestDto>>> call, Throwable t) {
                Toast.makeText(CrearRutinaActivity.this, "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cargarEjerciciosDelServidor() {
        RepositorioEjercicios.getTodosAsync(new RepositorioEjercicios.RepositorioCallback() {
            @Override
            public void onLoaded(List<Ejercicio> ejercicios) {
                listaEjercicios.clear();
                listaEjercicios.addAll(ejercicios);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String mensaje) {
                Toast.makeText(CrearRutinaActivity.this, mensaje, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void guardarRutina() {
        String nombreRaw = etNombreRutina.getText().toString().trim();

        if (nombreRaw.isEmpty()) {
            Toast.makeText(this, "Por favor, ponle un nombre a la rutina", Toast.LENGTH_SHORT).show();
            return;
        }

        // Capitalizar nombre
        String nombre = nombreRaw.substring(0, 1).toUpperCase() + nombreRaw.substring(1).toLowerCase();

        SharedPreferences prefs = getSharedPreferences("DatosUsuario", Context.MODE_PRIVATE);
        long userId = prefs.getLong("user_id", -1);
        if (userId == -1) {
            Toast.makeText(this, "Error: Usuario no identificado", Toast.LENGTH_SHORT).show();
            return;
        }

        List<Ejercicio> seleccionados = listaEjercicios.stream()
                .filter(Ejercicio::isSeleccionado)
                .collect(Collectors.toList());

        if (seleccionados.isEmpty()) {
            Toast.makeText(this, "Selecciona al menos un ejercicio", Toast.LENGTH_SHORT).show();
            return;
        }

        // 1. Calcular duración estimada (10 min por ejercicio) e Intensidad según número
        int duracionEstimada = seleccionados.size() * 10;
        String intensidad = seleccionados.size() > 4 ? "ALTA" : "MEDIA";

        String urlPortada = seleccionados.get(0).getUrlVideo();
        com.example.gymcrush.data.remote.dto.EntrenamientoRequestDto entrenamientoReq = 
            new com.example.gymcrush.data.remote.dto.EntrenamientoRequestDto(
                nombre, 
                "Rutina personalizada con " + seleccionados.size() + " ejercicios", 
                duracionEstimada, 
                intensidad, 
                "PERSONALIZADA",
                urlPortada
            );

        RetrofitClient.getApiService().crearEntrenamiento(entrenamientoReq).enqueue(new Callback<ApiResponseDto<EntrenamientoResponseDto>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<EntrenamientoResponseDto>> call, Response<ApiResponseDto<EntrenamientoResponseDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Long entrenamientoId = response.body().getDatos().getId();
                    
                    // Guardar localmente los ejercicios de esta rutina para el detalle
                    RepositorioRutinas.guardarRutinaCompletaConPortada(entrenamientoId, nombre, seleccionados, urlPortada);

                    // 2. Si NO es solo plantilla, además la asignamos a la agenda de hoy
                    boolean esSoloPlantilla = getIntent().getBooleanExtra("ES_PLANTILLA", false);
                    if (!esSoloPlantilla) {
                        asignarRutinaCreadaAUsuario(userId, entrenamientoId);
                    } else {
                        Toast.makeText(CrearRutinaActivity.this, "¡Plantilla '" + nombre + "' guardada!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } else {
                    Toast.makeText(CrearRutinaActivity.this, "Error al crear la rutina", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponseDto<EntrenamientoResponseDto>> call, Throwable t) {
                Toast.makeText(CrearRutinaActivity.this, "Error de red", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void asignarRutinaCreadaAUsuario(long userId, long entrenamientoId) {
        RutinaRequestDto request = new RutinaRequestDto(
                userId,
                entrenamientoId,
                LocalDate.now().toString(),
                1
        );

        RetrofitClient.getApiService().asignarEntrenamiento(request).enqueue(new Callback<ApiResponseDto<RutinaResponseDto>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<RutinaResponseDto>> call, Response<ApiResponseDto<RutinaResponseDto>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(CrearRutinaActivity.this, "Rutina asignada a tu agenda", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(CrearRutinaActivity.this, "Error al asignar a la agenda", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponseDto<RutinaResponseDto>> call, Throwable t) {
                Toast.makeText(CrearRutinaActivity.this, "Error de red al asignar", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void finalizarGuardado(boolean conErrores) {
        if (conErrores) {
            Toast.makeText(this, "Hubo errores al guardar algunos ejercicios", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Rutina guardada correctamente en el servidor", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}