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
import com.example.gymcrush.ui.ejercicios.Ejercicio;

import java.util.ArrayList;
import java.util.List;

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
        SharedPreferences prefs = getSharedPreferences("DatosUsuario", Context.MODE_PRIVATE);
        long userId = prefs.getLong("user_id", -1);
        if (userId == -1) return;

        RetrofitClient.getApiService().getEntrenamientosUsuario(userId).enqueue(new Callback<ApiResponseDto<List<EntrenamientoResponseDto>>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<List<EntrenamientoResponseDto>>> call, Response<ApiResponseDto<List<EntrenamientoResponseDto>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<EntrenamientoResponseDto> datos = response.body().getDatos();
                    listaEjercicios.clear();
                    for (EntrenamientoResponseDto dto : datos) {
                        // Convertimos el DTO del servidor al objeto Ejercicio usado en la UI de selección
                        Ejercicio ej = new Ejercicio(
                                dto.getNombre(),
                                "Personalizado",
                                dto.getDescripcion(),
                                String.valueOf(dto.getDuracion()),
                                "", 
                                R.drawable.ic_launcher_background, 
                                dto.getIntensidad()
                        );
                        listaEjercicios.add(ej);
                    }
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<ApiResponseDto<List<EntrenamientoResponseDto>>> call, Throwable t) {
                Toast.makeText(CrearRutinaActivity.this, "Error al cargar ejercicios del servidor", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void guardarRutina() {
        String nombre = etNombreRutina.getText().toString().trim();

        if (nombre.isEmpty()) {
            Toast.makeText(this, "Por favor, ponle un nombre a la rutina", Toast.LENGTH_SHORT).show();
            return;
        }

        // Filtrar solo los ejercicios seleccionados y clonarlos para guardar sus series/reps
        List<Ejercicio> seleccionados = new ArrayList<>();
        for (Ejercicio ej : listaEjercicios) {
            if (ej.isSeleccionado()) {
                Ejercicio nuevo = new Ejercicio(
                        ej.getNombre(),
                        ej.getCategoria(),
                        ej.getDescripcion(),
                        ej.getDuracion(),
                        ej.getUrlVideo(),
                        ej.getImagenResId(),
                        ej.getDificultad()
                );
                nuevo.setSeries(ej.getSeries());
                nuevo.setRepeticiones(ej.getRepeticiones());
                seleccionados.add(nuevo);
            }
        }

        if (seleccionados.isEmpty()) {
            Toast.makeText(this, "Selecciona al menos un ejercicio", Toast.LENGTH_SHORT).show();
            return;
        }

        // Guardar la lista de OBJETOS Ejercicio (con sus series/reps)
        RepositorioRutinas.guardarRutinaCompleta(nombre, seleccionados);

        Toast.makeText(this, "Rutina '" + nombre + "' guardada con éxito", Toast.LENGTH_SHORT).show();
        finish();
    }
}