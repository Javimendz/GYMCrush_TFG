package com.example.gymcrush.ui.dieta;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.gymcrush.R;

public class ResultadoDietaFragment extends Fragment {

    private String objetivo;
    private int calorias;

    public static ResultadoDietaFragment newInstance(String objetivo, int calorias) {
        ResultadoDietaFragment fragment = new ResultadoDietaFragment();
        Bundle args = new Bundle();
        args.putString("objetivo", objetivo);
        args.putInt("calorias", calorias);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            objetivo = getArguments().getString("objetivo");
            calorias = getArguments().getInt("calorias");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_resultado_dieta, container, false);

        // Botón volver
        view.findViewById(R.id.btnBackResultadoDieta).setOnClickListener(v -> {
            getParentFragmentManager().popBackStack();
        });

        TextView tvObj = view.findViewById(R.id.tv_resultado_objetivo);
        TextView tvCal = view.findViewById(R.id.tv_resultado_calorias);

        if (objetivo != null) {
            tvObj.setText(objetivo.toUpperCase());
        }
        tvCal.setText("Plan de " + calorias + " kcal / día");

        generarDietaAleatoria(view);

        return view;
    }

    private void generarDietaAleatoria(View view) {
        if (objetivo != null && objetivo.contains("Perder")) {
            setupComida(view.findViewById(R.id.res_comida_1), "Desayuno", "Tortilla de 3 claras y 1 huevo con espinacas.", "08:00");
            setupComida(view.findViewById(R.id.res_comida_2), "Media Mañana", "Una manzana y 5 nueces.", "11:00");
            setupComida(view.findViewById(R.id.res_comida_3), "Comida", "Pechuga de pollo (150g) con ensalada verde y poco aceite.", "14:00");
            setupComida(view.findViewById(R.id.res_comida_4), "Merienda", "Yogur griego 0% con canela.", "17:00");
            setupComida(view.findViewById(R.id.res_comida_5), "Cena", "Merluza al horno con espárragos trigueros.", "21:00");
        } else if (objetivo != null && objetivo.contains("Ganar")) {
            setupComida(view.findViewById(R.id.res_comida_1), "Desayuno", "Gachas de avena (80g) con leche, plátano y crema de cacahuete.", "08:30");
            setupComida(view.findViewById(R.id.res_comida_2), "Media Mañana", "Sándwich integral de pavo y queso con aguacate.", "11:30");
            setupComida(view.findViewById(R.id.res_comida_3), "Comida", "Arroz integral (100g) con ternera magra (150g) y verduras.", "14:30");
            setupComida(view.findViewById(R.id.res_comida_4), "Merienda", "Batido de proteínas con avena y un puñado de almendras.", "18:00");
            setupComida(view.findViewById(R.id.res_comida_5), "Cena", "Salmón a la plancha con patata cocida (200g) y brócoli.", "21:30");
        } else {
            setupComida(view.findViewById(R.id.res_comida_1), "Desayuno", "Tostadas integrales con tomate y aceite de oliva + café.", "08:30");
            setupComida(view.findViewById(R.id.res_comida_2), "Media Mañana", "Una pieza de fruta de temporada.", "11:30");
            setupComida(view.findViewById(R.id.res_comida_3), "Comida", "Lentejas estofadas con verduras y una ración de pollo.", "14:30");
            setupComida(view.findViewById(R.id.res_comida_4), "Merienda", "Queso batido 0% con un puñado de arándanos.", "18:00");
            setupComida(view.findViewById(R.id.res_comida_5), "Cena", "Ensalada completa con atún al natural y huevo cocido.", "21:00");
        }
    }

    private void setupComida(View view, String titulo, String desc, String hora) {
        if (view != null) {
            ((TextView) view.findViewById(R.id.tvTituloComida)).setText(titulo);
            ((TextView) view.findViewById(R.id.tvDescripcionComida)).setText(desc);
            ((TextView) view.findViewById(R.id.tvHoraComida)).setText(hora);
        }
    }
}
