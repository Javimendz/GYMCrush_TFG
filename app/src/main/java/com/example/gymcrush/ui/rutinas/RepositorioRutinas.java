package com.example.gymcrush.ui.rutinas;

import com.example.gymcrush.R;
import com.example.gymcrush.ui.ejercicios.Ejercicio;
import com.example.gymcrush.ui.ejercicios.RepositorioEjercicios;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RepositorioRutinas {

    private static Map<String, List<Ejercicio>> datosRutinas = new HashMap<>();
    private static List<RutinaModel> listaRutinas = new ArrayList<>();

    public static class RutinaModel {
        public String nombre;
        public String duracion;
        public String dificultad;
        public int imagenResId;

        public RutinaModel(String nombre, String duracion, String dificultad, int imagenResId) {
            this.nombre = nombre;
            this.duracion = duracion;
            this.dificultad = dificultad;
            this.imagenResId = imagenResId;
        }
    }

    public static List<RutinaModel> getRutinas() {
        if (listaRutinas.isEmpty()) {
            // Inicializar con algunos datos si está vacío
            List<Ejercicio> todos = RepositorioEjercicios.getTodos();
            
            List<Ejercicio> ejs1 = new ArrayList<>();
            ejs1.add(todos.get(0)); ejs1.get(0).setSeries("4"); ejs1.get(0).setRepeticiones("10");
            ejs1.add(todos.get(3)); ejs1.get(1).setSeries("3"); ejs1.get(1).setRepeticiones("8");
            agregarRutinaInicial("Upper Body Power", "30m", "🔥🔥", R.drawable.ic_launcher_background, ejs1);
            
            List<Ejercicio> ejs2 = new ArrayList<>();
            ejs2.add(todos.get(2)); ejs2.get(0).setSeries("3"); ejs2.get(0).setRepeticiones("12");
            agregarRutinaInicial("Flexibility Flow", "20m", "🔥", R.drawable.ic_launcher_background, ejs2);
        }
        return listaRutinas;
    }

    private static void agregarRutinaInicial(String nombre, String duracion, String dificultad, int img, List<Ejercicio> ejs) {
        listaRutinas.add(new RutinaModel(nombre, duracion, dificultad, img));
        datosRutinas.put(nombre, ejs);
    }

    public static void guardarRutinaCompleta(String nombre, List<Ejercicio> ejercicios) {
        listaRutinas.add(new RutinaModel(nombre, "Personalizada", "Media", R.drawable.ic_launcher_background));
        datosRutinas.put(nombre, ejercicios);
    }

    public static List<Ejercicio> getEjerciciosDeRutina(String nombre) {
        return datosRutinas.get(nombre);
    }
}