package com.example.gymcrush.ui.dieta;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.gymcrush.R;
import com.example.gymcrush.data.remote.dto.NutricionResponseDto;
import com.example.gymcrush.data.remote.dto.ComidaDiariaResponseDto;
import com.example.gymcrush.data.remote.dto.ComidaDiariaRequestDto;
import java.util.List;
import java.util.ArrayList;

public class ResultadoDietaFragment extends Fragment {

    private NutricionResponseDto planNutricional;
    private ComidaAdapter adapter;
    private RecetaAdapter recetaAdapter;
    private RecyclerView rvComidas, rvRecetas;
    private TextView tvLabelRecetas;
    private DietaViewModel viewModel;
    private Long usuarioId;

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

        viewModel = new ViewModelProvider(requireActivity()).get(DietaViewModel.class);
        SharedPreferences prefs = requireActivity().getSharedPreferences("DatosUsuario", Context.MODE_PRIVATE);
        usuarioId = prefs.getLong("user_id", -1L);

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

                    if (!nombreRecipe.isEmpty() && (nombreRecipe.equalsIgnoreCase(nombreComida) || nombreComida.toLowerCase().contains(nombreRecipe.toLowerCase()))) {
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

        adapter.setOnHechaClickListener((comida, isChecked) -> {
            if (isChecked && usuarioId != -1L && planNutricional != null) {
                ComidaDiariaRequestDto request = new ComidaDiariaRequestDto();
                request.setNombreAlimento(comida.getNombreAlimento());
                request.setMomento(comida.getMomento());
                request.setCalorias(comida.getCalorias());
                request.setProteina(comida.getProteina());
                request.setCarbohidratos(comida.getCarbohidratos());
                request.setGrasas(comida.getGrasas());
                request.setPlanNutricionalId(planNutricional.getId());
                
                viewModel.añadirComida(request, usuarioId);
                Toast.makeText(getContext(), "Añadido a tu consumo diario", Toast.LENGTH_SHORT).show();
            }
        });
        rvComidas.setAdapter(adapter);

        setupObservers();

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

    private void setupObservers() {
        viewModel.getPlanActual().observe(getViewLifecycleOwner(), plan -> {
            if (plan != null && planNutricional != null && plan.getId().equals(planNutricional.getId())) {
                this.planNutricional = plan;
                actualizarResumenMacros();
            }
        });
    }

    private void actualizarResumenMacros() {
        actualizarResumenMacros(getView());
    }

    private void actualizarResumenMacros(View view) {
        if (planNutricional == null || view == null) return;

        TextView tvProt = view.findViewById(R.id.tv_res_prot);
        TextView tvCarb = view.findViewById(R.id.tv_res_carb);
        TextView tvGras = view.findViewById(R.id.tv_res_gras);

        float p = 0, c = 0, g = 0;

        // Intentar sumar de las dietas planificadas
        if (planNutricional.getDietas() != null && !planNutricional.getDietas().isEmpty()) {
            for (ComidaDiariaResponseDto item : planNutricional.getDietas()) {
                if (item.getProteina() != null) p += item.getProteina();
                if (item.getCarbohidratos() != null) c += item.getCarbohidratos();
                if (item.getGrasas() != null) g += item.getGrasas();
            }
        }

        // Si no hay macros en dietas, probar con comidas consumidas
        if (p == 0 && c == 0 && planNutricional.getComidas() != null) {
            for (ComidaDiariaResponseDto item : planNutricional.getComidas()) {
                if (item.getProteina() != null) p += item.getProteina();
                if (item.getCarbohidratos() != null) c += item.getCarbohidratos();
                if (item.getGrasas() != null) g += item.getGrasas();
            }
        }

        // Si sigue siendo 0 (plan nuevo o sin datos), calcular según el objetivo calórico y tipo de dieta
        if (p == 0 && c == 0 && planNutricional.getCaloriasObjetivo() != null) {
            int cal = planNutricional.getCaloriasObjetivo();
            String tipo = (planNutricional.getTipoDieta() != null) ? planNutricional.getTipoDieta().toUpperCase() : "";

            if (tipo.contains("HIPER") || tipo.contains("VOLUMEN")) {
                // Hipercalórica: 22% Prot, 48% Carb, 30% Gras
                p = (float) (cal * 0.22 / 4);
                c = (float) (cal * 0.48 / 4);
                g = (float) (cal * 0.30 / 9);
            } else if (tipo.contains("DEFINI") || tipo.contains("BAJA") || tipo.contains("PERDIDA")) {
                // Definición: 35% Prot, 30% Carb, 35% Gras
                p = (float) (cal * 0.35 / 4);
                c = (float) (cal * 0.30 / 4);
                g = (float) (cal * 0.35 / 9);
            } else {
                // Estándar/Mantenimiento: 25% Prot, 40% Carb, 35% Gras
                p = (float) (cal * 0.25 / 4);
                c = (float) (cal * 0.40 / 4);
                g = (float) (cal * 0.35 / 9);
            }
        }

        tvProt.setText(String.format(java.util.Locale.getDefault(), "%.0fg", p));
        tvCarb.setText(String.format(java.util.Locale.getDefault(), "%.0fg", c));
        tvGras.setText(String.format(java.util.Locale.getDefault(), "%.0fg", g));
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

        actualizarResumenMacros(view);

        // En esta pantalla mostramos las sugerencias (dietas) que ya vienen en el plan
        if (planNutricional.getDietas() != null) {
            List<ComidaDiariaResponseDto> dietasActualizadas = new ArrayList<>(planNutricional.getDietas());
            
            // Si las dietas son placeholders genéricos, intentar asignarles una receta real de las que devolvió FatSecret
            if (planNutricional.getMacronutrientes() != null && 
                planNutricional.getMacronutrientes().getRecipes() != null &&
                planNutricional.getMacronutrientes().getRecipes().getRecipe() != null) {
                
                List<com.example.gymcrush.data.remote.dto.FatSecretRecipeDto> recetasFS = 
                    planNutricional.getMacronutrientes().getRecipes().getRecipe();
                
                for (int i = 0; i < dietasActualizadas.size(); i++) {
                    ComidaDiariaResponseDto comida = dietasActualizadas.get(i);
                    
                    // Enriquecemos con datos reales de FatSecret si están disponibles por índice
                    if (i < recetasFS.size()) {
                        com.example.gymcrush.data.remote.dto.FatSecretRecipeDto recetaMatch = recetasFS.get(i);
                        
                        // Si la comida actual es genérica o de simulación, actualizamos con la receta real
                        comida.setNombreAlimento(recetaMatch.getRecipeName());
                        comida.setDescripcion(recetaMatch.getRecipeDescription());
                        comida.setImagenUrl(recetaMatch.getRecipeImage());
                        comida.setRecipeUrl(recetaMatch.getRecipeUrl());
                        
                        if (recetaMatch.getRecipeNutrition() != null) {
                            try {
                                comida.setCalorias(Double.parseDouble(recetaMatch.getRecipeNutrition().getCalories()));
                                comida.setProteina(Double.parseDouble(recetaMatch.getRecipeNutrition().getProtein()));
                                comida.setCarbohidratos(Double.parseDouble(recetaMatch.getRecipeNutrition().getCarbohydrate()));
                                comida.setGrasas(Double.parseDouble(recetaMatch.getRecipeNutrition().getFat()));
                            } catch (Exception ignored) {}
                        }
                    }
                }
            }
            adapter.setComidas(dietasActualizadas);
            // Recalcular macros tras enriquecer con datos reales de FatSecret
            actualizarResumenMacros(view);
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
