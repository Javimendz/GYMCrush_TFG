package com.example.gymcrush.ui.rutinas;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gymcrush.ui.ejercicios.Ejercicio;
import com.example.gymcrush.ui.ejercicios.RepositorioEjercicios;
import com.example.gymcrush.R;
import java.util.ArrayList;
import java.util.List;

public class CrearRutinaActivity extends AppCompatActivity {

    private EditText etNombreRutina;
    private RecyclerView rvEjercicios;
    private Button btnGuardar;
    private EjercicioSeleccionAdapter adapter;
    private List<Ejercicio> listaEjercicios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rutina_activity_crear);

        // 1. Inicializar vistas
        etNombreRutina = findViewById(R.id.etNombreRutina);
        rvEjercicios = findViewById(R.id.rvSeleccionEjercicios);
        btnGuardar = findViewById(R.id.btnGuardarRutina);

        // 2. Cargar ejercicios REALES de la biblioteca
        listaEjercicios = RepositorioEjercicios.getTodos();

        // 3. Configurar el RecyclerView con el Adapter
        rvEjercicios.setLayoutManager(new LinearLayoutManager(this));
        adapter = new EjercicioSeleccionAdapter(listaEjercicios);
        rvEjercicios.setAdapter(adapter);

        // 4. Configurar el botón Guardar
        btnGuardar.setOnClickListener(v -> {
            guardarRutina();
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
                // Creamos un nuevo objeto Ejercicio para guardar los valores específicos de esta rutina
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