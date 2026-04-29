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
        public Long id;
        public String nombre;
        public String duracion;
        public String dificultad;
        public String descripcion;
        public String urlVideo;
        public int imagenResId;
        public boolean completada = false;
        public List<Ejercicio> ejercicios;

        public RutinaModel(String nombre, String duracion, String dificultad, int imagenResId) {
            this(null, nombre, duracion, dificultad, "", "", imagenResId, new ArrayList<>());
        }

        public RutinaModel(String nombre, String duracion, String dificultad, int imagenResId, List<Ejercicio> ejercicios) {
            this(null, nombre, duracion, dificultad, "", "", imagenResId, ejercicios);
        }

        public RutinaModel(Long id, String nombre, String duracion, String dificultad, String descripcion, String urlVideo, int imagenResId, List<Ejercicio> ejercicios) {
            this.id = id;
            this.nombre = nombre;
            this.duracion = duracion;
            this.dificultad = dificultad;
            this.descripcion = descripcion;
            this.urlVideo = urlVideo;
            this.imagenResId = imagenResId;
            this.ejercicios = ejercicios;
        }
    }

    public static List<RutinaModel> getRutinas() {
        if (listaRutinas.isEmpty()) {
            // Inicializar con algunos datos si está vacío
            List<Ejercicio> todos = RepositorioEjercicios.getTodos();
            
            List<Ejercicio> ejs1 = new ArrayList<>();
            Ejercicio ej1_1 = todos.get(5); // Dominadas (ID 6 es índice 5 si están ordenados)
            ej1_1.setSeries("4"); ej1_1.setRepeticiones("10");
            ejs1.add(ej1_1);
            
            Ejercicio ej1_2 = todos.get(3); // Press de Banca (ID 4 es índice 3)
            ej1_2.setSeries("3"); ej1_2.setRepeticiones("8");
            ejs1.add(ej1_2);
            
            agregarRutinaInicial("Upper Body Power", "30m", "🔥🔥", R.drawable.ic_launcher_background, ejs1);
            
            List<Ejercicio> ejs2 = new ArrayList<>();
            Ejercicio ej2_1 = todos.get(2); // Zancadas (ID 3 es índice 2)
            ej2_1.setSeries("3"); ej2_1.setRepeticiones("12");
            ejs2.add(ej2_1);
            
            agregarRutinaInicial("Flexibility Flow", "20m", "🔥", R.drawable.ic_launcher_background, ejs2);
        }
        return listaRutinas;
    }

    private static void agregarRutinaInicial(String nombre, String duracion, String dificultad, int img, List<Ejercicio> ejs) {
        listaRutinas.add(new RutinaModel(nombre, duracion, dificultad, img, ejs));
        datosRutinas.put(nombre, ejs);
    }

    public static void guardarRutinaCompleta(String nombre, List<Ejercicio> ejercicios) {
        listaRutinas.add(new RutinaModel(nombre, "Personalizada", "Media", R.drawable.ic_launcher_background, ejercicios));
        datosRutinas.put(nombre, ejercicios);
    }

    public static void guardarRutinaCompletaConPortada(String nombre, List<Ejercicio> ejercicios, String urlPortada) {
        int imgRes = (urlPortada == null || urlPortada.isEmpty()) ? R.drawable.ic_launcher_background : 0;
        listaRutinas.add(new RutinaModel(null, nombre, "Personalizada", "Media", "Rutina personalizada", urlPortada, imgRes, ejercicios));
        datosRutinas.put(nombre, ejercicios);
    }

    public static List<Ejercicio> getEjerciciosDeRutina(String nombre) {
        return datosRutinas.get(nombre);
    }

    public static void marcarComoCompletada(String nombre) {
        for (RutinaModel r : listaRutinas) {
            if (r.nombre.equals(nombre)) {
                r.completada = true;
                break;
            }
        }
    }
}