package com.example.gymcrush.ui.clases;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import com.example.gymcrush.data.model.Clase;
import com.example.gymcrush.data.remote.RetrofitClient;
import com.example.gymcrush.data.remote.dto.ApiResponseDto;
import com.example.gymcrush.data.remote.dto.HorarioResponseDto;
import com.example.gymcrush.data.remote.dto.ReservaResponseDto;
import com.example.gymcrush.data.remote.dto.SalaRequestDto;
import com.example.gymcrush.data.remote.dto.SalaResponseDto;
import com.example.gymcrush.data.repository.ReservasRepository;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ClasesFragment extends Fragment implements ClasesAdapter.OnClaseClickListener {

    private RecyclerView rvClases;
    private ClasesAdapter adapter;
    private ClasesViewModel viewModel;
    private List<MaterialButton> botonesDias;
    private FloatingActionButton fabAddClase;
    private String diaActual = "LUNES";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_clases, container, false);

        viewModel = new ViewModelProvider(this).get(ClasesViewModel.class);

        rvClases = view.findViewById(R.id.rvClases);
        rvClases.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new ClasesAdapter(new ArrayList<>(), this);
        rvClases.setAdapter(adapter);

        configurarBotonesDias(view);
        setupObservers();

        fabAddClase = view.findViewById(R.id.fabAddClase);
        checkAdminRole();
        fabAddClase.setOnClickListener(v -> mostrarOpcionesGestionAdmin());

        // Intentar cargar el día actual de la semana en español
        diaActual = getDiaDeHoy();
        marcarBotonDiaActual(view);
        viewModel.cargarHorariosPorDia(diaActual);

        return view;
    }

    private String getDiaDeHoy() {
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        int day = calendar.get(java.util.Calendar.DAY_OF_WEEK);
        switch (day) {
            case java.util.Calendar.MONDAY: return "LUNES";
            case java.util.Calendar.TUESDAY: return "MARTES";
            case java.util.Calendar.WEDNESDAY: return "MIERCOLES";
            case java.util.Calendar.THURSDAY: return "JUEVES";
            case java.util.Calendar.FRIDAY: return "VIERNES";
            case java.util.Calendar.SATURDAY: return "SABADO";
            case java.util.Calendar.SUNDAY: return "DOMINGO";
            default: return "LUNES";
        }
    }

    private void marcarBotonDiaActual(View view) {
        String diaSimplificado = diaActual;
        int btnId = R.id.btnLunes;
        if (diaSimplificado.equals("MARTES")) btnId = R.id.btnMartes;
        else if (diaSimplificado.equals("MIERCOLES")) btnId = R.id.btnMiercoles;
        else if (diaSimplificado.equals("JUEVES")) btnId = R.id.btnJueves;
        else if (diaSimplificado.equals("VIERNES")) btnId = R.id.btnViernes;
        
        MaterialButton btn = view.findViewById(btnId);
        if (btn != null) actualizarEstiloBotones(btn);
    }

    private void checkAdminRole() {
        SharedPreferences prefs = requireContext().getSharedPreferences("DatosUsuario", Context.MODE_PRIVATE);
        Set<String> roles = prefs.getStringSet("roles", new HashSet<>());
        if (roles.contains("ROLE_ADMIN") || roles.contains("ADMIN")) {
            fabAddClase.setVisibility(View.VISIBLE);
            if (adapter != null) adapter.setAdminMode(true);
        } else {
            fabAddClase.setVisibility(View.GONE);
            if (adapter != null) adapter.setAdminMode(false);
        }
    }

    private void setupObservers() {
        viewModel.getHorarios().observe(getViewLifecycleOwner(), horarios -> {
            List<Clase> listaMapeada = new ArrayList<>();
            for (HorarioResponseDto h : horarios) {
                int aforo = h.getAforoMax() != null ? h.getAforoMax() : 0;
                int ocupacion = h.getOcupacion();
                int disponibles = Math.max(0, aforo - ocupacion);
                
                listaMapeada.add(new Clase(
                        h.getId(),
                        h.getNombreActividad(),
                        h.getHoraInicio() + " - " + h.getHoraFin(),
                        h.getNombreSala(),
                        disponibles,
                        h.getDiaSemana()
                ));
            }
            adapter.setClases(listaMapeada);
        });

        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void configurarBotonesDias(View view) {
        botonesDias = new ArrayList<>();
        botonesDias.add(view.findViewById(R.id.btnLunes));
        botonesDias.add(view.findViewById(R.id.btnMartes));
        botonesDias.add(view.findViewById(R.id.btnMiercoles));
        botonesDias.add(view.findViewById(R.id.btnJueves));
        botonesDias.add(view.findViewById(R.id.btnViernes));

        String[] codigosDias = {"LUNES", "MARTES", "MIERCOLES", "JUEVES", "VIERNES"};

        for (int i = 0; i < botonesDias.size(); i++) {
            final int index = i;
            if (botonesDias.get(i) != null) {
                botonesDias.get(i).setOnClickListener(v -> {
                    actualizarEstiloBotones(botonesDias.get(index));
                    diaActual = codigosDias[index];
                    viewModel.cargarHorariosPorDia(diaActual);
                });
            }
        }
    }

    private void actualizarEstiloBotones(MaterialButton botonSeleccionado) {
        for (MaterialButton btn : botonesDias) {
            if (btn == null) continue;
            if (btn == botonSeleccionado) {
                btn.setStrokeColorResource(R.color.accent_color);
                btn.setAlpha(1.0f);
            } else {
                btn.setStrokeColorResource(android.R.color.transparent);
                btn.setAlpha(0.6f);
            }
            btn.setTextColor(Color.WHITE);
        }
    }

    private void mostrarOpcionesGestionAdmin() {
        String[] opciones = {"Nueva Clase (Horario)", "Gestionar Salas (Crear/Eliminar)"};
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Panel de Administración")
                .setItems(opciones, (dialog, which) -> {
                    if (which == 0) {
                        abrirDialogoNuevaClase();
                    } else {
                        abrirDialogoGestionSalas();
                    }
                })
                .show();
    }

    private void abrirDialogoNuevaClase() {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_admin_nueva_sala, null);
        android.widget.EditText etHoraInicio = dialogView.findViewById(R.id.etNombreSala);
        android.widget.EditText etHoraFin = dialogView.findViewById(R.id.etCapacidadSala);
        android.widget.Spinner spinnerActividad = new android.widget.Spinner(getContext());
        android.widget.Spinner spinnerSala = new android.widget.Spinner(getContext());
        android.widget.Spinner spinnerDia = new android.widget.Spinner(getContext());

        com.google.android.material.textfield.TextInputLayout til1 = (com.google.android.material.textfield.TextInputLayout) etHoraInicio.getParent().getParent();
        com.google.android.material.textfield.TextInputLayout til2 = (com.google.android.material.textfield.TextInputLayout) etHoraFin.getParent().getParent();
        til1.setHint("Hora Inicio (HH:mm)");
        til2.setHint("Hora Fin (HH:mm)");
        etHoraInicio.setText("10:00");
        etHoraFin.setText("11:00");

        android.widget.LinearLayout layout = (android.widget.LinearLayout) dialogView;
        layout.setPadding(40, 20, 40, 20);

        String[] dias = {"LUNES", "MARTES", "MIERCOLES", "JUEVES", "VIERNES", "SABADO", "DOMINGO"};
        android.widget.ArrayAdapter<String> adapterDia = new android.widget.ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, dias);
        spinnerDia.setAdapter(adapterDia);
        layout.addView(crearLabel("Día de la semana:"));
        layout.addView(spinnerDia);

        layout.addView(crearLabel("Actividad:"));
        layout.addView(spinnerActividad);
        layout.addView(crearLabel("Sala:"));
        layout.addView(spinnerSala);

        // Cargar Actividades
        RetrofitClient.getApiService().getTodasLasActividades().enqueue(new Callback<ApiResponseDto<List<com.example.gymcrush.data.remote.dto.ActividadResponseDto>>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<List<com.example.gymcrush.data.remote.dto.ActividadResponseDto>>> call, Response<ApiResponseDto<List<com.example.gymcrush.data.remote.dto.ActividadResponseDto>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<com.example.gymcrush.data.remote.dto.ActividadResponseDto> lista = response.body().getDatos();
                    android.widget.ArrayAdapter<com.example.gymcrush.data.remote.dto.ActividadResponseDto> adapterAct = new android.widget.ArrayAdapter<com.example.gymcrush.data.remote.dto.ActividadResponseDto>(getContext(), android.R.layout.simple_spinner_item, lista) {
                        @Override public View getView(int p, View c, ViewGroup parent) {
                            TextView v = (TextView) super.getView(p, c, parent);
                            v.setText(getItem(p).getNombre());
                            return v;
                        }
                        @Override public View getDropDownView(int p, View c, ViewGroup parent) {
                            TextView v = (TextView) super.getDropDownView(p, c, parent);
                            v.setText(getItem(p).getNombre());
                            return v;
                        }
                    };
                    spinnerActividad.setAdapter(adapterAct);
                }
            }
            @Override public void onFailure(Call<ApiResponseDto<List<com.example.gymcrush.data.remote.dto.ActividadResponseDto>>> call, Throwable t) {}
        });

        // Cargar Salas
        RetrofitClient.getApiService().getTodasLasSalas().enqueue(new Callback<ApiResponseDto<List<SalaResponseDto>>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<List<SalaResponseDto>>> call, Response<ApiResponseDto<List<SalaResponseDto>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<SalaResponseDto> lista = response.body().getDatos();
                    android.widget.ArrayAdapter<SalaResponseDto> adapterSala = new android.widget.ArrayAdapter<SalaResponseDto>(getContext(), android.R.layout.simple_spinner_item, lista) {
                        @Override public View getView(int p, View c, ViewGroup parent) {
                            TextView v = (TextView) super.getView(p, c, parent);
                            v.setText(getItem(p).getNombre());
                            return v;
                        }
                        @Override public View getDropDownView(int p, View c, ViewGroup parent) {
                            TextView v = (TextView) super.getDropDownView(p, c, parent);
                            v.setText(getItem(p).getNombre());
                            return v;
                        }
                    };
                    spinnerSala.setAdapter(adapterSala);
                }
            }
            @Override public void onFailure(Call<ApiResponseDto<List<SalaResponseDto>>> call, Throwable t) {}
        });

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Nuevo Horario")
                .setView(layout)
                .setPositiveButton("Crear", (dialog, which) -> {
                    String hIn = etHoraInicio.getText().toString();
                    String hOut = etHoraFin.getText().toString();
                    String dia = (String) spinnerDia.getSelectedItem();
                    com.example.gymcrush.data.remote.dto.ActividadResponseDto act = (com.example.gymcrush.data.remote.dto.ActividadResponseDto) spinnerActividad.getSelectedItem();
                    SalaResponseDto sala = (SalaResponseDto) spinnerSala.getSelectedItem();

                    if (hIn.isEmpty() || hOut.isEmpty() || act == null || sala == null) return;

                    com.example.gymcrush.data.remote.dto.HorarioRequestDto request = new com.example.gymcrush.data.remote.dto.HorarioRequestDto(
                            dia, hIn, hOut, sala.getCapacidadMax(), act.getId(), sala.getId()
                    );

                    RetrofitClient.getApiService().crearHorario(request).enqueue(new Callback<ApiResponseDto<HorarioResponseDto>>() {
                        @Override
                        public void onResponse(Call<ApiResponseDto<HorarioResponseDto>> call, Response<ApiResponseDto<HorarioResponseDto>> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(getContext(), "Horario creado con éxito", Toast.LENGTH_SHORT).show();
                                viewModel.cargarHorariosPorDia(diaActual);
                            } else {
                                try {
                                    String errorMsg = "Error al crear: " + response.code();
                                    if (response.errorBody() != null) {
                                        errorMsg += " " + response.errorBody().string();
                                    }
                                    Toast.makeText(getContext(), errorMsg, Toast.LENGTH_LONG).show();
                                } catch (Exception e) {
                                    Toast.makeText(getContext(), "Error desconocido al crear horario", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                        @Override 
                        public void onFailure(Call<ApiResponseDto<HorarioResponseDto>> call, Throwable t) {
                            Toast.makeText(getContext(), "Fallo de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private TextView crearLabel(String texto) {
        TextView tv = new TextView(getContext());
        tv.setText(texto);
        tv.setPadding(0, 20, 0, 5);
        tv.setTextColor(Color.GRAY);
        return tv;
    }

    private void abrirDialogoGestionSalas() {
        RetrofitClient.getApiService().getTodasLasSalas().enqueue(new Callback<ApiResponseDto<List<SalaResponseDto>>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<List<SalaResponseDto>>> call, Response<ApiResponseDto<List<SalaResponseDto>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    mostrarListaSalas(response.body().getDatos());
                }
            }
            @Override
            public void onFailure(Call<ApiResponseDto<List<SalaResponseDto>>> call, Throwable t) {
                Toast.makeText(getContext(), "Error al cargar salas", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mostrarListaSalas(List<SalaResponseDto> salas) {
        String[] nombres = new String[salas.size() + 1];
        for (int i = 0; i < salas.size(); i++) {
            nombres[i] = salas.get(i).getNombre() + " (" + salas.get(i).getCapacidadMax() + "p)";
        }
        nombres[salas.size()] = "➕ Añadir Nueva Sala";

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Gestión de Salas")
                .setItems(nombres, (dialog, which) -> {
                    if (which == salas.size()) {
                        abrirDialogoCrearSala();
                    } else {
                        confirmarEliminarSala(salas.get(which));
                    }
                })
                .show();
    }

    private void abrirDialogoCrearSala() {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_admin_nueva_sala, null);
        android.widget.EditText etNombre = dialogView.findViewById(R.id.etNombreSala);
        android.widget.EditText etCapacidad = dialogView.findViewById(R.id.etCapacidadSala);

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Nueva Sala")
                .setView(dialogView)
                .setPositiveButton("Crear", (dialog, which) -> {
                    String nombre = etNombre.getText().toString();
                    String capStr = etCapacidad.getText().toString();
                    if (nombre.isEmpty() || capStr.isEmpty()) return;
                    
                    int cap = Integer.parseInt(capStr);
                    SalaRequestDto request = new SalaRequestDto(nombre, cap, "Sala de entrenamiento", "Planta 1", "Equipamiento estándar", true);
                    
                    RetrofitClient.getApiService().crearSala(request).enqueue(new Callback<ApiResponseDto<SalaResponseDto>>() {
                        @Override
                        public void onResponse(Call<ApiResponseDto<SalaResponseDto>> call, Response<ApiResponseDto<SalaResponseDto>> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(getContext(), "Sala creada", Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override
                        public void onFailure(Call<ApiResponseDto<SalaResponseDto>> call, Throwable t) {}
                    });
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void confirmarEliminarSala(SalaResponseDto sala) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Eliminar Sala")
                .setMessage("¿Estás seguro de eliminar la sala: " + sala.getNombre() + "?")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    RetrofitClient.getApiService().eliminarSala(sala.getId()).enqueue(new Callback<ApiResponseDto<Void>>() {
                        @Override
                        public void onResponse(Call<ApiResponseDto<Void>> call, Response<ApiResponseDto<Void>> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(getContext(), "Sala eliminada", Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override
                        public void onFailure(Call<ApiResponseDto<Void>> call, Throwable t) {}
                    });
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    @Override
    public void onApuntarseClick(Clase clase) {
        if (clase.getCuposDisponibles() <= 0) {
            Toast.makeText(getContext(), "Lo sentimos, no quedan plazas libres", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences prefs = requireContext().getSharedPreferences("DatosUsuario", Context.MODE_PRIVATE);
        long userId = prefs.getLong("user_id", -1);
        if (userId == -1) {
            Toast.makeText(getContext(), "Error: Usuario no identificado", Toast.LENGTH_SHORT).show();
            return;
        }

        Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.dialog_confirmar_reserva);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        TextView tvMensaje = dialog.findViewById(R.id.tv_mensaje_reserva);
        tvMensaje.setText("¿Deseas apuntarte a la clase de " + clase.getNombre() + " a las " + clase.getHora() + "?");

        dialog.findViewById(R.id.btn_cancelar_reserva).setOnClickListener(v -> dialog.dismiss());
        dialog.findViewById(R.id.btn_confirmar_reserva).setOnClickListener(v -> {
            viewModel.realizarReserva(userId, clase.getId(), new ReservasRepository.RepositoryCallback<ReservaResponseDto>() {
                @Override
                public void onSuccess(ReservaResponseDto result) {
                    Toast.makeText(getContext(), "¡Reserva realizada con éxito!", Toast.LENGTH_SHORT).show();
                    viewModel.cargarHorariosPorDia(diaActual);
                    dialog.dismiss();
                }

                @Override
                public void onError(String errorMessage) {
                    Toast.makeText(getContext(), "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            });
        });

        dialog.show();
    }

    @Override
    public void onEliminarClick(Clase clase) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Eliminar Clase")
                .setMessage("¿Estás seguro de eliminar esta clase?")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    RetrofitClient.getApiService().eliminarHorario(clase.getId()).enqueue(new Callback<ApiResponseDto<Void>>() {
                        @Override
                        public void onResponse(Call<ApiResponseDto<Void>> call, Response<ApiResponseDto<Void>> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(getContext(), "Clase eliminada", Toast.LENGTH_SHORT).show();
                                viewModel.cargarHorariosPorDia(diaActual);
                            }
                        }
                        @Override
                        public void onFailure(Call<ApiResponseDto<Void>> call, Throwable t) {}
                    });
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}
