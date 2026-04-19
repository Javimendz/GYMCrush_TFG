package com.example.gymcrush.ui.rutinas;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.gymcrush.R;
import com.example.gymcrush.ui.ejercicios.Ejercicio;
import com.example.gymcrush.ui.ejercicios.EjercicioAdapter;
import java.util.ArrayList;
import java.util.List;

public class DetalleRutinaActivity extends AppCompatActivity {

    private List<Ejercicio> listaEjercicios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rutina_activity_detalle);

        // 1. Recogemos el nombre de la rutina
        String nombreRutina = getIntent().getStringExtra("NOMBRE_RUTINA");

        TextView tvTitulo = findViewById(R.id.tvTituloDetalle);
        if (tvTitulo != null) {
            tvTitulo.setText(nombreRutina);
        }

        RecyclerView rv = findViewById(R.id.rvEjerciciosDetalle);
        if (rv != null) {
            rv.setLayoutManager(new LinearLayoutManager(this));

            // 2. Obtenemos los objetos Ejercicio guardados
            listaEjercicios = RepositorioRutinas.getEjerciciosDeRutina(nombreRutina);

            if (listaEjercicios == null) {
                listaEjercicios = new ArrayList<>();
            }

            // 3. Adaptador configurado para mostrar Checkbox de completado
            EjercicioAdapter adapter = new EjercicioAdapter(listaEjercicios, true);
            rv.setAdapter(adapter);
        }

        // Botón COMPLETAR RUTINA
        Button btnCompletar = findViewById(R.id.btnCompletarRutina);
        btnCompletar.setOnClickListener(v -> {
            int completados = 0;
            for (Ejercicio e : listaEjercicios) {
                if (e.isSeleccionado()) completados++;
            }
            
            if (completados == 0) {
                Toast.makeText(this, "Marca al menos un ejercicio como completado", Toast.LENGTH_SHORT).show();
            } else {
                // MARCAMOS LA RUTINA COMO COMPLETADA OFICIALMENTE EN EL REPOSITORIO
                RepositorioRutinas.marcarComoCompletada(nombreRutina);
                
                Toast.makeText(this, "¡Felicidades! Has completado la rutina", Toast.LENGTH_LONG).show();
                finish(); // Volver atrás después de completar
            }
        });

        findViewById(R.id.btnVolver).setOnClickListener(v -> finish());
    }
}