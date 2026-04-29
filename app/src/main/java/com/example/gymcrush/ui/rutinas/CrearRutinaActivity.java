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
        String nombre = etNombreRutina.getText().toString().trim();

        if (nombre.isEmpty()) {
            Toast.makeText(this, "Por favor, ponle un nombre a la rutina", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences prefs = getSharedPreferences("DatosUsuario", Context.MODE_PRIVATE);
        long userId = prefs.getLong("user_id", -1);
        if (userId == -1) {
            Toast.makeText(this, "Error: Usuario no identificado", Toast.LENGTH_SHORT).show();
            return;
        }

        // 1. Filtrar solo los ejercicios seleccionados
        List<Ejercicio> seleccionados = new ArrayList<>();
        for (Ejercicio ej : listaEjercicios) {
            if (ej.isSeleccionado()) {
                seleccionados.add(ej);
            }
        }

        if (seleccionados.isEmpty()) {
            Toast.makeText(this, "Selecciona al menos un ejercicio", Toast.LENGTH_SHORT).show();
            return;
        }

        // Si es una plantilla (proviene de Tab "Mis Rutinas")
        boolean esPlantilla = getIntent().getBooleanExtra("ES_PLANTILLA", false);
        if (esPlantilla) {
            guardarComoPlantilla(userId, nombre, seleccionados);
        } else {
            // Comportamiento normal: Guardar como rutina diaria
            procesarEjerciciosYGuardar(userId, nombre, seleccionados);
        }
    }

    private void guardarComoPlantilla(long userId, String nombre, List<Ejercicio> seleccionados) {
        // Tomar la URL del video del primer ejercicio como portada de la rutina
        String urlPortada = (seleccionados != null && !seleccionados.isEmpty()) ? seleccionados.get(0).getUrlVideo() : "";

        com.example.gymcrush.data.remote.dto.EntrenamientoRequestDto request = 
            new com.example.gymcrush.data.remote.dto.EntrenamientoRequestDto(
                nombre, 
                "Rutina personalizada creada por el usuario", 
                60, 
                "MEDIA", 
                "PERSONALIZADA",
                urlPortada
            );
        
        com.example.gymcrush.data.remote.RetrofitClient.getApiService().crearEntrenamiento(request).enqueue(new Callback<ApiResponseDto<EntrenamientoResponseDto>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<EntrenamientoResponseDto>> call, Response<ApiResponseDto<EntrenamientoResponseDto>> response) {
                if (response.isSuccessful()) {
                    // Guardar localmente para que el DetalleRutinaActivity pueda mostrar los ejercicios
                    RepositorioRutinas.guardarRutinaCompletaConPortada(nombre, seleccionados, urlPortada);
                    Toast.makeText(CrearRutinaActivity.this, "¡Plantilla guardada con éxito!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(CrearRutinaActivity.this, "Error al guardar plantilla en el servidor", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponseDto<EntrenamientoResponseDto>> call, Throwable t) {
                Toast.makeText(CrearRutinaActivity.this, "Error de red", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void procesarEjerciciosYGuardar(long userId, String nombreRutina, List<Ejercicio> seleccionados) {
        List<Ejercicio> listosParaAsignar = new ArrayList<>();
        List<Ejercicio> pendientesDeCrear = seleccionados.stream()
                .filter(e -> e.getId() == null)
                .collect(Collectors.toList());

        listosParaAsignar.addAll(seleccionados.stream()
                .filter(e -> e.getId() != null)
                .collect(Collectors.toList()));

        if (pendientesDeCrear.isEmpty()) {
            enviarAsignacionesAlServidor(userId, nombreRutina, listosParaAsignar);
        } else {
            // Crear los ejercicios que vienen de ExerciseDB en nuestro backend
            crearEjerciciosFaltantes(userId, nombreRutina, pendientesDeCrear, listosParaAsignar);
        }
    }

    private void crearEjerciciosFaltantes(long userId, String nombreRutina, List<Ejercicio> pendientes, List<Ejercicio> listos) {
        final int total = pendientes.size();
        final int[] creados = {0};

        for (Ejercicio ej : pendientes) {
            // Asegurar que la descripción no exceda el límite del backend (500 chars)
            String desc = ej.getDescripcion();
            if (desc != null && desc.length() > 500) {
                desc = desc.substring(0, 497) + "...";
            } else if (desc == null || desc.isEmpty()) {
                desc = "Sin descripción detallada.";
            }

            TutorialRequestDto dto = new TutorialRequestDto(
                    ej.getNombre(),
                    desc,
                    ej.getUrlVideo(),
                    10, // duracionMin por defecto
                    defaultCategoriaId != null ? defaultCategoriaId : 1L,
                    ej.getCategoria(), // musculoObjetivo
                    "N/A" // equipamiento
            );

            RetrofitClient.getApiService().crearTutorial(dto).enqueue(new Callback<ApiResponseDto<TutorialResponseDto>>() {
                @Override
                public void onResponse(Call<ApiResponseDto<TutorialResponseDto>> call, Response<ApiResponseDto<TutorialResponseDto>> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().getDatos() != null) {
                        ej.setId(response.body().getDatos().getId());
                        listos.add(ej);
                    } else {
                        android.util.Log.e("API_ERROR", "Error creando tutorial: " + response.code());
                    }
                    creados[0]++;
                    if (creados[0] == total) {
                        enviarAsignacionesAlServidor(userId, nombreRutina, listos);
                    }
                }

                @Override
                public void onFailure(Call<ApiResponseDto<TutorialResponseDto>> call, Throwable t) {
                    android.util.Log.e("API_ERROR", "Fallo red creando tutorial", t);
                    creados[0]++;
                    if (creados[0] == total) {
                        enviarAsignacionesAlServidor(userId, nombreRutina, listos);
                    }
                }
            });
        }
    }

    private void enviarAsignacionesAlServidor(long userId, String nombreRutina, List<Ejercicio> seleccionados) {
        if (seleccionados.isEmpty()) {
            Toast.makeText(this, "No se pudieron preparar los ejercicios", Toast.LENGTH_SHORT).show();
            return;
        }

        final int totalAAsignar = seleccionados.size();
        final int[] completados = {0};
        final boolean[] huboError = {false};

        String urlPortada = (seleccionados != null && !seleccionados.isEmpty()) ? seleccionados.get(0).getUrlVideo() : "";

        for (int i = 0; i < seleccionados.size(); i++) {
            Ejercicio ej = seleccionados.get(i);
            RutinaRequestDto request = new RutinaRequestDto(
                    userId,
                    ej.getId(),
                    LocalDate.now().toString(),
                    i + 1
            );

            RetrofitClient.getApiService().asignarEntrenamiento(request).enqueue(new Callback<ApiResponseDto<RutinaResponseDto>>() {
                @Override
                public void onResponse(Call<ApiResponseDto<RutinaResponseDto>> call, Response<ApiResponseDto<RutinaResponseDto>> response) {
                    completados[0]++;
                    if (!response.isSuccessful() || response.body() == null || !response.body().isSuccess()) {
                        huboError[0] = true;
                    }
                    
                    if (completados[0] == totalAAsignar) {
                        RepositorioRutinas.guardarRutinaCompletaConPortada(nombreRutina, seleccionados, urlPortada);
                        finalizarGuardado(huboError[0]);
                    }
                }

                @Override
                public void onFailure(Call<ApiResponseDto<RutinaResponseDto>> call, Throwable t) {
                    completados[0]++;
                    huboError[0] = true;
                    if (completados[0] == totalAAsignar) {
                        RepositorioRutinas.guardarRutinaCompletaConPortada(nombreRutina, seleccionados, urlPortada);
                        finalizarGuardado(huboError[0]);
                    }
                }
            });
        }
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