package com.example.gymcrush.ui.ejercicios;

import com.example.gymcrush.R;
import java.util.ArrayList;
import java.util.List;

public class RepositorioEjercicios {
    public static List<Ejercicio> getTodos() {
        List<Ejercicio> lista = new ArrayList<>();
        
        // PIERNAS
        lista.add(new Ejercicio(
                "Sentadilla con Barra", 
                "Piernas", 
                "La sentadilla con barra es el ejercicio estrella para ganar fuerza y masa muscular en el tren inferior.", 
                "4 x 10", 
                "https://www.youtube.com/watch?v=gcNh17Ckjgg", 
                R.drawable.ic_launcher_background, 
                "Media 🔥🔥"
        ));
        
        lista.add(new Ejercicio(
                "Peso Muerto", 
                "Piernas", 
                "Levantamiento de potencia que trabaja toda la cadena posterior: glúteos, isquios y espalda baja.", 
                "3 x 8", 
                "https://www.youtube.com/watch?v=ytGaGIn3SjE",
                R.drawable.ic_launcher_background, 
                "Alta 🔥🔥🔥"
        ));

        lista.add(new Ejercicio(
                "Zancadas", 
                "Piernas", 
                "Excelente ejercicio unilateral para trabajar cuádriceps, glúteos y mejorar el equilibrio.", 
                "3 x 12 por pierna", 
                "https://www.youtube.com/watch?v=D7KaRcUTQeE", 
                R.drawable.ic_launcher_background, 
                "Baja 🔥"
        ));

        // PECHO
        lista.add(new Ejercicio(
                "Press de Banca", 
                "Pecho", 
                "Ejercicio básico multiarticular para el desarrollo de los pectorales, tríceps y deltoides anterior.", 
                "4 x 12", 
                "https://www.youtube.com/watch?v=vthMCtgVtFw", 
                R.drawable.ic_launcher_background, 
                "Media 🔥🔥"
        ));

        lista.add(new Ejercicio(
                "Flexiones", 
                "Pecho", 
                "Ejercicio de peso corporal clásico para fortalecer el pecho, hombros y tríceps.", 
                "3 x 20", 
                "https://www.youtube.com/watch?v=yY7Pmq_A8Ok", 
                R.drawable.ic_launcher_background, 
                "Baja 🔥"
        ));

        // ESPALDA
        lista.add(new Ejercicio(
                "Dominadas", 
                "Espalda", 
                "Ejercicio fundamental de tracción superior para trabajar el dorsal ancho y bíceps.", 
                "3 x Al fallo", 
                "https://www.youtube.com/watch?v=eGo4IYlbE5g", 
                R.drawable.ic_launcher_background, 
                "Alta 🔥🔥🔥"
        ));

        lista.add(new Ejercicio(
                "Jalón al Pecho", 
                "Espalda", 
                "Máquina de tracción que permite trabajar el dorsal ancho de forma controlada.", 
                "4 x 10", 
                "https://www.youtube.com/watch?v=CAwf7n6Luuc", 
                R.drawable.ic_launcher_background, 
                "Media 🔥🔥"
        ));

        // HOMBROS
        lista.add(new Ejercicio(
                "Press Militar", 
                "Hombro", 
                "Empuje vertical por encima de la cabeza para desarrollar la fuerza y tamaño del hombro.", 
                "4 x 8", 
                "https://www.youtube.com/watch?v=2yjwxt_fshE", 
                R.drawable.ic_launcher_background, 
                "Alta 🔥🔥🔥"
        ));

        // BRAZO
        lista.add(new Ejercicio(
                "Curl de Bíceps", 
                "Brazo", 
                "Ejercicio de aislamiento para fortalecer y dar forma al bíceps braquial.", 
                "3 x 15", 
                "https://www.youtube.com/watch?v=ykJmrZ5v0Oo", 
                R.drawable.ic_launcher_background, 
                "Baja 🔥"
        ));

        // CORE
        lista.add(new Ejercicio(
                "Plancha Abdominal", 
                "Core", 
                "Mantén el cuerpo recto como una tabla apoyado en los antebrazos para fortalecer el abdomen.", 
                "3 x 45s", 
                "https://www.youtube.com/watch?v=TvxNkmjdhMM", 
                R.drawable.ic_launcher_background, 
                "Baja 🔥"
        ));

        // CARDIO
        lista.add(new Ejercicio(
                "Burpees", 
                "Cardio", 
                "Ejercicio de cuerpo completo explosivo que mejora la capacidad cardiovascular y quema grasa.", 
                "4 x 15", 
                "https://www.youtube.com/watch?v=auBLPXO8Fww", 
                R.drawable.ic_launcher_background, 
                "Alta 🔥🔥🔥"
        ));

        return lista;
    }

    public static List<String> getCategorias() {
        List<String> cats = new ArrayList<>();
        cats.add("Todos");
        cats.add("Piernas");
        cats.add("Pecho");
        cats.add("Espalda");
        cats.add("Hombro");
        cats.add("Brazo");
        cats.add("Core");
        cats.add("Cardio");
        return cats;
    }
}