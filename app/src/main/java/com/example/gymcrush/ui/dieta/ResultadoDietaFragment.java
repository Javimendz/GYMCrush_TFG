package com.example.gymcrush.ui.dieta;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.gymcrush.R;
import com.example.gymcrush.data.remote.dto.NutricionResponseDto;
import com.example.gymcrush.data.remote.dto.ComidaDiariaResponseDto;

public class ResultadoDietaFragment extends Fragment {

    private NutricionResponseDto planNutricional;
    private ComidaAdapter adapter;
    private RecetaAdapter recetaAdapter;
    private RecyclerView rvComidas, rvRecetas;
    private TextView tvLabelRecetas;

    public static ResultadoDietaFragment newInstance(NutricionResponseDto plan) {
        ResultadoDietaFragment fragment = new ResultadoDietaFragment();
        Bundle args = new Bundle();
        args.putSerializable("plan_nutricional", plan);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            planNutricional = (NutricionResponseDto) getArguments().getSerializable("plan_nutricional");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_resultado_dieta, container, false);

        initViews(view);
        mostrarDatos(view);

        return view;
    }

    private void initViews(View view) {
        view.findViewById(R.id.btnBackResultadoDieta).setOnClickListener(v -> {
            getParentFragmentManager().popBackStack();
        });

        rvComidas = view.findViewById(R.id.rvComidasPlan);
        rvComidas.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ComidaAdapter();
        adapter.setOnComidaClickListener(comida -> {
            if (planNutricional != null && planNutricional.getMacronutrientes() != null &&
                    planNutricional.getMacronutrientes().getRecipes() != null &&
                    planNutricional.getMacronutrientes().getRecipes().getRecipe() != null) {

                for (com.example.gymcrush.data.remote.dto.FatSecretRecipeDto recipe :
                        planNutricional.getMacronutrientes().getRecipes().getRecipe()) {
                    
                    String nombreRecipe = recipe.getRecipeName() != null ? recipe.getRecipeName().trim() : "";
                    String nombreComida = comida.getNombreAlimento() != null ? comida.getNombreAlimento().trim() : "";

                    if (!nombreRecipe.isEmpty() && nombreRecipe.equalsIgnoreCase(nombreComida)) {
                        comida.setDescripcion(recipe.getRecipeDescription());
                        comida.setRecipeUrl(recipe.getRecipeUrl());
                        if (recipe.getRecipeImage() != null && !recipe.getRecipeImage().isEmpty()) {
                            comida.setImagenUrl(recipe.getRecipeImage());
                        }
                        break;
                    }
                }
            }

            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, DetalleComidaFragment.newInstance(comida))
                    .addToBackStack(null)
                    .commit();
        });
        rvComidas.setAdapter(adapter);

        tvLabelRecetas = view.findViewById(R.id.tvLabelRecetas);
        rvRecetas = view.findViewById(R.id.rvRecetasPlan);
        rvRecetas.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recetaAdapter = new RecetaAdapter(receta -> {
            ComidaDiariaResponseDto tempComida = new ComidaDiariaResponseDto();
            tempComida.setNombreAlimento(receta.getRecipeName());
            tempComida.setDescripcion(receta.getRecipeDescription());
            tempComida.setRecipeUrl(receta.getRecipeUrl());
            tempComida.setImagenUrl(receta.getRecipeImage());
            tempComida.setMomento("RECETA");

            if (receta.getRecipeNutrition() != null) {
                try {
                    tempComida.setCalorias(Double.parseDouble(receta.getRecipeNutrition().getCalories()));
                    tempComida.setProteina(Double.parseDouble(receta.getRecipeNutrition().getProtein()));
                    tempComida.setCarbohidratos(Double.parseDouble(receta.getRecipeNutrition().getCarbohydrate()));
                    tempComida.setGrasas(Double.parseDouble(receta.getRecipeNutrition().getFat()));
                } catch (Exception ignored) {}
            }

            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, DetalleComidaFragment.newInstance(tempComida))
                    .addToBackStack(null)
                    .commit();
        });
        rvRecetas.setAdapter(recetaAdapter);
    }

    private void mostrarDatos(View view) {
        if (planNutricional == null || view == null) return;

        TextView tvObj = view.findViewById(R.id.tv_resultado_objetivo);
        TextView tvCal = view.findViewById(R.id.tv_resultado_calorias);
        TextView tvProt = view.findViewById(R.id.tv_res_prot);
        TextView tvCarb = view.findViewById(R.id.tv_res_carb);
        TextView tvGras = view.findViewById(R.id.tv_res_gras);

        // Usar nombreDieta si existe, si no tipoDieta
        String titulo = planNutricional.getNombreDieta() != null && !planNutricional.getNombreDieta().isEmpty()
                ? planNutricional.getNombreDieta()
                : (planNutricional.getTipoDieta() != null ? planNutricional.getTipoDieta() : "PLAN PERSONALIZADO");

        tvCal.setText(titulo);
        
        int calObjetivo = planNutricional.getCaloriasObjetivo() != null ? planNutricional.getCaloriasObjetivo() : 0;
        tvObj.setText(String.format(java.util.Locale.getDefault(), "OBJETIVO: %d KCAL", calObjetivo));

        float p = 0, c = 0, g = 0;
        if (planNutricional.getDietas() != null) {
            for (com.example.gymcrush.data.remote.dto.ComidaDiariaResponseDto item : planNutricional.getDietas()) {
                if (item.getProteina() != null) p += item.getProteina();
                if (item.getCarbohidratos() != null) c += item.getCarbohidratos();
                if (item.getGrasas() != null) g += item.getGrasas();
            }
        }
        tvProt.setText(String.format(java.util.Locale.getDefault(), "%.0fg", p));
        tvCarb.setText(String.format(java.util.Locale.getDefault(), "%.0fg", c));
        tvGras.setText(String.format(java.util.Locale.getDefault(), "%.0fg", g));

        // En esta pantalla mostramos las sugerencias (dietas) que ya vienen en el plan
        if (planNutricional.getDietas() != null) {
            adapter.setComidas(planNutricional.getDietas());
        }

        // Mostrar recetas si vienen del backend (FatSecret)
        if (planNutricional.getMacronutrientes() != null && 
            planNutricional.getMacronutrientes().getRecipes() != null &&
            planNutricional.getMacronutrientes().getRecipes().getRecipe() != null &&
            !planNutricional.getMacronutrientes().getRecipes().getRecipe().isEmpty()) {
            
            tvLabelRecetas.setVisibility(View.VISIBLE);
            rvRecetas.setVisibility(View.VISIBLE);
            recetaAdapter.setRecetas(planNutricional.getMacronutrientes().getRecipes().getRecipe());
        } else {
            tvLabelRecetas.setVisibility(View.GONE);
            rvRecetas.setVisibility(View.GONE);
        }
    }
}
