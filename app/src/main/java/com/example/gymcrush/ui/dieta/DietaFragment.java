package com.example.gymcrush.ui.dieta;

import java.util.List;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.gymcrush.R;
import com.example.gymcrush.ui.custom.CustomDonutChart;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.example.gymcrush.data.remote.dto.ComidaDiariaResponseDto;
import com.example.gymcrush.data.remote.dto.DietaResponseDto;
import com.example.gymcrush.data.remote.dto.NutricionResponseDto;
import com.example.gymcrush.data.remote.dto.PerfilFisicoRequest;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;

public class DietaFragment extends Fragment {

    private DietaViewModel viewModel;
    private TextInputEditText etPeso, etAltura, etEdad;
    private AutoCompleteTextView spinnerActividad, spinnerObjetivo, spinnerGenero;
    private MaterialButton btnActualizar, btnAddComida, btnBuscarComida;
    
    private MaterialCardView cardEmptySuggestion, cardSuggestion;
    private TextView tvNombreSugerida, tvCaloriasSugeridas, tvProt, tvCarbs, tvGrasas;
    private CustomDonutChart donutChart;
    private LinearProgressIndicator progressCalorias;
    
    private RecyclerView rvCatalogo, rvHistorial, rvComidas, rvRecetas;
    private DietaAdapter adapter;
    private HistorialNutricionAdapter historialAdapter;
    private ComidaAdapter comidaAdapter;
    private RecetaAdapter recetaAdapter;
    private Long usuarioId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dieta, container, false);

        viewModel = new ViewModelProvider(this).get(DietaViewModel.class);
        
        SharedPreferences prefs = requireActivity().getSharedPreferences("DatosUsuario", Context.MODE_PRIVATE);
        usuarioId = prefs.getLong("user_id", -1L);

        initViews(view);
        setupRecyclerViews();
        setupObservers();
        configurarDropdowns();

        cardSuggestion.setOnClickListener(v -> {
            NutricionResponseDto plan = viewModel.getPlanActual().getValue();
            if (plan != null) {
                ResultadoDietaFragment fragment = ResultadoDietaFragment.newInstance(plan);
                getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
            }
        });

        if (usuarioId != -1L) {
            viewModel.cargarDatosIniciales(usuarioId);
        }

        btnActualizar.setOnClickListener(v -> {
            generarSugerencia();
            // Evitamos la navegación inmediata con datos obsoletos comprobando si el plan es nuevo
            viewModel.getPlanActual().observe(getViewLifecycleOwner(), new Observer<NutricionResponseDto>() {
                private boolean isFirstObservation = true;
                @Override
                public void onChanged(NutricionResponseDto plan) {
                    if (isFirstObservation) {
                        isFirstObservation = false;
                        return;
                    }

                    if (plan != null) {
                        ResultadoDietaFragment fragment = ResultadoDietaFragment.newInstance(plan);
                        getParentFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, fragment)
                            .addToBackStack(null)
                            .commit();
                        viewModel.getPlanActual().removeObserver(this);
                    }
                }
            });
        });
        btnAddComida.setOnClickListener(v -> mostrarDialogoAñadirComida());
        btnBuscarComida.setOnClickListener(v -> mostrarDialogoBusqueda());

        view.findViewById(R.id.btnBackDieta).setOnClickListener(v -> {
            if (getActivity() != null) getActivity().onBackPressed();
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        if (getArguments() != null) {
            String section = getArguments().getString("scrollToSection");
            boolean mostrarHistorial = getArguments().getBoolean("mostrarHistorial", false);

            if (mostrarHistorial || "historial".equals(section)) {
                rvHistorial.post(() -> {
                    View header = view.findViewById(R.id.rvHistorialNutricion);
                    if (header != null) header.getParent().requestChildFocus(header, header);
                });
            }
        }
    }

    public com.example.gymcrush.data.remote.dto.NutricionResponseDto getPlanActual() {
        return viewModel.getPlanActual().getValue();
    }

    private void initViews(View v) {
        etPeso = v.findViewById(R.id.etPeso);
        etAltura = v.findViewById(R.id.etAltura);
        etEdad = v.findViewById(R.id.etEdad);
        spinnerActividad = v.findViewById(R.id.spinnerActividad);
        spinnerObjetivo = v.findViewById(R.id.spinnerObjetivo);
        spinnerGenero = v.findViewById(R.id.spinnerGenero);
        btnActualizar = v.findViewById(R.id.btnActualizarDatos);
        btnAddComida = v.findViewById(R.id.btnAddComidaManual);
        btnBuscarComida = v.findViewById(R.id.btnBuscarComida);

        cardEmptySuggestion = v.findViewById(R.id.cardEmptySuggestion);
        cardSuggestion = v.findViewById(R.id.cardSuggestion);
        tvNombreSugerida = v.findViewById(R.id.tvNombreDietaSugerida);
        tvCaloriasSugeridas = v.findViewById(R.id.tvCaloriasSugeridas);
        tvProt = v.findViewById(R.id.tvProt);
        tvCarbs = v.findViewById(R.id.tvCarbs);
        tvGrasas = v.findViewById(R.id.tvGrasas);
        donutChart = v.findViewById(R.id.donutChartMacros);
        progressCalorias = v.findViewById(R.id.progressCalorias);

        rvCatalogo = v.findViewById(R.id.rvCatalogoDietas);
        rvHistorial = v.findViewById(R.id.rvHistorialNutricion);
        rvComidas = v.findViewById(R.id.rvComidasSugeridas);
        rvRecetas = v.findViewById(R.id.rvRecetasSugeridas);
    }

    private void setupRecyclerViews() {
        adapter = new DietaAdapter(dieta -> {
            // Convertimos la dieta del catálogo a un formato compatible con la vista de resultados
            NutricionResponseDto planSimulado = new NutricionResponseDto();
            planSimulado.setNombreDieta(dieta.getNombre());
            planSimulado.setTipoDieta(dieta.getTipo());
            planSimulado.setCaloriasObjetivo(dieta.getObjetivoCalorico().intValue());

            // Simulamos las comidas diarias recomendadas basándonos en los macros de la dieta
            java.util.List<ComidaDiariaResponseDto> dietasSimuladas = new java.util.ArrayList<>();

            String[] momentos = {"DESAYUNO", "ALMUERZO", "COMIDA", "MERIENDA", "CENA"};
            for (String momento : momentos) {
                ComidaDiariaResponseDto item = new ComidaDiariaResponseDto();
                item.setMomento(momento);
                item.setNombreAlimento("Sugerencia de " + momento.toLowerCase());
                // Repartimos macros aproximados para que no salga 0g
                item.setProteina(dieta.getCantidadProteinas() / 5);
                item.setCarbohidratos(dieta.getCantidadCarbohidratos() / 5);
                item.setGrasas(dieta.getCantidadGrasas().doubleValue() / 5);
                item.setCalorias(dieta.getObjetivoCalorico() / 5);
                dietasSimuladas.add(item);
            }
            planSimulado.setDietas(dietasSimuladas);

            // Pasamos a la pantalla de detalles
            ResultadoDietaFragment fragment = ResultadoDietaFragment.newInstance(planSimulado);
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        });
        rvCatalogo.setLayoutManager(new LinearLayoutManager(getContext()));
        rvCatalogo.setAdapter(adapter);

        historialAdapter = new HistorialNutricionAdapter(plan -> {
            ResultadoDietaFragment fragment = ResultadoDietaFragment.newInstance(plan);
            getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
        });
        rvHistorial.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvHistorial.setAdapter(historialAdapter);

        comidaAdapter = new ComidaAdapter();
        comidaAdapter.setOnComidaClickListener(comida -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, DetalleComidaFragment.newInstance(comida))
                    .addToBackStack(null)
                    .commit();
        });
        comidaAdapter.setOnComidaLongClickListener(comida -> {
            new com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Eliminar plato")
                    .setMessage("¿Estás seguro de que quieres eliminar " + comida.getNombreAlimento() + " de tu plan?")
                    .setPositiveButton("Eliminar", (dialog, which) -> {
                        viewModel.eliminarComida(comida.getId(), usuarioId);
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        });
        rvComidas.setLayoutManager(new LinearLayoutManager(getContext()) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        rvComidas.setAdapter(comidaAdapter);

        recetaAdapter = new RecetaAdapter(receta -> {
            com.example.gymcrush.data.remote.dto.ComidaDiariaResponseDto tempComida = new com.example.gymcrush.data.remote.dto.ComidaDiariaResponseDto();
            tempComida.setNombreAlimento(receta.getRecipeName());
            tempComida.setDescripcion(receta.getRecipeDescription());
            tempComida.setRecipeUrl(receta.getRecipeUrl());
            tempComida.setImagenUrl(receta.getRecipeImage());
            tempComida.setMomento("RECETA RECOMENDADA");

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
        rvRecetas.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvRecetas.setAdapter(recetaAdapter);
    }

    private void setupObservers() {
        viewModel.getSaludActual().observe(getViewLifecycleOwner(), salud -> {
            if (salud != null) {
                if (salud.getPeso() != null) etPeso.setText(String.valueOf(salud.getPeso()));
                if (salud.getEstatura() != null) etAltura.setText(String.valueOf(salud.getEstatura()));
                if (salud.getNivelActividad() != null) {
                    spinnerActividad.setText(salud.getNivelActividad(), false);
                }
            }
        });

        viewModel.getPlanActual().observe(getViewLifecycleOwner(), plan -> {
            if (plan != null) {
                cardEmptySuggestion.setVisibility(View.GONE);
                cardSuggestion.setVisibility(View.VISIBLE);
                mostrarPlan(plan);
            } else {
                cardEmptySuggestion.setVisibility(View.VISIBLE);
                cardSuggestion.setVisibility(View.GONE);
            }
        });

        viewModel.getDietasCatalogo().observe(getViewLifecycleOwner(), dietas -> {
            if (dietas != null) {
                adapter.setDietas(dietas);
            }
        });

        viewModel.getHistorial().observe(getViewLifecycleOwner(), historial -> {
            if (historial != null) {
                historialAdapter.setHistorial(historial);
            }
        });

        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
        });
    }

    private void mostrarPlan(NutricionResponseDto plan) {
        String titulo = plan.getNombreDieta() != null && !plan.getNombreDieta().isEmpty()
                ? plan.getNombreDieta()
                : (plan.getTipoDieta() != null ? plan.getTipoDieta() : "PLAN PERSONALIZADO");
        tvNombreSugerida.setText(titulo);
        
        NutricionResponseDto.ParsedMacros macros = plan.getParsedMacros();
        float p = macros.getProteinasValue();
        float c = macros.getCarbohidratosValue();
        float g = macros.getGrasasValue();
        int calObjetivo = plan.getCaloriasObjetivo() != null ? plan.getCaloriasObjetivo() : 2000;

        tvProt.setText(String.format(java.util.Locale.getDefault(), "%.0fg Prot", p));
        tvCarbs.setText(String.format(java.util.Locale.getDefault(), "%.0fg Carbs", c));
        tvGrasas.setText(String.format(java.util.Locale.getDefault(), "%.0fg Grasas", g));

        donutChart.setData(p, c, g);

        // Cálculo de progreso: Comidas registradas vs Objetivo diario
        double calConsumidas = 0;
        List<ComidaDiariaResponseDto> sugerencias = plan.getSugerencias();
        if (sugerencias != null) {
            for (ComidaDiariaResponseDto item : sugerencias) {
                if (item.getCalorias() != null) calConsumidas += item.getCalorias();
            }
        }

        tvCaloriasSugeridas.setText(String.format(java.util.Locale.getDefault(), "%.0f kcal de %d", calConsumidas, calObjetivo));
        int progress = calObjetivo > 0 ? (int) ((calConsumidas / calObjetivo) * 100) : 0;
        progressCalorias.setProgress(Math.min(progress, 100));

        // En el RecyclerView de la pantalla principal de Dieta mostramos las SUGERENCIAS
        if (sugerencias != null) {
            comidaAdapter.setComidas(sugerencias);
        }
    }

    private void generarSugerencia() {
        try {
            PerfilFisicoRequest request = new PerfilFisicoRequest();
            request.setUsuarioId(usuarioId);
            request.setPeso(Double.parseDouble(etPeso.getText().toString()));
            request.setAltura(Double.parseDouble(etAltura.getText().toString()));
            request.setEdad(Integer.parseInt(etEdad.getText().toString()));

            if (spinnerGenero != null) {
                request.setGenero(spinnerGenero.getText().toString());
            } else {
                request.setGenero("MASCULINO"); // Valor por defecto si no hay selector
            }

            // Mapeo de Nivel de Actividad al formato que espera el Backend (Enum)
            String actividadSeleccionada = spinnerActividad.getText().toString();
            String actividadBackend;
            switch (actividadSeleccionada) {
                case "SEDENTARIO":
                    actividadBackend = "SEDENTARIO";
                    break;
                case "LIGERO":
                    actividadBackend = "LIGERO";
                    break;
                case "MODERADO":
                    actividadBackend = "MODERADO";
                    break;
                case "INTENSO":
                    actividadBackend = "INTENSO";
                    break;
                case "ATLETA":
                    actividadBackend = "ATLETA";
                    break;
                default:
                    actividadBackend = "MODERADO";
                    break;
            }
            request.setNivelActividad(actividadBackend);

            // Mapeo de objetivo al formato que espera el Backend (EnumObjetivo)
            String objetivoSeleccionado = spinnerObjetivo.getText().toString();
            String objetivoBackend;
            switch (objetivoSeleccionado) {
                case "PERDER_GRASA":
                    objetivoBackend = "PERDER";
                    break;
                case "GANAR_MUSCULO":
                    objetivoBackend = "GANAR";
                    break;
                default:
                    objetivoBackend = "MANTENER";
                    break;
            }
            request.setObjetivo(objetivoBackend);

            viewModel.generarPlan(request);
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Por favor, completa todos los campos correctamente", Toast.LENGTH_SHORT).show();
        }
    }

    private void mostrarDialogoAñadirComida() {
        NutricionResponseDto plan = viewModel.getPlanActual().getValue();
        if (plan == null) {
            Toast.makeText(getContext(), "Primero debes generar o tener un plan activo", Toast.LENGTH_SHORT).show();
            return;
        }

        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_comida, null);
        AutoCompleteTextView spinnerMomento = dialogView.findViewById(R.id.dialog_spinner_momento);
        TextInputEditText etNombre = dialogView.findViewById(R.id.dialog_et_nombre);
        TextInputEditText etCalorias = dialogView.findViewById(R.id.dialog_et_calorias);
        TextInputEditText etProt = dialogView.findViewById(R.id.dialog_et_prot);
        TextInputEditText etCarbs = dialogView.findViewById(R.id.dialog_et_carbs);
        TextInputEditText etGrasas = dialogView.findViewById(R.id.dialog_et_grasas);

        String[] momentos = {"DESAYUNO", "ALMUERZO", "COMIDA", "MERIENDA", "CENA"};
        spinnerMomento.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, momentos));

        new com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
                .setView(dialogView)
                .setPositiveButton("Guardar", (dialog, which) -> {
                    try {
                        com.example.gymcrush.data.remote.dto.ComidaDiariaRequestDto request = new com.example.gymcrush.data.remote.dto.ComidaDiariaRequestDto();
                        request.setMomento(spinnerMomento.getText().toString());
                        request.setNombreAlimento(etNombre.getText().toString());
                        request.setCalorias(Double.parseDouble(etCalorias.getText().toString()));
                        request.setProteina(Double.parseDouble(etProt.getText().toString()));
                        request.setCarbohidratos(Double.parseDouble(etCarbs.getText().toString()));
                        request.setGrasas(Double.parseDouble(etGrasas.getText().toString()));
                        request.setPlanNutricionalId(plan.getId());

                        viewModel.añadirComida(request, usuarioId);
                    } catch (Exception e) {
                        Toast.makeText(getContext(), "Error en los datos introducidos", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void mostrarDialogoBusqueda() {
        NutricionResponseDto plan = viewModel.getPlanActual().getValue();
        if (plan == null) {
            Toast.makeText(getContext(), "Primero debes generar o tener un plan activo", Toast.LENGTH_SHORT).show();
            return;
        }

        // Limpiar resultados anteriores al abrir el diálogo
        viewModel.limpiarBusqueda();

        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_search_food, null);
        androidx.appcompat.widget.SearchView searchView = dialogView.findViewById(R.id.searchViewFood);
        searchView.setSuggestionsAdapter(null); // Previene crashes externos de OneSearchSuggestProvider
        AutoCompleteTextView spinnerMomento = dialogView.findViewById(R.id.spinnerMomentoBusqueda);
        android.widget.ProgressBar progressBar = dialogView.findViewById(R.id.pbSearchLoading);
        RecyclerView rvResults = dialogView.findViewById(R.id.rvFoodSearchResults);
        TextView tvNoResults = dialogView.findViewById(R.id.tvNoSearchResults);

        String[] momentos = {"DESAYUNO", "ALMUERZO", "COMIDA", "MERIENDA", "CENA"};
        spinnerMomento.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, momentos));
        spinnerMomento.setText(momentos[0], false);
        spinnerMomento.setOnClickListener(v -> spinnerMomento.showDropDown());

        FoodSearchAdapter searchAdapter = new FoodSearchAdapter();
        rvResults.setAdapter(searchAdapter);
        rvResults.setLayoutManager(new LinearLayoutManager(getContext()));

        // Observar resultados específicos para este diálogo
        viewModel.getResultadosBusqueda().observe(getViewLifecycleOwner(), items -> {
            if (items != null) {
                searchAdapter.setItems(items);
                // Mostrar "No hay resultados" solo si la búsqueda terminó y está vacía
                boolean showNoResults = items.isEmpty() && !searchView.getQuery().toString().isEmpty();
                tvNoResults.setVisibility(showNoResults ? View.VISIBLE : View.GONE);
                if (showNoResults) {
                    tvNoResults.setText(R.string.no_results_found);
                }
            }
        });

        viewModel.getLoading().observe(getViewLifecycleOwner(), loading -> {
            progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
            if (loading) tvNoResults.setVisibility(View.GONE);
        });

        // Observar errores para mostrarlos en el diálogo
        viewModel.getError().observe(getViewLifecycleOwner(), errorMsg -> {
            if (errorMsg != null && !errorMsg.isEmpty() && searchView.hasFocus()) {
                tvNoResults.setText(errorMsg);
                tvNoResults.setVisibility(View.VISIBLE);
            }
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
                if (newText.isEmpty()) {
                    tvNoResults.setVisibility(View.GONE);
                }
                return false;
            }
        });

        new com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext(), R.style.MaterialAlertDialog_Rounded)
                .setView(dialogView)
                .setPositiveButton("Añadir", (dialog, which) -> {
                    java.util.List<com.example.gymcrush.data.remote.dto.ComidaDiariaRequestDto> seleccionados = searchAdapter.getSelectedItems();
                    if (!seleccionados.isEmpty()) {
                        String momento = spinnerMomento.getText().toString();
                        for (com.example.gymcrush.data.remote.dto.ComidaDiariaRequestDto c : seleccionados) {
                            c.setPlanNutricionalId(plan.getId());
                            c.setMomento(momento);
                        }
                        viewModel.guardarMuchasComidas(plan.getId(), seleccionados, usuarioId);
                    } else {
                        Toast.makeText(getContext(), "No has seleccionado ningún alimento", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cerrar", null)
                .setOnDismissListener(dialog -> {
                    viewModel.getResultadosBusqueda().removeObservers(getViewLifecycleOwner());
                    viewModel.getLoading().removeObservers(getViewLifecycleOwner());
                    // No removemos el de error global para no romper otros flujos, pero podríamos
                })
                .show();
    }

    private void configurarDropdowns() {
        String[] niveles = {"SEDENTARIO", "LIGERO", "MODERADO", "INTENSO", "ATLETA"};
        String[] objetivos = {"PERDER_GRASA", "MANTENER", "GANAR_MUSCULO"};
        String[] generos = {"MASCULINO", "FEMENINO"};

        ArrayAdapter<String> adapterNiveles = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, niveles);
        spinnerActividad.setAdapter(adapterNiveles);

        ArrayAdapter<String> adapterObjetivos = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, objetivos);
        spinnerObjetivo.setAdapter(adapterObjetivos);

        // Agregamos el spinner de género si existe en el XML o lo manejamos por defecto
        if (spinnerGenero != null) {
            ArrayAdapter<String> adapterGeneros = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, generos);
            spinnerGenero.setAdapter(adapterGeneros);
            spinnerGenero.setOnClickListener(v -> spinnerGenero.showDropDown());
        }
        
        // Forzar que el dropdown se muestre al hacer click
        spinnerActividad.setOnClickListener(v -> spinnerActividad.showDropDown());
        spinnerObjetivo.setOnClickListener(v -> spinnerObjetivo.showDropDown());
    }
}
