package com.example.gymcrush.ui.dieta;

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
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.gymcrush.R;
import com.example.gymcrush.ui.custom.CustomDonutChart;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.example.gymcrush.data.remote.dto.ComidaDiariaResponseDto;
import com.example.gymcrush.data.remote.dto.NutricionResponseDto;
import com.example.gymcrush.data.remote.dto.PerfilFisicoRequest;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;

public class DietaFragment extends Fragment {

    private DietaViewModel viewModel;
    private TextInputEditText etPeso, etAltura, etEdad;
    private AutoCompleteTextView spinnerActividad, spinnerObjetivo;
    private MaterialButton btnActualizar, btnAddComida, btnBuscarComida, btnMarcarTodo;
    
    private MaterialCardView cardEmptySuggestion, cardSuggestion;
    private TextView tvNombreSugerida, tvCaloriasSugeridas, tvCaloriasRestantes, tvProt, tvCarbs, tvGrasas;
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

        viewModel = new ViewModelProvider(requireActivity()).get(DietaViewModel.class);
        
        SharedPreferences prefs = requireActivity().getSharedPreferences("GymCrushPrefs", Context.MODE_PRIVATE);
        usuarioId = prefs.getLong("usuarioId", -1L);
        if (usuarioId == -1L) {
            prefs = requireActivity().getSharedPreferences("DatosUsuario", Context.MODE_PRIVATE);
            usuarioId = prefs.getLong("user_id", -1L);
        }

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

        btnActualizar.setOnClickListener(v -> generarSugerencia());
        btnAddComida.setOnClickListener(v -> mostrarDialogoAñadirComida());
        btnBuscarComida.setOnClickListener(v -> mostrarDialogoBusqueda());
        btnMarcarTodo.setOnClickListener(v -> completarTodasLasComidas());

        view.findViewById(R.id.btnBackDieta).setOnClickListener(v -> {
            if (getActivity() != null) getActivity().onBackPressed();
        });

        return view;
    }

    private void initViews(View v) {
        etPeso = v.findViewById(R.id.etPeso);
        etAltura = v.findViewById(R.id.etAltura);
        etEdad = v.findViewById(R.id.etEdad);
        spinnerActividad = v.findViewById(R.id.spinnerActividad);
        spinnerObjetivo = v.findViewById(R.id.spinnerObjetivo);
        btnActualizar = v.findViewById(R.id.btnActualizarDatos);
        btnAddComida = v.findViewById(R.id.btnAddComidaManual);
        btnBuscarComida = v.findViewById(R.id.btnBuscarComida);
        btnMarcarTodo = v.findViewById(R.id.btnMarcarTodoCompletadoHome);

        cardEmptySuggestion = v.findViewById(R.id.cardEmptySuggestion);
        cardSuggestion = v.findViewById(R.id.cardSuggestion);
        tvNombreSugerida = v.findViewById(R.id.tvNombreDietaSugerida);
        tvCaloriasSugeridas = v.findViewById(R.id.tvCaloriasSugeridas);
        tvCaloriasRestantes = v.findViewById(R.id.tvCaloriasRestantes);
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
            NutricionResponseDto planSimulado = new NutricionResponseDto();
            planSimulado.setNombreDieta(dieta.getNombre());
            planSimulado.setTipoDieta(dieta.getTipo());
            planSimulado.setCaloriasObjetivo(dieta.getObjetivoCalorico().intValue());

            java.util.List<ComidaDiariaResponseDto> dietasSimuladas = new java.util.ArrayList<>();
            String[] momentos = {"DESAYUNO", "ALMUERZO", "COMIDA", "MERIENDA", "CENA"};
            String[] platosEjemplo = {"Avena con Frutas", "Sándwich Integral de Pavo", "Pollo con Arroz y Brócoli", "Batido de Proteína y Nueces", "Salmón al Horno con Espárragos"};
            String[] urlsEjemplo = {"https://www.fatsecret.es/recetas/avena-con-frutas/Default.aspx", "https://www.fatsecret.es/recetas/sandwich-de-pavo/Default.aspx", "https://www.fatsecret.es/recetas/pollo-con-arroz/Default.aspx", "https://www.fatsecret.es/recetas/batido-proteinas/Default.aspx", "https://www.fatsecret.es/recetas/salmon-horno/Default.aspx"};
            String[] imagenesEjemplo = {
                "https://images.unsplash.com/photo-1517673400267-0251440c45dc?q=80&w=500&auto=format&fit=crop",
                "https://images.unsplash.com/photo-1528733918455-5a59687cedf0?q=80&w=500&auto=format&fit=crop",
                "https://images.unsplash.com/photo-1604908176997-125f25cc6f3d?q=80&w=500&auto=format&fit=crop",
                "https://images.unsplash.com/photo-1574783756247-29f6c244a51d?q=80&w=500&auto=format&fit=crop",
                "https://images.unsplash.com/photo-1467003909585-2f8a72700288?q=80&w=500&auto=format&fit=crop"
            };
            
            for (int i = 0; i < momentos.length; i++) {
                ComidaDiariaResponseDto item = new ComidaDiariaResponseDto();
                item.setMomento(momentos[i]);
                item.setNombreAlimento(platosEjemplo[i]);
                item.setRecipeUrl(urlsEjemplo[i]);
                item.setImagenUrl(imagenesEjemplo[i]);
                item.setDescripcion("Una opción saludable y equilibrada diseñada para tu dieta " + dieta.getTipo().toLowerCase() + ".");
                item.setProteina(dieta.getCantidadProteinas() / 5);
                item.setCarbohidratos(dieta.getCantidadCarbohidratos() / 5);
                item.setGrasas(dieta.getCantidadGrasas().doubleValue() / 5);
                item.setCalorias(dieta.getObjetivoCalorico() / 5);
                dietasSimuladas.add(item);
            }
            planSimulado.setDietas(dietasSimuladas);

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
        comidaAdapter.setOnHechaClickListener((comida, isChecked) -> {
            NutricionResponseDto plan = viewModel.getPlanActual().getValue();
            if (plan != null) {
                if (isChecked && comida.getId() == null) {
                    com.example.gymcrush.data.remote.dto.ComidaDiariaRequestDto request = new com.example.gymcrush.data.remote.dto.ComidaDiariaRequestDto();
                    request.setNombreAlimento(comida.getNombreAlimento());
                    request.setMomento(comida.getMomento());
                    request.setCalorias(comida.getCalorias());
                    request.setProteina(comida.getProteina());
                    request.setCarbohidratos(comida.getCarbohidratos());
                    request.setGrasas(comida.getGrasas());
                    request.setPlanNutricionalId(plan.getId());
                    request.setImagenUrl(comida.getImagenUrl());
                    request.setDescripcion(comida.getDescripcion());
                    request.setRecipeUrl(comida.getRecipeUrl());
                    viewModel.añadirComida(request, usuarioId);
                } else if (!isChecked && comida.getId() != null) {
                    viewModel.eliminarComida(comida.getId(), plan.getId(), usuarioId);
                }
            }
            actualizarResumenMacros(getView());
            verificarYActualizarVisibilidadBotonCompletado();
            verificarTodasComidasCompletadas();
        });
        
        comidaAdapter.setOnComidaClickListener(comida -> {
            NutricionResponseDto plan = viewModel.getPlanActual().getValue();
            if (plan != null && plan.getMacronutrientes() != null && plan.getMacronutrientes().getRecipes() != null) {
                for (com.example.gymcrush.data.remote.dto.FatSecretRecipeDto recipe : plan.getMacronutrientes().getRecipes().getRecipe()) {
                    if (recipe.getRecipeName().trim().equalsIgnoreCase(comida.getNombreAlimento().trim())) {
                        comida.setDescripcion(recipe.getRecipeDescription());
                        comida.setRecipeUrl(recipe.getRecipeUrl());
                        if (recipe.getRecipeImage() != null) {
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

        comidaAdapter.setOnComidaLongClickListener(comida -> {
            new com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Eliminar plato")
                    .setMessage("¿Estás seguro de que quieres eliminar " + comida.getNombreAlimento() + " de tu plan?")
                    .setPositiveButton("Eliminar", (dialog, which) -> {
                        NutricionResponseDto currentPlan = viewModel.getPlanActual().getValue();
                        Long planId = (currentPlan != null) ? currentPlan.getId() : null;
                        viewModel.eliminarComida(comida.getId(), planId, usuarioId);
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        });
        
        rvComidas.setLayoutManager(new LinearLayoutManager(getContext()) {
            @Override public boolean canScrollVertically() { return false; }
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
        viewModel.getPlanActual().observe(getViewLifecycleOwner(), plan -> {
            if (plan != null) {
                cardEmptySuggestion.setVisibility(View.GONE);
                cardSuggestion.setVisibility(View.VISIBLE);
                mostrarPlan(plan);
                verificarYActualizarVisibilidadBotonCompletado();
                if (plan.getMacronutrientes() != null && plan.getMacronutrientes().getRecipes() != null) {
                    recetaAdapter.setRecetas(plan.getMacronutrientes().getRecipes().getRecipe());
                }
            } else {
                cardEmptySuggestion.setVisibility(View.VISIBLE);
                cardSuggestion.setVisibility(View.GONE);
                if (btnMarcarTodo != null) btnMarcarTodo.setVisibility(View.GONE);
            }
        });

        viewModel.getSaludActual().observe(getViewLifecycleOwner(), salud -> {
            if (salud != null) {
                if (salud.getPeso() != null) etPeso.setText(String.valueOf(salud.getPeso()));
                if (salud.getEstatura() != null) etAltura.setText(String.valueOf(salud.getEstatura()));
                if (salud.getNivelActividad() != null) spinnerActividad.setText(salud.getNivelActividad(), false);
            }
        });

        viewModel.getDietasCatalogo().observe(getViewLifecycleOwner(), dietas -> {
            if (dietas != null) adapter.setDietas(dietas);
        });

        viewModel.getHistorial().observe(getViewLifecycleOwner(), historial -> {
            if (historial != null) historialAdapter.setHistorial(historial);
        });

        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
        });
    }

    private void verificarTodasComidasCompletadas() {
        if (comidaAdapter == null || comidaAdapter.getComidas().isEmpty()) return;

        boolean todasCompletadas = true;
        for (ComidaDiariaResponseDto comida : comidaAdapter.getComidas()) {
            if (!comida.isSelected()) {
                todasCompletadas = false;
                break;
            }
        }

        if (todasCompletadas) {
            new com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
                    .setTitle("¡Felicidades!")
                    .setMessage("Has completado todas las comidas de hoy. ¿Quieres guardar este plan como completado en tu historial?")
                    .setPositiveButton("Guardar", (dialog, which) -> {
                        NutricionResponseDto plan = viewModel.getPlanActual().getValue();
                        if (plan != null && usuarioId != -1L) {
                            viewModel.guardarPlan(usuarioId, plan);
                        }
                        if (btnMarcarTodo != null) btnMarcarTodo.setVisibility(View.GONE);
                    })
                    .setNegativeButton("Cerrar", (dialog, which) -> {
                        if (btnMarcarTodo != null) btnMarcarTodo.setVisibility(View.GONE);
                    })
                    .setCancelable(false)
                    .show();
        }
    }

    private void verificarYActualizarVisibilidadBotonCompletado() {
        if (comidaAdapter == null || comidaAdapter.getComidas().isEmpty()) {
            if (btnMarcarTodo != null) btnMarcarTodo.setVisibility(View.GONE);
            return;
        }
        boolean todasCompletadas = true;
        for (ComidaDiariaResponseDto comida : comidaAdapter.getComidas()) {
            if (!comida.isSelected()) {
                todasCompletadas = false;
                break;
            }
        }
        if (btnMarcarTodo != null) btnMarcarTodo.setVisibility(todasCompletadas ? View.GONE : View.VISIBLE);
    }

    private void mostrarPlan(NutricionResponseDto plan) {
        String titulo = plan.getNombreDieta() != null && !plan.getNombreDieta().isEmpty() ? plan.getNombreDieta() : (plan.getTipoDieta() != null ? plan.getTipoDieta() : "PLAN PERSONALIZADO");
        tvNombreSugerida.setText(titulo);

        java.util.List<ComidaDiariaResponseDto> todasLasComidas = new java.util.ArrayList<>();
        if (plan.getDietas() != null) {
            for (ComidaDiariaResponseDto sugerencia : plan.getDietas()) {
                if (sugerencia.getId() != null) sugerencia.setSelected(true);
                todasLasComidas.add(sugerencia);
            }
        }
        if (plan.getComidas() != null) {
            for (ComidaDiariaResponseDto consumida : plan.getComidas()) {
                consumida.setSelected(true);
                todasLasComidas.add(consumida);
            }
        }
        comidaAdapter.setComidas(todasLasComidas);
        actualizarResumenMacros(getView());
    }

    private void completarTodasLasComidas() {
        NutricionResponseDto plan = viewModel.getPlanActual().getValue();
        if (plan == null || comidaAdapter == null || comidaAdapter.getComidas() == null) return;

        new com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
                .setTitle("Marcar todo como completado")
                .setMessage("¿Deseas marcar todas las comidas como consumidas y guardarlas en tu plan?")
                .setPositiveButton("Confirmar", (dialog, which) -> {
                    java.util.List<com.example.gymcrush.data.remote.dto.ComidaDiariaRequestDto> aGuardar = new java.util.ArrayList<>();
                    for (ComidaDiariaResponseDto comida : comidaAdapter.getComidas()) {
                        if (!comida.isSelected()) {
                            com.example.gymcrush.data.remote.dto.ComidaDiariaRequestDto req = new com.example.gymcrush.data.remote.dto.ComidaDiariaRequestDto();
                            req.setNombreAlimento(comida.getNombreAlimento());
                            req.setMomento(comida.getMomento());
                            req.setCalorias(comida.getCalorias());
                            req.setProteina(comida.getProteina());
                            req.setCarbohidratos(comida.getCarbohidratos());
                            req.setGrasas(comida.getGrasas());
                            req.setPlanNutricionalId(plan.getId());
                            req.setImagenUrl(comida.getImagenUrl());
                            req.setDescripcion(comida.getDescripcion());
                            req.setRecipeUrl(comida.getRecipeUrl());
                            aGuardar.add(req);
                            comida.setSelected(true);
                        }
                    }
                    if (!aGuardar.isEmpty()) {
                        viewModel.guardarMuchasComidas(plan.getId(), aGuardar, usuarioId);
                        comidaAdapter.notifyDataSetChanged();
                        actualizarResumenMacros(getView());
                        verificarYActualizarVisibilidadBotonCompletado();
                        verificarTodasComidasCompletadas();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void actualizarResumenMacros(View view) {
        if (view == null) return;
        NutricionResponseDto plan = viewModel.getPlanActual().getValue();
        if (plan == null) return;

        double protSum = 0, carbSum = 0, fatSum = 0, calSum = 0;
        for (ComidaDiariaResponseDto c : comidaAdapter.getComidas()) {
            if (c.isSelected()) {
                protSum += c.getProteina();
                carbSum += c.getCarbohidratos();
                fatSum += c.getGrasas();
                calSum += c.getCalorias();
            }
        }

        tvCaloriasSugeridas.setText(String.format("%.0f kcal consumidas", calSum));
        int calRest = Math.max(0, plan.getCaloriasObjetivo() - (int)calSum);
        tvCaloriasRestantes.setText(String.valueOf(calRest));
        tvProt.setText(String.format("%.0fg Proteína", protSum));
        tvCarbs.setText(String.format("%.0fg Carbos", carbSum));
        tvGrasas.setText(String.format("%.0fg Grasas", fatSum));

        progressCalorias.setProgress((int) ((calSum / plan.getCaloriasObjetivo()) * 100));
        donutChart.setData((float) protSum, (float) carbSum, (float) fatSum);
    }

    private void generarSugerencia() {
        try {
            PerfilFisicoRequest request = new PerfilFisicoRequest();
            request.setUsuarioId(usuarioId);
            request.setPeso(Double.parseDouble(etPeso.getText().toString()));
            request.setAltura(Double.parseDouble(etAltura.getText().toString()));
            request.setEdad(Integer.parseInt(etEdad.getText().toString()));
            request.setNivelActividad(spinnerActividad.getText().toString());
            request.setObjetivo(spinnerObjetivo.getText().toString().equals("PERDER_GRASA") ? "PERDER" : (spinnerObjetivo.getText().toString().equals("GANAR_MUSCULO") ? "GANAR" : "MANTENER"));
            viewModel.generarPlan(request);
        } catch (Exception e) {
            Toast.makeText(getContext(), "Error al generar sugerencia", Toast.LENGTH_SHORT).show();
        }
    }

    private void configurarDropdowns() {
        String[] actividades = {"SEDENTARIO", "LIGERO", "MODERADO", "INTENSO", "ATLETA"};
        spinnerActividad.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, actividades));
        String[] objetivos = {"PERDER_GRASA", "MANTENER", "GANAR_MUSCULO"};
        spinnerObjetivo.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, objetivos));
    }

    private void mostrarDialogoAñadirComida() { /* Implementación simplificada para brevedad */ }
    private void mostrarDialogoBusqueda() { /* Implementación simplificada para brevedad */ }
}