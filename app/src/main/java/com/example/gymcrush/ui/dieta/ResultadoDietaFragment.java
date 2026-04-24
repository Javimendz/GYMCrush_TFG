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
import com.example.gymcrush.data.remote.dto.FatSecretRecipeDto;
import com.example.gymcrush.ui.custom.CustomDonutChart;
import java.util.List;
import java.util.ArrayList;
import java.util.Locale;

public class ResultadoDietaFragment extends Fragment {

    private NutricionResponseDto planNutricional;
    private ComidaAdapter adapter;
    private RecetaAdapter recetaAdapter;
    private RecyclerView rvComidas, rvRecetas;
    private TextView tvLabelRecetas;
    private CustomDonutChart donutChart;
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
        SharedPreferences prefs = requireActivity().getSharedPreferences("GymCrushPrefs", Context.MODE_PRIVATE);
        usuarioId = prefs.getLong("usuarioId", -1L);
        viewModel = new ViewModelProvider(requireActivity()).get(DietaViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_resultado_dieta, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        setupObservers();
        mostrarDatos(view);
    }

    private void initViews(View view) {
        view.findViewById(R.id.btnBackResultadoDieta).setOnClickListener(v -> getParentFragmentManager().popBackStack());

        donutChart = view.findViewById(R.id.donutChartMacros);
        rvComidas = view.findViewById(R.id.rvComidasPlan);
        rvComidas.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ComidaAdapter();
        adapter.setOnComidaClickListener(this::onComidaClick);
        adapter.setOnHechaClickListener((comida, isChecked) -> actualizarResumenMacros(view));
        adapter.setOnMenuClickListener((v, comida) -> {
            android.widget.PopupMenu popup = new android.widget.PopupMenu(getContext(), v);
            popup.getMenu().add("Eliminar");
            popup.setOnMenuItemClickListener(item -> {
                if (comida.getId() != null) {
                    viewModel.eliminarComida(comida.getId(), usuarioId);
                } else {
                    Toast.makeText(getContext(), "No se puede eliminar una sugerencia", Toast.LENGTH_SHORT).show();
                }
                return true;
            });
            popup.show();
        });
        rvComidas.setAdapter(adapter);

        rvRecetas = view.findViewById(R.id.rvRecetasPlan);
        rvRecetas.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recetaAdapter = new RecetaAdapter(this::onRecetaClick);
        rvRecetas.setAdapter(recetaAdapter);
        tvLabelRecetas = view.findViewById(R.id.tvLabelRecetas);

        view.findViewById(R.id.btnAddComidaResultado).setOnClickListener(v -> mostrarDialogoAñadirComida());
        view.findViewById(R.id.btnBuscarComidaResultado).setOnClickListener(v -> mostrarDialogoBusqueda());
    }

    private void onComidaClick(ComidaDiariaResponseDto comida) {
        if (planNutricional != null && planNutricional.getMacronutrientes() != null &&
                planNutricional.getMacronutrientes().getRecipes() != null &&
                planNutricional.getMacronutrientes().getRecipes().getRecipe() != null) {

            for (FatSecretRecipeDto recipe : planNutricional.getMacronutrientes().getRecipes().getRecipe()) {
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
        DetalleComidaFragment detalle = DetalleComidaFragment.newInstance(comida);
        detalle.show(getParentFragmentManager(), "detalle_comida");
    }

    private void onRecetaClick(FatSecretRecipeDto recipe) {
        ComidaDiariaResponseDto dto = new ComidaDiariaResponseDto();
        dto.setNombreAlimento(recipe.getRecipeName());
        dto.setDescripcion(recipe.getRecipeDescription());
        dto.setRecipeUrl(recipe.getRecipeUrl());
        dto.setImagenUrl(recipe.getRecipeImage());
        
        if (recipe.getRecipeNutrition() != null) {
            try {
                dto.setCalorias(Double.parseDouble(recipe.getRecipeNutrition().getCalories()));
                dto.setProteina(Double.parseDouble(recipe.getRecipeNutrition().getProtein()));
                dto.setCarbohidratos(Double.parseDouble(recipe.getRecipeNutrition().getCarbohydrate()));
                dto.setGrasas(Double.parseDouble(recipe.getRecipeNutrition().getFat()));
            } catch (Exception ignored) {}
        }
        
        DetalleComidaFragment.newInstance(dto).show(getParentFragmentManager(), "detalle_receta");
    }

    private void setupObservers() {
        viewModel.getPlanActual().observe(getViewLifecycleOwner(), plan -> {
            if (plan != null && planNutricional != null && plan.getId().equals(planNutricional.getId())) {
                this.planNutricional = plan;
                mostrarDatos(getView());
            }
        });

        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
        });
    }

    private void mostrarDatos(View view) {
        if (planNutricional == null || view == null) return;

        TextView tvObj = view.findViewById(R.id.tv_resultado_objetivo);
        TextView tvCal = view.findViewById(R.id.tv_resultado_calorias);

        String titulo = planNutricional.getNombreDieta() != null && !planNutricional.getNombreDieta().isEmpty()
                ? planNutricional.getNombreDieta()
                : (planNutricional.getTipoDieta() != null ? planNutricional.getTipoDieta() : "PLAN PERSONALIZADO");

        tvCal.setText(titulo);
        int calObjetivo = planNutricional.getCaloriasObjetivo() != null ? planNutricional.getCaloriasObjetivo() : 0;
        tvObj.setText(String.format(Locale.getDefault(), "OBJETIVO: %d KCAL", calObjetivo));

        actualizarResumenMacros(view);

        List<ComidaDiariaResponseDto> listaAMostrar = new ArrayList<>();
        if (planNutricional.getDietas() != null) {
            for (ComidaDiariaResponseDto sugerencia : planNutricional.getDietas()) {
                ComidaDiariaResponseDto itemMostrar = sugerencia;
                if (planNutricional.getComidas() != null) {
                    for (ComidaDiariaResponseDto real : planNutricional.getComidas()) {
                        if (real.getNombreAlimento().equalsIgnoreCase(sugerencia.getNombreAlimento()) &&
                            real.getMomento().equalsIgnoreCase(sugerencia.getMomento())) {
                            itemMostrar = real;
                            break;
                        }
                    }
                }
                listaAMostrar.add(itemMostrar);
            }
        }
        
        if (planNutricional.getComidas() != null) {
            for (ComidaDiariaResponseDto real : planNutricional.getComidas()) {
                boolean yaEsta = false;
                for (ComidaDiariaResponseDto m : listaAMostrar) {
                    if (real.getId() != null && real.getId().equals(m.getId())) {
                        yaEsta = true;
                        break;
                    }
                }
                if (!yaEsta) listaAMostrar.add(real);
            }
        }
        adapter.setComidas(listaAMostrar);

        if (planNutricional.getMacronutrientes() != null && 
            planNutricional.getMacronutrientes().getRecipes() != null &&
            planNutricional.getMacronutrientes().getRecipes().getRecipe() != null) {
            
            List<FatSecretRecipeDto> recetas = planNutricional.getMacronutrientes().getRecipes().getRecipe();
            recetaAdapter.setRecetas(recetas);
            rvRecetas.setVisibility(View.VISIBLE);
            tvLabelRecetas.setVisibility(View.VISIBLE);
        } else {
            rvRecetas.setVisibility(View.GONE);
            tvLabelRecetas.setVisibility(View.GONE);
        }
    }

    private void actualizarResumenMacros(View view) {
        if (planNutricional == null || view == null) return;

        TextView tvProt = view.findViewById(R.id.tv_res_prot);
        TextView tvCarb = view.findViewById(R.id.tv_res_carb);
        TextView tvGras = view.findViewById(R.id.tv_res_gras);
        TextView tvResumenCal = view.findViewById(R.id.tv_resumen_calorias);
        com.google.android.material.progressindicator.LinearProgressIndicator progress = view.findViewById(R.id.progress_resultado_calorias);

        double p = 0, c = 0, g = 0, calConsumidas = 0;
        int calObjetivo = planNutricional.getCaloriasObjetivo() != null ? planNutricional.getCaloriasObjetivo() : 2000;

        // Sumamos solo los items que tienen la propiedad 'selected' como true
        // Primero de las sugerencias (dietas)
        if (planNutricional.getDietas() != null) {
            for (ComidaDiariaResponseDto item : planNutricional.getDietas()) {
                if (item.isSelected()) {
                    if (item.getProteina() != null) p += item.getProteina();
                    if (item.getCarbohidratos() != null) c += item.getCarbohidratos();
                    if (item.getGrasas() != null) g += item.getGrasas();
                    if (item.getCalorias() != null) calConsumidas += item.getCalorias();
                }
            }
        }
        
        // Luego de las comidas reales añadidas por el usuario
        if (planNutricional.getComidas() != null) {
            for (ComidaDiariaResponseDto item : planNutricional.getComidas()) {
                if (item.isSelected()) {
                    if (item.getProteina() != null) p += item.getProteina();
                    if (item.getCarbohidratos() != null) c += item.getCarbohidratos();
                    if (item.getGrasas() != null) g += item.getGrasas();
                    if (item.getCalorias() != null) calConsumidas += item.getCalorias();
                }
            }
        }

        tvResumenCal.setText(String.format(Locale.getDefault(), "%.0f kcal de %d", calConsumidas, calObjetivo));
        int pct = calObjetivo > 0 ? (int) ((calConsumidas / calObjetivo) * 100) : 0;
        progress.setProgress(Math.min(pct, 100));

        tvProt.setText(String.format(Locale.getDefault(), "%.0fg Prot", p));
        tvCarb.setText(String.format(Locale.getDefault(), "%.0fg Carbs", c));
        tvGras.setText(String.format(Locale.getDefault(), "%.0fg Grasas", g));

        if (donutChart != null) {
            donutChart.setData((float) p, (float) c, (float) g);
        }
    }

    private void mostrarDialogoAñadirComida() {
        if (planNutricional == null) {
            Toast.makeText(getContext(), "Error: No hay plan activo", Toast.LENGTH_SHORT).show();
            return;
        }

        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_comida, null);
        android.widget.AutoCompleteTextView spinnerMomento = dialogView.findViewById(R.id.dialog_spinner_momento);
        com.google.android.material.textfield.TextInputEditText etNombre = dialogView.findViewById(R.id.dialog_et_nombre);
        com.google.android.material.textfield.TextInputEditText etCalorias = dialogView.findViewById(R.id.dialog_et_calorias);
        com.google.android.material.textfield.TextInputEditText etProt = dialogView.findViewById(R.id.dialog_et_prot);
        com.google.android.material.textfield.TextInputEditText etCarbs = dialogView.findViewById(R.id.dialog_et_carbs);
        com.google.android.material.textfield.TextInputEditText etGrasas = dialogView.findViewById(R.id.dialog_et_grasas);

        String[] momentos = {"DESAYUNO", "ALMUERZO", "COMIDA", "MERIENDA", "CENA"};
        spinnerMomento.setAdapter(new android.widget.ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, momentos));

        new com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
                .setView(dialogView)
                .setPositiveButton("Guardar", (dialog, which) -> {
                    try {
                        ComidaDiariaRequestDto request = new ComidaDiariaRequestDto();
                        request.setMomento(spinnerMomento.getText().toString());
                        request.setNombreAlimento(etNombre.getText().toString());
                        request.setCalorias(Double.parseDouble(etCalorias.getText().toString()));
                        request.setProteina(Double.parseDouble(etProt.getText().toString()));
                        request.setCarbohidratos(Double.parseDouble(etCarbs.getText().toString()));
                        request.setGrasas(Double.parseDouble(etGrasas.getText().toString()));
                        request.setPlanNutricionalId(planNutricional.getId());

                        viewModel.añadirComida(request, usuarioId);
                    } catch (Exception e) {
                        Toast.makeText(getContext(), "Error en los datos introducidos", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void mostrarDialogoBusqueda() {
        if (planNutricional == null) {
            Toast.makeText(getContext(), "Error: No hay plan activo", Toast.LENGTH_SHORT).show();
            return;
        }

        viewModel.limpiarBusqueda();

        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_search_food, null);
        androidx.appcompat.widget.SearchView searchView = dialogView.findViewById(R.id.searchViewFood);
        searchView.setSuggestionsAdapter(null);
        searchView.setInputType(android.text.InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        android.widget.AutoCompleteTextView spinnerMomento = dialogView.findViewById(R.id.spinnerMomentoBusqueda);
        android.widget.ProgressBar progressBar = dialogView.findViewById(R.id.pbSearchLoading);
        RecyclerView rvResults = dialogView.findViewById(R.id.rvFoodSearchResults);
        TextView tvNoResults = dialogView.findViewById(R.id.tvNoSearchResults);

        String[] momentos = {"DESAYUNO", "ALMUERZO", "COMIDA", "MERIENDA", "CENA"};
        spinnerMomento.setAdapter(new android.widget.ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, momentos));
        spinnerMomento.setText(momentos[0], false);
        spinnerMomento.setOnClickListener(v -> spinnerMomento.showDropDown());

        FoodSearchAdapter searchAdapter = new FoodSearchAdapter();
        rvResults.setAdapter(searchAdapter);
        rvResults.setLayoutManager(new LinearLayoutManager(getContext()));

        viewModel.getResultadosBusqueda().observe(getViewLifecycleOwner(), items -> {
            if (items != null) {
                searchAdapter.setItems(items);
                boolean showNoResults = items.isEmpty() && !searchView.getQuery().toString().isEmpty();
                tvNoResults.setVisibility(showNoResults ? View.VISIBLE : View.GONE);
                if (showNoResults) {
                    tvNoResults.setText("No se encontraron resultados");
                }
            }
        });

        viewModel.getLoading().observe(getViewLifecycleOwner(), loading -> {
            progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
            if (loading) tvNoResults.setVisibility(View.GONE);
        });

        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!query.isEmpty()) {
                    viewModel.buscarAlimentos(query);
                    searchView.clearFocus();
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                viewModel.onSearchQueryChanged(newText);
                return false;
            }
        });

        new com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext(), R.style.MaterialAlertDialog_Rounded)
                .setView(dialogView)
                .setPositiveButton("Añadir", (dialog, which) -> {
                    List<ComidaDiariaRequestDto> seleccionados = searchAdapter.getSelectedItems();
                    if (!seleccionados.isEmpty()) {
                        String momento = spinnerMomento.getText().toString();
                        for (ComidaDiariaRequestDto c : seleccionados) {
                            c.setPlanNutricionalId(planNutricional.getId());
                            c.setMomento(momento);
                        }
                        viewModel.guardarMuchasComidas(planNutricional.getId(), seleccionados, usuarioId);
                    }
                })
                .setNegativeButton("Cerrar", null)
                .show();
    }
}
