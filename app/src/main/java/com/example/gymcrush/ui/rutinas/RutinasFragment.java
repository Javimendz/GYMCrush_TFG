package com.example.gymcrush.ui.rutinas;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.gymcrush.R;
import com.example.gymcrush.data.remote.RetrofitClient;
import com.example.gymcrush.data.remote.dto.ApiResponseDto;
import com.example.gymcrush.data.remote.dto.EntrenamientoRequestDto;
import com.example.gymcrush.data.remote.dto.EntrenamientoResponseDto;
import com.example.gymcrush.data.remote.dto.RutinaResponseDto;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RutinasFragment extends Fragment {

    private RecyclerView rvRutinas, rvEjercicios, rvRutinasGuardadas;
    private androidx.viewpager2.widget.ViewPager2 vpRutinasFavoritas;
    private com.google.android.material.tabs.TabLayout dotsIndicator;
    private RutinaApiAdapter rutinasAdapter;
    private EntrenamientoAdapter ejerciciosAdapter;
    private RutinaGuardadaAdapter rutinasGuardadasAdapter;
    private List<EntrenamientoResponseDto> listaEjerciciosCompleta = new ArrayList<>();
    
    private TabLayout tabLayoutPrincipal, tabLayoutCategorias;
    private NestedScrollView containerRutinas, containerEjercicios, containerRutinasGuardadas;
    private FrameLayout containerPlanes;
    private LinearLayout layoutVacio, layoutSelectorFecha;
    private android.widget.EditText etBuscador;
    private com.example.gymcrush.ui.salud.ProgressGraphView graphEvolucion;
    private TextView tvFechaSeleccionada, tvRacha;
    private MaterialButton btnCambiarFecha;
    private ExtendedFloatingActionButton fabAddEjercicio;
    private boolean isAdmin = false;
    private long userId;
    private LocalDate fechaSeleccionada = LocalDate.now();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View vista = inflater.inflate(R.layout.rutina_fragment_lista, container, false);

        SharedPreferences prefs = requireContext().getSharedPreferences("DatosUsuario", Context.MODE_PRIVATE);
        userId = prefs.getLong("user_id", -1);

        // Referencias
        rvRutinas = vista.findViewById(R.id.rvMisRutinas);
        rvEjercicios = vista.findViewById(R.id.rvEjerciciosRegistrados);
        rvRutinasGuardadas = vista.findViewById(R.id.rvRutinasGuardadas);
        vpRutinasFavoritas = vista.findViewById(R.id.vpRutinasFavoritas);
        dotsIndicator = vista.findViewById(R.id.dotsIndicator);
        
        View btnCrearRutina = vista.findViewById(R.id.btnIrACrear);
        // ... (el resto de inicializaciones se mantienen si existen en el layout)
        if (btnCrearRutina == null) {
            // Si reemplazamos la card del generador, este botón podría no existir
        }

        tabLayoutPrincipal = vista.findViewById(R.id.tabLayoutRutinas);
        tabLayoutCategorias = vista.findViewById(R.id.tabLayoutCategorias);
        containerRutinas = vista.findViewById(R.id.containerRutinas);
        containerEjercicios = vista.findViewById(R.id.containerEntrenamientos);
        containerPlanes = vista.findViewById(R.id.containerPlanes);
        containerRutinasGuardadas = vista.findViewById(R.id.containerRutinasGuardadas);
        layoutVacio = vista.findViewById(R.id.layoutEjerciciosVacio);
        fabAddEjercicio = vista.findViewById(R.id.fabAddEjercicioAdmin);
        
        layoutSelectorFecha = vista.findViewById(R.id.layoutSelectorFecha);
        tvFechaSeleccionada = vista.findViewById(R.id.tvFechaSeleccionada);
        btnCambiarFecha = vista.findViewById(R.id.btnCambiarFecha);
        etBuscador = vista.findViewById(R.id.etBuscarEjercicio);
        graphEvolucion = vista.findViewById(R.id.graphEvolucionRutinas);
        tvRacha = vista.findViewById(R.id.tvRacha);

        checkAdminRole();

        // 1. Configurar Rutinas de la Agenda (RecyclerView inferior)
        rvRutinas.setLayoutManager(new LinearLayoutManager(getContext()));
        rvRutinas.setNestedScrollingEnabled(false);
        rutinasAdapter = new RutinaApiAdapter(new ArrayList<>(), new RutinaApiAdapter.OnRutinaClickListener() {
            @Override
            public void onCompletar(RutinaResponseDto rutina) {
                marcarRutinaCompletada(rutina.getId());
            }

            @Override
            public void onEliminar(RutinaResponseDto rutina) {
                eliminarRutinaDeAgenda(rutina.getId());
            }
        });
        rvRutinas.setAdapter(rutinasAdapter);

        // 2. Configurar Carrusel de Rutinas (ViewPager2 superior)
        setupCarruselRutinas();

        // 3. Configurar Entrenamientos
        rvEjercicios.setLayoutManager(new LinearLayoutManager(getContext()));
        rvEjercicios.setNestedScrollingEnabled(false);
        ejerciciosAdapter = new EntrenamientoAdapter(new ArrayList<>());
        ejerciciosAdapter.setAdmin(isAdmin);
        // ...
        rvEjercicios.setAdapter(ejerciciosAdapter);

        // 4. Configurar Rutinas Guardadas (Pestaña "MIS RUTINAS")
        rvRutinasGuardadas.setLayoutManager(new LinearLayoutManager(getContext()));
        rutinasGuardadasAdapter = new RutinaGuardadaAdapter(RepositorioRutinas.getRutinas());
        rvRutinasGuardadas.setAdapter(rutinasGuardadasAdapter);

        setupBuscador();

        if (btnCrearRutina != null) {
            btnCrearRutina.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), CrearRutinaActivity.class);
                startActivity(intent);
            });
        }

        setupTabs();
        setupDatePicker();
        fabAddEjercicio.setOnClickListener(v -> mostrarDialogoNuevoEjercicio());

        actualizarUIFecha();
        cargarRutinaPorFecha();
        cargarEjerciciosDesdeServidor();
        cargarEstadisticas();

        return vista;
    }

    private void setupCarruselRutinas() {
        if (vpRutinasFavoritas == null) return;
        
        // Usamos las rutinas guardadas localmente para el carrusel
        // Se añade true para indicar que es para ViewPager2 (y use match_parent)
        RutinaGuardadaAdapter carruselAdapter = new RutinaGuardadaAdapter(RepositorioRutinas.getRutinas(), true);
        vpRutinasFavoritas.setAdapter(carruselAdapter);
        
        // Conectar TabLayout (dots) con ViewPager2
        new com.google.android.material.tabs.TabLayoutMediator(dotsIndicator, vpRutinasFavoritas, (tab, position) -> {}).attach();
    }

    private void eliminarRutinaDeAgenda(Long agendaId) {
        RetrofitClient.getApiService().eliminarRutina(agendaId).enqueue(new Callback<ApiResponseDto<Void>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<Void>> call, Response<ApiResponseDto<Void>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Ejercicio eliminado de la agenda", Toast.LENGTH_SHORT).show();
                    cargarRutinaPorFecha();
                }
            }

            @Override
            public void onFailure(Call<ApiResponseDto<Void>> call, Throwable t) {
                Toast.makeText(getContext(), "Error al eliminar", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupDatePicker() {
        btnCambiarFecha.setOnClickListener(v -> {
            android.app.DatePickerDialog datePicker = new android.app.DatePickerDialog(requireContext(),
                    (view, year, month, dayOfMonth) -> {
                        fechaSeleccionada = LocalDate.of(year, month + 1, dayOfMonth);
                        actualizarUIFecha();
                        cargarRutinaPorFecha();
                    },
                    fechaSeleccionada.getYear(),
                    fechaSeleccionada.getMonthValue() - 1,
                    fechaSeleccionada.getDayOfMonth());
            datePicker.show();
        });
    }

    private void actualizarUIFecha() {
        if (fechaSeleccionada.equals(LocalDate.now())) {
            tvFechaSeleccionada.setText(getString(R.string.fecha_hoy, fechaSeleccionada.toString()));
        } else {
            tvFechaSeleccionada.setText(getString(R.string.fecha_seleccionada, fechaSeleccionada.toString()));
        }
    }

    private void cargarRutinaPorFecha() {
        if (userId == -1) return;
        String fechaStr = fechaSeleccionada.toString();
        RetrofitClient.getApiService().getRutinaDiaria(userId, fechaStr).enqueue(new Callback<ApiResponseDto<List<RutinaResponseDto>>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<List<RutinaResponseDto>>> call, Response<ApiResponseDto<List<RutinaResponseDto>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    rutinasAdapter.updateList(response.body().getDatos());
                }
            }

            @Override
            public void onFailure(Call<ApiResponseDto<List<RutinaResponseDto>>> call, Throwable t) {
                Toast.makeText(getContext(), "Error al cargar agenda", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cargarRutinaDiaria() {
        fechaSeleccionada = LocalDate.now();
        actualizarUIFecha();
        cargarRutinaPorFecha();
    }

    private void cargarEstadisticas() {
        if (userId == -1) return;
        
        final Float[] puntos = new Float[7];
        final String[] etiquetas = new String[7];
        final AtomicInteger peticionesFinalizadas = new AtomicInteger(0);
        LocalDate hoy = LocalDate.now();

        for (int i = 0; i < 7; i++) {
            final int index = i;
            LocalDate dia = hoy.minusDays(6 - i);
            etiquetas[index] = dia.getDayOfMonth() + "/" + dia.getMonthValue();
            String fechaStr = dia.toString();
            
            RetrofitClient.getApiService().getRutinaDiaria(userId, fechaStr).enqueue(new Callback<ApiResponseDto<List<RutinaResponseDto>>>() {
                @Override
                public void onResponse(Call<ApiResponseDto<List<RutinaResponseDto>>> call, Response<ApiResponseDto<List<RutinaResponseDto>>> response) {
                    float completadas = 0;
                    if (response.isSuccessful() && response.body() != null) {
                        completadas = (float) response.body().getDatos().stream()
                                .filter(RutinaResponseDto::getCompletado)
                                .count();
                    }
                    puntos[index] = completadas;
                    verificarYActualizar();
                }

                @Override
                public void onFailure(Call<ApiResponseDto<List<RutinaResponseDto>>> call, Throwable t) {
                    puntos[index] = 0f;
                    verificarYActualizar();
                }

                private void verificarYActualizar() {
                    if (peticionesFinalizadas.incrementAndGet() == 7) {
                        List<Float> puntosList = Arrays.asList(puntos);
                        graphEvolucion.setData(puntosList, Arrays.asList(etiquetas));
                        calcularYMostrarRacha(puntos);
                    }
                }
            });
        }
    }

    private void calcularYMostrarRacha(Float[] puntos) {
        int racha = 0;
        // El índice 6 es 'hoy'. Recorremos hacia atrás.
        for (int i = 6; i >= 0; i--) {
            if (puntos[i] > 0) {
                racha++;
            } else if (i == 6) {
                // Si hoy es 0, la racha no se rompe aún (esperamos a que termine el día)
                continue;
            } else {
                // Se rompió la racha en algún día anterior
                break;
            }
        }
        
        String labelDia = (racha == 1) ? getString(R.string.dia) : getString(R.string.dias);
        tvRacha.setText(getString(R.string.racha_dias, racha, labelDia));
    }

    private void marcarRutinaCompletada(Long rutinaId) {
        // En lugar de llamar directamente a la API, primero verificamos el estado actual si fuera necesario,
        // pero el endpoint PATCH /completar suele ser un toggle o una acción de "marcar como hecho".
        // Si el backend lo trata como un toggle, servirá para ambas acciones.
        RetrofitClient.getApiService().completarRutina(rutinaId).enqueue(new Callback<ApiResponseDto<RutinaResponseDto>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<RutinaResponseDto>> call, Response<ApiResponseDto<RutinaResponseDto>> response) {
                if (response.isSuccessful()) {
                    String mensaje = "Estado de rutina actualizado";
                    if (response.body() != null && response.body().getDatos() != null) {
                        boolean completado = response.body().getDatos().getCompletado();
                        mensaje = completado ? "¡Felicidades! Entrenamiento completado" : "Rutina marcada como pendiente";
                    }
                    Toast.makeText(getContext(), mensaje, Toast.LENGTH_SHORT).show();
                    cargarRutinaPorFecha();
                    cargarEstadisticas();
                }
            }

            @Override
            public void onFailure(Call<ApiResponseDto<RutinaResponseDto>> call, Throwable t) {
                Toast.makeText(getContext(), "Error al actualizar estado", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkAdminRole() {
        SharedPreferences prefs = requireContext().getSharedPreferences("DatosUsuario", Context.MODE_PRIVATE);
        Set<String> roles = prefs.getStringSet("roles", new HashSet<>());
        isAdmin = roles.contains("ROLE_ADMIN") || roles.contains("ADMIN");
        if (ejerciciosAdapter != null) ejerciciosAdapter.setAdmin(isAdmin);
        actualizarVisibilidadFab(tabLayoutPrincipal.getSelectedTabPosition());
    }

    private void setupBuscador() {
        etBuscador.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filtrarPorNombre(s.toString());
            }
            @Override public void afterTextChanged(android.text.Editable s) {}
        });
    }

    private void filtrarPorNombre(String query) {
        List<EntrenamientoResponseDto> filtrada = listaEjerciciosCompleta.stream()
                .filter(e -> e.getNombre().toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());
        ejerciciosAdapter.updateLista(filtrada);
    }

    private void setupTabs() {
        tabLayoutPrincipal.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                actualizarVisibilidadFab(position);
                
                containerRutinas.setVisibility(View.GONE);
                containerPlanes.setVisibility(View.GONE);
                containerEjercicios.setVisibility(View.GONE);
                containerRutinasGuardadas.setVisibility(View.GONE);
                layoutSelectorFecha.setVisibility(View.GONE);

                if (position == 0) {
                    containerRutinas.setVisibility(View.VISIBLE);
                    layoutSelectorFecha.setVisibility(View.VISIBLE);
                } else if (position == 1) {
                    containerRutinasGuardadas.setVisibility(View.VISIBLE);
                    actualizarRutinasGuardadas();
                } else if (position == 2) {
                    containerPlanes.setVisibility(View.VISIBLE);
                    mostrarFragmentPlanes();
                } else {
                    containerEjercicios.setVisibility(View.VISIBLE);
                }
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });

        tabLayoutCategorias.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                filtrarPorCategoria(tab.getText().toString());
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void actualizarRutinasGuardadas() {
        if (rutinasGuardadasAdapter != null) {
            rutinasGuardadasAdapter.updateList(RepositorioRutinas.getRutinas());
        }
    }

    private void mostrarFragmentPlanes() {
        PlanesFragment fragment = new PlanesFragment();
        fragment.setOnPlanSubscriptionListener(() -> {
            // Cuando se suscribe a un plan, volvemos a la agenda y recargamos
            tabLayoutPrincipal.getTabAt(0).select();
            cargarRutinaPorFecha();
        });
        
        getChildFragmentManager().beginTransaction()
                .replace(R.id.containerPlanes, fragment)
                .commit();
    }

    private void actualizarVisibilidadFab(int tabPosition) {
        if (isAdmin && tabPosition == 2) fabAddEjercicio.show();
        else fabAddEjercicio.hide();
    }

    private void cargarEjerciciosDesdeServidor() {
        SharedPreferences prefs = requireContext().getSharedPreferences("DatosUsuario", Context.MODE_PRIVATE);
        long userId = prefs.getLong("user_id", -1);
        if (userId == -1) return;

        RetrofitClient.getApiService().getEntrenamientosUsuario(userId).enqueue(new Callback<ApiResponseDto<List<EntrenamientoResponseDto>>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<List<EntrenamientoResponseDto>>> call, Response<ApiResponseDto<List<EntrenamientoResponseDto>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listaEjerciciosCompleta = response.body().getDatos();
                    configurarFiltrosCategorias();
                }
            }

            @Override
            public void onFailure(Call<ApiResponseDto<List<EntrenamientoResponseDto>>> call, Throwable t) {
                Toast.makeText(getContext(), "Error al cargar entrenamientos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void configurarFiltrosCategorias() {
        tabLayoutCategorias.removeAllTabs();
        tabLayoutCategorias.addTab(tabLayoutCategorias.newTab().setText("TODOS"));

        Set<String> categorias = new HashSet<>();
        for (EntrenamientoResponseDto e : listaEjerciciosCompleta) {
            if (e.getCategoria() != null && !e.getCategoria().isEmpty()) {
                categorias.add(e.getCategoria().toUpperCase());
            }
        }

        for (String cat : categorias) {
            tabLayoutCategorias.addTab(tabLayoutCategorias.newTab().setText(cat));
        }

        filtrarPorCategoria("TODOS");
    }

    private void filtrarPorCategoria(String categoria) {
        List<EntrenamientoResponseDto> filtrada;
        if (categoria.equals("TODOS")) {
            filtrada = new ArrayList<>(listaEjerciciosCompleta);
        } else {
            filtrada = listaEjerciciosCompleta.stream()
                    .filter(e -> e.getCategoria() != null && e.getCategoria().equalsIgnoreCase(categoria))
                    .collect(Collectors.toList());
        }

        ejerciciosAdapter.updateLista(filtrada);
        
        if (filtrada.isEmpty()) {
            layoutVacio.setVisibility(View.VISIBLE);
            rvEjercicios.setVisibility(View.GONE);
        } else {
            layoutVacio.setVisibility(View.GONE);
            rvEjercicios.setVisibility(View.VISIBLE);
        }
    }

    private void mostrarDialogoNuevoEjercicio() {
        Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.dialog_admin_nuevo_ejercicio);
        if (dialog.getWindow() != null) dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextInputEditText etNombre = dialog.findViewById(R.id.etNombreEjercicio);
        TextInputEditText etDesc = dialog.findViewById(R.id.etDescripcionEjercicio);
        TextInputEditText etCat = dialog.findViewById(R.id.etCategoriaEjercicio);
        TextInputEditText etDur = dialog.findViewById(R.id.etDuracionEjercicio);
        TextInputEditText etInt = dialog.findViewById(R.id.etIntensidadEjercicio);
        MaterialButton btnGuardar = dialog.findViewById(R.id.btnGuardarEjercicio);

        btnGuardar.setOnClickListener(v -> {
            String nombre = etNombre.getText().toString().trim();
            String desc = etDesc.getText().toString().trim();
            String cat = etCat.getText().toString().trim();
            String durStr = etDur.getText().toString().trim();
            String intensidad = etInt.getText().toString().trim();

            if (nombre.isEmpty() || durStr.isEmpty() || cat.isEmpty()) {
                Toast.makeText(getContext(), "Por favor, rellena los campos básicos", Toast.LENGTH_SHORT).show();
                return;
            }

            EntrenamientoRequestDto request = new EntrenamientoRequestDto(
                nombre, desc, Integer.parseInt(durStr), intensidad, cat
            );

            RetrofitClient.getApiService().crearEntrenamiento(request).enqueue(new Callback<ApiResponseDto<EntrenamientoResponseDto>>() {
                @Override
                public void onResponse(Call<ApiResponseDto<EntrenamientoResponseDto>> call, Response<ApiResponseDto<EntrenamientoResponseDto>> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(getContext(), "Entrenamiento guardado", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        cargarEjerciciosDesdeServidor();
                    }
                }
                @Override public void onFailure(Call<ApiResponseDto<EntrenamientoResponseDto>> call, Throwable t) {}
            });
        });

        dialog.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (rutinasAdapter != null) rutinasAdapter.notifyDataSetChanged();
    }
}
