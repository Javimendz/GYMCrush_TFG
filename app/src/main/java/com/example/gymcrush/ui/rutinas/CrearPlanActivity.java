package com.example.gymcrush.ui.rutinas;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gymcrush.R;
import com.example.gymcrush.data.remote.RetrofitClient;
import com.example.gymcrush.data.remote.dto.ApiResponseDto;
import com.example.gymcrush.data.remote.dto.PlanRequestDto;
import com.example.gymcrush.data.remote.dto.PlanResponseDto;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CrearPlanActivity extends AppCompatActivity {

    private TextInputEditText etNombre, etDescripcion;
    private Spinner spinnerObjetivo, spinnerNivel;
    private Button btnGuardar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_plan);

        etNombre = findViewById(R.id.etNombrePlan);
        etDescripcion = findViewById(R.id.etDescripcionPlan);
        spinnerObjetivo = findViewById(R.id.spinnerObjetivo);
        spinnerNivel = findViewById(R.id.spinnerNivel);
        btnGuardar = findViewById(R.id.btnGuardarPlan);

        setupSpinners();

        btnGuardar.setOnClickListener(v -> guardarPlan());
    }

    private void setupSpinners() {
        String[] objetivos = {"GANAR_MUSCULO", "PERDER_PESO", "MANTENER_FORMA"};
        String[] niveles = {"PRINCIPIANTE", "INTERMEDIO", "AVANZADO"};

        ArrayAdapter<String> adapterObj = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, objetivos);
        adapterObj.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerObjetivo.setAdapter(adapterObj);

        ArrayAdapter<String> adapterNiv = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, niveles);
        adapterNiv.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerNivel.setAdapter(adapterNiv);
    }

    private void guardarPlan() {
        String nombre = etNombre.getText().toString().trim();
        String descripcion = etDescripcion.getText().toString().trim();
        String objetivo = spinnerObjetivo.getSelectedItem().toString();
        String nivel = spinnerNivel.getSelectedItem().toString();

        if (nombre.isEmpty()) {
            etNombre.setError("Campo obligatorio");
            return;
        }

        PlanRequestDto dto = new PlanRequestDto(nombre, descripcion, objetivo, nivel);

        RetrofitClient.getApiService().crearPlan(dto).enqueue(new Callback<ApiResponseDto<PlanResponseDto>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<PlanResponseDto>> call, Response<ApiResponseDto<PlanResponseDto>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(CrearPlanActivity.this, "Plan creado correctamente", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(CrearPlanActivity.this, "Error al crear el plan", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponseDto<PlanResponseDto>> call, Throwable t) {
                Toast.makeText(CrearPlanActivity.this, "Error de red", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
