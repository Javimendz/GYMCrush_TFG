package com.example.gymcrush.ui.rutinas;

import com.example.gymcrush.R;
import com.example.gymcrush.data.remote.dto.EntrenamientoResponseDto;
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
        return listaRutinas;
    }

    private static void agregarRutinaInicial(String nombre, String duracion, String dificultad, int img, List<Ejercicio> ejs) {
        listaRutinas.add(new RutinaModel(nombre, duracion, dificultad, img, ejs));
        datosRutinas.put(nombre, ejs);
    }

    public static void guardarRutinaCompleta(String nombre, List<Ejercicio> ejercicios) {
        guardarRutinaCompletaConPortada(null, nombre, ejercicios, null);
    }

    public static void guardarRutinaCompletaConPortada(Long id, String nombre, List<Ejercicio> ejercicios, String urlPortada) {
        int imgRes = (urlPortada == null || urlPortada.isEmpty()) ? R.drawable.ic_launcher_background : 0;
        // Evitar duplicados por nombre para mantener consistencia
        listaRutinas.removeIf(r -> r.nombre.equals(nombre));
        listaRutinas.add(new RutinaModel(id, nombre, "Personalizada", "Media", "Rutina personalizada", urlPortada, imgRes, ejercicios));
        datosRutinas.put(nombre, ejercicios);
    }

    public static List<Ejercicio> getEjerciciosDeRutina(String nombre) {
        if (nombre == null) return new ArrayList<>();
        String nombreBusqueda = nombre.toLowerCase().trim();
        for (String key : datosRutinas.keySet()) {
            if (key.toLowerCase().trim().equals(nombreBusqueda)) {
                return datosRutinas.get(key);
            }
        }
        return new ArrayList<>();
    }

    public static void eliminarRutina(String nombre) {
        datosRutinas.remove(nombre);
        listaRutinas.removeIf(r -> r.nombre.equals(nombre));
    }

    public static void sincronizarConServidor(List<EntrenamientoResponseDto> entrenamientos) {
        List<RutinaModel> nuevasRutinas = new ArrayList<>();
        for (EntrenamientoResponseDto e : entrenamientos) {
            // Sincronización insensible a mayúsculas/minúsculas para evitar pérdida de datos
            String nombreNormalizado = e.getNombre() != null ? e.getNombre().toLowerCase().trim() : "";
            List<Ejercicio> ejs = null;
            
            // Buscar en el mapa con el nombre normalizado
            for (String key : datosRutinas.keySet()) {
                if (key.toLowerCase().trim().equals(nombreNormalizado)) {
                    ejs = datosRutinas.get(key);
                    break;
                }
            }

            if (ejs == null) ejs = new ArrayList<>();
            
            RutinaModel model = new RutinaModel(
                    e.getId(),
                    e.getNombre(),
                    e.getDuracion() + " min",
                    e.getIntensidad(),
                    e.getDescripcion(),
                    e.getUrlVideo(),
                    (e.getUrlVideo() == null || e.getUrlVideo().isEmpty()) ? R.drawable.ic_launcher_background : 0,
                    ejs
            );
            nuevasRutinas.add(model);
            
            // Asegurar que el mapa conserve la asociación
            if (!ejs.isEmpty()) {
                datosRutinas.put(e.getNombre(), ejs);
            }
        }
        
        // Mantener rutinas locales que no tienen ID (las hardcodeadas iniciales) si no están en el servidor
        for (RutinaModel local : listaRutinas) {
            if (local.id == null) {
                boolean yaExiste = nuevasRutinas.stream().anyMatch(n -> n.nombre.equals(local.nombre));
                if (!yaExiste) nuevasRutinas.add(local);
            }
        }

        listaRutinas.clear();
        listaRutinas.addAll(nuevasRutinas);
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