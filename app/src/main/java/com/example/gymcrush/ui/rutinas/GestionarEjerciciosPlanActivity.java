package com.example.gymcrush.ui.rutinas;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.gymcrush.R;
import com.example.gymcrush.data.remote.RetrofitClient;
import com.example.gymcrush.data.remote.dto.ApiResponseDto;
import com.example.gymcrush.data.remote.dto.DetallePlanRequestDto;
import com.example.gymcrush.data.remote.dto.DetallePlanResponseDto;
import com.example.gymcrush.ui.ejercicios.Ejercicio;
import com.example.gymcrush.ui.ejercicios.RepositorioEjercicios;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GestionarEjerciciosPlanActivity extends AppCompatActivity {

    private Long planId;
    private String nombrePlan;
    private RecyclerView rvDetalles;
    private DetallePlanAdapter adapter;
    private FloatingActionButton fabAdd;
    private List<Ejercicio> catalogoEjercicios = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestionar_ejercicios_plan);

        planId = getIntent().getLongExtra("PLAN_ID", -1);
        nombrePlan = getIntent().getStringExtra("PLAN_NOMBRE");

        Toolbar toolbar = findViewById(R.id.toolbarGestionar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Ejercicios: " + nombrePlan);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        rvDetalles = findViewById(R.id.rvDetallePlan);
        fabAdd = findViewById(R.id.fabAddEjercicioDetalle);

        rvDetalles.setLayoutManager(new LinearLayoutManager(this));
        adapter = new DetallePlanAdapter(new ArrayList<>(), this::eliminarEjercicio);
        rvDetalles.setAdapter(adapter);

        fabAdd.setOnClickListener(v -> mostrarDialogoAnadir());

        cargarDetalles();
        cargarCatalogoEjercicios();
    }

    private void cargarDetalles() {
        RetrofitClient.getApiService().getEjerciciosDelPlan(planId).enqueue(new Callback<ApiResponseDto<List<DetallePlanResponseDto>>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<List<DetallePlanResponseDto>>> call, Response<ApiResponseDto<List<DetallePlanResponseDto>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adapter.updateList(response.body().getDatos());
                }
            }
            @Override
            public void onFailure(Call<ApiResponseDto<List<DetallePlanResponseDto>>> call, Throwable t) {
                Toast.makeText(GestionarEjerciciosPlanActivity.this, "Error al cargar ejercicios del plan", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cargarCatalogoEjercicios() {
        RepositorioEjercicios.getTodosAsync(new RepositorioEjercicios.RepositorioCallback() {
            @Override
            public void onLoaded(List<Ejercicio> ejercicios) {
                catalogoEjercicios = ejercicios;
            }
            @Override
            public void onError(String mensaje) {
                Toast.makeText(GestionarEjerciciosPlanActivity.this, "Error al cargar catálogo: " + mensaje, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void eliminarEjercicio(DetallePlanResponseDto detalle) {
        RetrofitClient.getApiService().quitarEjercicioDelPlan(detalle.getId()).enqueue(new Callback<ApiResponseDto<Void>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<Void>> call, Response<ApiResponseDto<Void>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(GestionarEjerciciosPlanActivity.this, "Ejercicio quitado", Toast.LENGTH_SHORT).show();
                    cargarDetalles();
                }
            }
            @Override
            public void onFailure(Call<ApiResponseDto<Void>> call, Throwable t) {
                Toast.makeText(GestionarEjerciciosPlanActivity.this, "Error de red", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mostrarDialogoAnadir() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_anadir_ejercicio_plan, null);
        
        Spinner spinnerEjercicios = view.findViewById(R.id.spinnerEjerciciosCatalogo);
        Spinner spinnerDia = view.findViewById(R.id.spinnerDiaSemana);
        EditText etOrden = view.findViewById(R.id.etOrdenPlan);

        ArrayAdapter<Ejercicio> adapterEj = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, catalogoEjercicios);
        adapterEj.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEjercicios.setAdapter(adapterEj);

        String[] dias = {"Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"};
        ArrayAdapter<String> adapterDia = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, dias);
        spinnerDia.setAdapter(adapterDia);

        builder.setView(view)
                .setTitle("Añadir Ejercicio al Plan")
                .setPositiveButton("Añadir", (dialog, which) -> {
                    Ejercicio ej = (Ejercicio) spinnerEjercicios.getSelectedItem();
                    int dia = spinnerDia.getSelectedItemPosition() + 1;
                    String ordenStr = etOrden.getText().toString();
                    int orden = ordenStr.isEmpty() ? 1 : Integer.parseInt(ordenStr);

                    if (ej != null) {
                        anadirEjercicioAlPlan(ej, dia, orden);
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void anadirEjercicioAlPlan(Ejercicio ej, int dia, int orden) {
        // Paso 1: Asegurarnos de que el ejercicio existe en la tabla de 'Entrenamientos' del servidor
        com.example.gymcrush.data.remote.dto.EntrenamientoRequestDto entrenamientoReq = new com.example.gymcrush.data.remote.dto.EntrenamientoRequestDto(
                ej.getNombre(),
                ej.getDescripcion(),
                30,
                ej.getDificultad(),
                ej.getCategoria()
        );

        RetrofitClient.getApiService().crearEntrenamiento(entrenamientoReq).enqueue(new Callback<ApiResponseDto<com.example.gymcrush.data.remote.dto.EntrenamientoResponseDto>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<com.example.gymcrush.data.remote.dto.EntrenamientoResponseDto>> call, Response<ApiResponseDto<com.example.gymcrush.data.remote.dto.EntrenamientoResponseDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Long nuevoIdServidor = response.body().getDatos().getId();

                    // Paso 2: Añadir al detalle del plan
                    DetallePlanRequestDto dto = new DetallePlanRequestDto(nuevoIdServidor, dia, orden);
                    RetrofitClient.getApiService().añadirEjercicioAlPlan(planId, dto).enqueue(new Callback<ApiResponseDto<DetallePlanResponseDto>>() {
                        @Override
                        public void onResponse(Call<ApiResponseDto<DetallePlanResponseDto>> call, Response<ApiResponseDto<DetallePlanResponseDto>> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(GestionarEjerciciosPlanActivity.this, "Añadido correctamente", Toast.LENGTH_SHORT).show();
                                cargarDetalles();
                            }
                        }
                        @Override
                        public void onFailure(Call<ApiResponseDto<DetallePlanResponseDto>> call, Throwable t) {
                            Toast.makeText(GestionarEjerciciosPlanActivity.this, "Error de red", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(GestionarEjerciciosPlanActivity.this, "Error al registrar entrenamiento", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponseDto<com.example.gymcrush.data.remote.dto.EntrenamientoResponseDto>> call, Throwable t) {
                Toast.makeText(GestionarEjerciciosPlanActivity.this, "Error de red", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
