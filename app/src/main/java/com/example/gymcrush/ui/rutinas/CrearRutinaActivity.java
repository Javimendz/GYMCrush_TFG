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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CrearRutinaActivity extends AppCompatActivity {

    private EditText etNombreRutina;
    private RecyclerView rvEjercicios;
    private Button btnGuardar;
    private EjercicioSeleccionAdapter adapter;
    private List<Ejercicio> listaEjercicios = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rutina_activity_crear);

        // 1. Inicializar vistas
        etNombreRutina = findViewById(R.id.etNombreRutina);
        rvEjercicios = findViewById(R.id.rvSeleccionEjercicios);
        btnGuardar = findViewById(R.id.btnGuardarRutina);

        // 2. Configurar el RecyclerView con el Adapter (vacío inicialmente)
        rvEjercicios.setLayoutManager(new LinearLayoutManager(this));
        adapter = new EjercicioSeleccionAdapter(listaEjercicios);
        rvEjercicios.setAdapter(adapter);

        // 3. Cargar ejercicios desde el servidor (los que el admin añadió)
        cargarEjerciciosDelServidor();

        // 4. Configurar el botón Guardar
        btnGuardar.setOnClickListener(v -> {
            guardarRutina();
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
            if (ej.isSeleccionado() && ej.getId() != null) {
                seleccionados.add(ej);
            }
        }

        if (seleccionados.isEmpty()) {
            Toast.makeText(this, "Selecciona al menos un ejercicio", Toast.LENGTH_SHORT).show();
            return;
        }

        // 2. Enviar cada ejercicio como una asignación individual (el backend parece estar diseñado así)
        // O si el backend permitiera una lista, usaríamos otro endpoint.
        // Dado que asignarEntrenamiento recibe un RutinaRequestDto con UN solo entrenamientoId:
        
        final int totalAAsignar = seleccionados.size();
        final int[] completados = {0};
        final boolean[] huboError = {false};

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
                        // Guardar también en el repositorio local para que aparezca en "MIS RUTINAS"
                        RepositorioRutinas.guardarRutinaCompleta(nombre, seleccionados);
                        finalizarGuardado(huboError[0]);
                    }
                }

                @Override
                public void onFailure(Call<ApiResponseDto<RutinaResponseDto>> call, Throwable t) {
                    completados[0]++;
                    huboError[0] = true;
                    if (completados[0] == totalAAsignar) {
                        // Guardar también en el repositorio local para que aparezca en "MIS RUTINAS"
                        RepositorioRutinas.guardarRutinaCompleta(nombre, seleccionados);
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