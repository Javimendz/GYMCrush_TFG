package com.example.gymcrush.ui.rutinas;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.gymcrush.R;
import com.example.gymcrush.ui.ejercicios.Ejercicio;
import com.example.gymcrush.ui.ejercicios.EjercicioAdapter;
import java.util.ArrayList;
import java.util.List;

public class DetalleRutinaActivity extends AppCompatActivity {

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

            // 2. Obtenemos los objetos Ejercicio guardados (que ya llevan sus series y reps)
            List<Ejercicio> listaEjercicios = RepositorioRutinas.getEjerciciosDeRutina(nombreRutina);

            if (listaEjercicios == null) {
                listaEjercicios = new ArrayList<>();
            }

            // 3. Usamos el adaptador de la biblioteca, pero activando el modo 'mostrarReps'
            // para que se vean las series y repeticiones personalizadas.
            EjercicioAdapter adapter = new EjercicioAdapter(listaEjercicios, true);
            rv.setAdapter(adapter);
        }

        findViewById(R.id.btnVolver).setOnClickListener(v -> finish());
    }
}