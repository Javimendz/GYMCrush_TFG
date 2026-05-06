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
        if (usuarioId == -1L) {
            prefs = requireActivity().getSharedPreferences("DatosUsuario", Context.MODE_PRIVATE);
            usuarioId = prefs.getLong("user_id", -1L);
        }
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
        view.findViewById(R.id.btnRenombrarPlan).setOnClickListener(v -> mostrarDialogoRenombrar());
        view.findViewById(R.id.btnGuardarPlan).setOnClickListener(v -> {
            if (planNutricional != null) {
                mostrarConfirmacionGuardar();
            }
        });

        donutChart = view.findViewById(R.id.donutChartMacros);
        rvComidas = view.findViewById(R.id.rvComidasPlan);
        rvComidas.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ComidaAdapter();
        adapter.setOnComidaClickListener(this::onComidaClick);
        adapter.setOnMenuClickListener((v, comida) -> {
            android.widget.PopupMenu popup = new android.widget.PopupMenu(getContext(), v);
            popup.getMenu().add("Eliminar");
            popup.setOnMenuItemClickListener(item -> {
                if (comida.getId() != null) {
                    viewModel.eliminarComida(comida.getId(), planNutricional.getId(), usuarioId);
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

    // --- CORRECCIÓN APLICADA AQUÍ ---
    private void setupObservers() {
        viewModel.getPlanActual().observe(getViewLifecycleOwner(), plan -> {
            if (plan != null) {
                // SOLUCIÓN: Solo actualizamos si es exactamente el mismo plan que estamos
                // viendo (comparando IDs) o si abrimos el fragmento sin plan previo.
                if (this.planNutricional != null && this.planNutricional.getId() != null) {
                    if (this.planNutricional.getId().equals(plan.getId())) {
                        this.planNutricional = plan;
                        mostrarDatos(getView());
                    }
                } else {
                    this.planNutricional = plan;
                    mostrarDatos(getView());
                }
            }
        });

        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
        });
    }

    private void mostrarConfirmacionGuardar() {
        String mensaje = planNutricional.getId() == null
                ? "¿Deseas guardar este plan en tu historial?"
                : "¿Deseas guardar todas las recetas sugeridas en tu diario de hoy?";

        new com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext(), R.style.MaterialAlertDialog_Rounded)
                .setTitle("Confirmar Guardado")
                .setMessage(mensaje)
                .setPositiveButton("Confirmar", (dialog, which) -> {
                    if (planNutricional.getId() == null) {
                        // Caso catálogo o nuevo plan: Guardar plan completo
                        viewModel.guardarPlan(usuarioId, planNutricional);
                    } else {
                        // Caso plan existente: Guardar sugerencias que aún no están en 'comidas'
                        List<ComidaDiariaRequestDto> batch = new ArrayList<>();
                        if (planNutricional.getDietas() != null) {
                            for (ComidaDiariaResponseDto sugerencia : planNutricional.getDietas()) {
                                // Verificar si esta sugerencia ya fue guardada (está en el adapter como seleccionada)
                                boolean yaGuardada = false;
                                if (adapter != null && adapter.getComidas() != null) {
                                    for (ComidaDiariaResponseDto item : adapter.getComidas()) {
                                        if (item.isSelected() &&
                                                item.getNombreAlimento().equalsIgnoreCase(sugerencia.getNombreAlimento()) &&
                                                item.getMomento().equalsIgnoreCase(sugerencia.getMomento())) {
                                            yaGuardada = true;
                                            break;
                                        }
                                    }
                                }

                                if (!yaGuardada) {
                                    ComidaDiariaRequestDto req = new ComidaDiariaRequestDto();
                                    req.setNombreAlimento(sugerencia.getNombreAlimento());
                                    req.setMomento(sugerencia.getMomento());
                                    req.setCalorias(sugerencia.getCalorias());
                                    req.setProteina(sugerencia.getProteina());
                                    req.setCarbohidratos(sugerencia.getCarbohidratos());
                                    req.setGrasas(sugerencia.getGrasas());
                                    req.setPlanNutricionalId(planNutricional.getId());
                                    req.setImagenUrl(sugerencia.getImagenUrl());
                                    req.setDescripcion(sugerencia.getDescripcion());
                                    req.setRecipeUrl(sugerencia.getRecipeUrl());
                                    batch.add(req);
                                }
                            }
                        }
                        if (!batch.isEmpty()) {
                            viewModel.guardarMuchasComidas(planNutricional.getId(), batch, usuarioId);
                            Toast.makeText(getContext(), "Guardando " + batch.size() + " sugerencias...", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Todas las sugerencias ya están guardadas", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void mostrarDatos(View view) {
        if (planNutricional == null || view == null) return;

        TextView tvTituloHeader = view.findViewById(R.id.tvTituloPlan);
        TextView tvObj = view.findViewById(R.id.tv_resultado_objetivo);
        TextView tvCal = view.findViewById(R.id.tv_resultado_calorias);
        View btnRenombrar = view.findViewById(R.id.btnRenombrarPlan);
        View btnGuardar = view.findViewById(R.id.btnGuardarPlan);

        // El botón de guardar estará siempre visible ahora
        btnGuardar.setVisibility(View.VISIBLE);

        // Siempre permitimos renombrar, incluso si el plan es transitorio (ID null)
        btnRenombrar.setVisibility(View.VISIBLE);

        String titulo = planNutricional.getNombreDieta() != null && !planNutricional.getNombreDieta().isEmpty()
                ? planNutricional.getNombreDieta()
                : (planNutricional.getTipoDieta() != null ? planNutricional.getTipoDieta() : "PLAN PERSONALIZADO");

        // tvCal es el texto grande debajo de OBJETIVO en la card
        if (tvCal != null) {
            tvCal.setText(titulo);
        }

        // tvTituloHeader es el texto en la barra superior (Toolbar personalizada)
        if (tvTituloHeader != null) {
            tvTituloHeader.setText(titulo);
        }
        tvObj.setText("TU PLAN IDEAL");

        List<ComidaDiariaResponseDto> listaAMostrar = new ArrayList<>();
        if (planNutricional.getDietas() != null) {
            for (ComidaDiariaResponseDto sugerencia : planNutricional.getDietas()) {
                ComidaDiariaResponseDto itemMostrar = sugerencia;
                if (planNutricional.getComidas() != null) {
                    for (ComidaDiariaResponseDto real : planNutricional.getComidas()) {
                        if (real.getNombreAlimento().equalsIgnoreCase(sugerencia.getNombreAlimento()) &&
                                real.getMomento().equalsIgnoreCase(sugerencia.getMomento()) &&
                                (real.getPlanNutricionalId() == null || real.getPlanNutricionalId().equals(planNutricional.getId()))) {
                            itemMostrar = real;
                            itemMostrar.setSelected(true); // Marcamos como consumida
                            break;
                        }
                    }
                }
                listaAMostrar.add(itemMostrar);
            }
        }

        if (planNutricional.getComidas() != null) {
            for (ComidaDiariaResponseDto real : planNutricional.getComidas()) {
                if (real.getPlanNutricionalId() != null && !real.getPlanNutricionalId().equals(planNutricional.getId())) {
                    continue;
                }
                boolean yaEsta = false;
                for (ComidaDiariaResponseDto m : listaAMostrar) {
                    if (real.getId() != null && real.getId().equals(m.getId())) {
                        yaEsta = true;
                        break;
                    }
                }
                if (!yaEsta) {
                    real.setSelected(true); // Es comida extra, está consumida
                    listaAMostrar.add(real);
                }
            }
        }
        adapter.setComidas(listaAMostrar);

        actualizarResumenMacros(view);

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

        // Sumar todos los macros del plan, ya que no hay selección de "completado"
        if (adapter != null && adapter.getComidas() != null) {
            for (ComidaDiariaResponseDto item : adapter.getComidas()) {
                if (item.getProteina() != null) p += item.getProteina();
                if (item.getCarbohidratos() != null) c += item.getCarbohidratos();
                if (item.getGrasas() != null) g += item.getGrasas();
                if (item.getCalorias() != null) calConsumidas += item.getCalorias();
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

    private void mostrarDialogoRenombrar() {
        if (planNutricional == null) return;

        // Contenedor para el EditText con estilo
        android.widget.FrameLayout container = new android.widget.FrameLayout(requireContext());
        com.google.android.material.textfield.TextInputLayout textInputLayout = new com.google.android.material.textfield.TextInputLayout(requireContext(), null, com.google.android.material.R.style.Widget_MaterialComponents_TextInputLayout_OutlinedBox);
        textInputLayout.setHint("Nombre del plan");
        textInputLayout.setBoxStrokeColor(android.graphics.Color.parseColor("#CD0277"));
        textInputLayout.setHintTextColor(android.content.res.ColorStateList.valueOf(android.graphics.Color.WHITE));
        textInputLayout.setDefaultHintTextColor(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#CD0277")));
        textInputLayout.setPlaceholderText("Ej. Mi Dieta de Definición");
        textInputLayout.setPlaceholderTextColor(android.content.res.ColorStateList.valueOf(android.graphics.Color.GRAY));

        com.google.android.material.textfield.TextInputEditText et = new com.google.android.material.textfield.TextInputEditText(textInputLayout.getContext());
        et.setText(planNutricional.getNombreDieta());
        et.setTextColor(android.graphics.Color.WHITE);
        if (et.getText() != null) {
            et.setSelection(et.getText().length());
        }

        textInputLayout.addView(et);

        int paddingPx = (int) (24 * getResources().getDisplayMetrics().density);
        android.widget.FrameLayout.LayoutParams lp = new android.widget.FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(paddingPx, paddingPx / 2, paddingPx, 0);
        container.addView(textInputLayout, lp);

        new com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext(), R.style.MaterialAlertDialog_Rounded)
                .setTitle("Renombrar Plan")
                .setMessage("Elige un nombre descriptivo para tu plan nutricional.")
                .setView(container)
                .setPositiveButton("Actualizar", (dialog, which) -> {
                    String nuevoNombre = et.getText().toString().trim();
                    if (!nuevoNombre.isEmpty()) {
                        if (planNutricional.getId() == null) {
                            // Si el plan es transitorio, actualizamos localmente y refrescamos la UI
                            planNutricional.setNombreDieta(nuevoNombre);
                            mostrarDatos(getView());
                        } else {
                            // Si el plan ya existe en BD, llamamos al backend
                            viewModel.actualizarNombrePlan(planNutricional.getId(), nuevoNombre, usuarioId);
                        }
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