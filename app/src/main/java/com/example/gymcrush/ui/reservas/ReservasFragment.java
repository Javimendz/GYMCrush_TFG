package com.example.gymcrush.ui.reservas;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gymcrush.R;
import com.example.gymcrush.data.model.Clase;
import com.example.gymcrush.data.remote.RetrofitClient;
import com.example.gymcrush.data.remote.dto.ApiResponseDto;
import com.example.gymcrush.data.remote.dto.HorarioResponseDto;
import com.example.gymcrush.data.remote.dto.ReservaResponseDto;
import com.example.gymcrush.data.repository.ReservasRepository;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReservasFragment extends Fragment implements ReservasAdapter.OnReservaClickListener {

    private RecyclerView rvReservas;
    private ReservasAdapter adapter;
    private LinearLayout layoutWeeklyDays;
    private String diaSeleccionadoCodigo = "LUNES"; // Por defecto hoy o Lunes
    private FloatingActionButton fabAddReserva;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reservas, container, false);

        rvReservas = view.findViewById(R.id.rvReservas);
        rvReservas.setLayoutManager(new LinearLayoutManager(getContext()));
        layoutWeeklyDays = view.findViewById(R.id.layoutWeeklyDays);
        fabAddReserva = view.findViewById(R.id.fabAddReserva);

        // Detectar día de hoy para selección inicial
        diaSeleccionadoCodigo = getDiaDeHoy();

        configurarCalendarioSemanal(inflater);
        actualizarListaPorDia(diaSeleccionadoCodigo);

        fabAddReserva.setOnClickListener(v -> abrirDialogoNuevaReserva());

        checkRoleAndSetupUI(view);

        return view;
    }

    private void checkRoleAndSetupUI(View view) {
        SharedPreferences prefs = requireContext().getSharedPreferences("DatosUsuario", Context.MODE_PRIVATE);
        Set<String> roles = prefs.getStringSet("roles", new HashSet<>());
        boolean isAdmin = roles.contains("ROLE_ADMIN") || roles.contains("ADMIN");

        if (isAdmin) {
            view.findViewById(R.id.tvTituloCitas).setVisibility(View.VISIBLE);
            ((TextView)view.findViewById(R.id.tvTituloCitas)).setText("Gestión de Reservas Global");
        }
    }

    private void abrirDialogoNuevaReserva() {
        // Al pulsar "+", mostramos los horarios disponibles para el día seleccionado
        // para que el usuario pueda elegir uno y reservar.
        
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_seleccionar_clase, null);
        RecyclerView rvClasesDisponibles = dialogView.findViewById(R.id.rvClasesDisponibles);
        rvClasesDisponibles.setLayoutManager(new LinearLayoutManager(getContext()));
        
        android.app.AlertDialog dialog = new android.app.AlertDialog.Builder(requireContext())
                .setTitle("Reservar en " + diaSeleccionadoCodigo)
                .setView(dialogView)
                .setNegativeButton("Cerrar", null)
                .create();

        // Cargar horarios del día
        ReservasRepository.getInstance().getHorariosPorDia(diaSeleccionadoCodigo, new ReservasRepository.RepositoryCallback<List<HorarioResponseDto>>() {
            @Override
            public void onSuccess(List<HorarioResponseDto> result) {
                // Adaptador simple o reutilizar ClasesAdapter
                List<Clase> clases = new ArrayList<>();
                for (HorarioResponseDto h : result) {
                    int disponibles = (h.getAforoMax() != null ? h.getAforoMax() : 0) - h.getOcupacion();
                    clases.add(new Clase(h.getId(), h.getNombreActividad(), h.getHoraInicio() + " - " + h.getHoraFin(), h.getNombreSala(), disponibles, h.getDiaSemana()));
                }
                
                com.example.gymcrush.ui.clases.ClasesAdapter clasesAdapter = new com.example.gymcrush.ui.clases.ClasesAdapter(clases, new com.example.gymcrush.ui.clases.ClasesAdapter.OnClaseClickListener() {
                    @Override
                    public void onApuntarseClick(Clase clase) {
                        confirmarReserva(clase, dialog);
                    }

                    @Override
                    public void onEliminarClick(Clase clase) {
                        // El admin podría eliminar desde aquí también si quisiéramos
                    }
                });
                rvClasesDisponibles.setAdapter(clasesAdapter);
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(getContext(), "Error al cargar horarios: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    private void confirmarReserva(Clase clase, android.app.AlertDialog parentDialog) {
        SharedPreferences prefs = requireContext().getSharedPreferences("DatosUsuario", Context.MODE_PRIVATE);
        long userId = prefs.getLong("user_id", -1);

        ReservasRepository.getInstance().crearReserva(userId, clase.getId(), new ReservasRepository.RepositoryCallback<ReservaResponseDto>() {
            @Override
            public void onSuccess(ReservaResponseDto result) {
                Toast.makeText(getContext(), "Reserva confirmada!", Toast.LENGTH_SHORT).show();
                parentDialog.dismiss();
                actualizarListaPorDia(diaSeleccionadoCodigo);
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(getContext(), "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getDiaDeHoy() {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        switch (day) {
            case Calendar.MONDAY: return "LUNES";
            case Calendar.TUESDAY: return "MARTES";
            case Calendar.WEDNESDAY: return "MIERCOLES";
            case Calendar.THURSDAY: return "JUEVES";
            case Calendar.FRIDAY: return "VIERNES";
            case Calendar.SATURDAY: return "SABADO";
            case Calendar.SUNDAY: return "DOMINGO";
            default: return "LUNES";
        }
    }

    private void configurarCalendarioSemanal(LayoutInflater inflater) {
        layoutWeeklyDays.removeAllViews();
        
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY); // Empezamos en Lunes

        String[] nombresDiasCortos = {"Lu", "Ma", "Mi", "Ju", "Vi", "Sa", "Do"};
        String[] codigosDias = {"LUNES", "MARTES", "MIERCOLES", "JUEVES", "VIERNES", "SABADO", "DOMINGO"};

        for (int i = 0; i < 7; i++) {
            View diaView = inflater.inflate(R.layout.item_calendario_dia, layoutWeeklyDays, false);
            TextView tvNombre = diaView.findViewById(R.id.tvNombreDia);
            TextView tvNumero = diaView.findViewById(R.id.tvNumeroDia);
            
            tvNombre.setText(nombresDiasCortos[i]);
            tvNumero.setText(String.valueOf(cal.get(Calendar.DAY_OF_MONTH)));

            final String codigo = codigosDias[i];
            final View container = diaView.findViewById(R.id.layoutDiaContainer);

            // Estilo inicial
            if (codigo.equals(diaSeleccionadoCodigo)) {
                resaltarDia(tvNumero);
            }

            diaView.setOnClickListener(v -> {
                // Limpiar otros resaltados
                for (int j = 0; j < layoutWeeklyDays.getChildCount(); j++) {
                    View vChild = layoutWeeklyDays.getChildAt(j);
                    TextView tvNum = vChild.findViewById(R.id.tvNumeroDia);
                    tvNum.setBackgroundResource(android.R.color.transparent);
                    tvNum.setTextColor(Color.parseColor("#333333"));
                }
                
                resaltarDia(tvNumero);
                diaSeleccionadoCodigo = codigo;
                actualizarListaPorDia(codigo);
            });

            layoutWeeklyDays.addView(diaView);
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }
    }

    private void resaltarDia(TextView tvNumero) {
        tvNumero.setBackgroundResource(R.drawable.dot_active); // Reutilizamos tu círculo neón
        tvNumero.setTextColor(Color.WHITE);
    }

    private void actualizarListaPorDia(String codigoDia) {
        SharedPreferences prefs = requireContext().getSharedPreferences("DatosUsuario", Context.MODE_PRIVATE);
        long userId = prefs.getLong("user_id", -1);
        Set<String> roles = prefs.getStringSet("roles", new HashSet<>());
        boolean isAdmin = roles.contains("ROLE_ADMIN") || roles.contains("ADMIN");

        if (userId == -1) return;

        // Si es admin, podríamos querer ver TODAS las reservas del sistema para ese día,
        // pero tu API actual 'getMisReservas(userId)' solo trae las del usuario.
        // Asumiremos que el admin quiere gestionar las suyas o que existe un endpoint de gestión global.
        
        ReservasRepository.getInstance().getMisReservas(userId, new ReservasRepository.RepositoryCallback<List<ReservaResponseDto>>() {
            @Override
            public void onSuccess(List<ReservaResponseDto> result) {
                List<ReservaResponseDto> filtradas = new ArrayList<>();
                for (ReservaResponseDto r : result) {
                    // Si el DTO de reserva tuviera el día, filtraríamos aquí.
                    // Por ahora mostramos las del usuario.
                    filtradas.add(r);
                }

                if (adapter == null) {
                    adapter = new ReservasAdapter(filtradas, ReservasFragment.this);
                    adapter.setAdminMode(isAdmin); // Pasar el rol al adaptador
                    rvReservas.setAdapter(adapter);
                } else {
                    adapter.setAdminMode(isAdmin);
                    adapter.setReservas(filtradas);
                }
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(getContext(), "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onCancelarClick(ReservaResponseDto reserva) {
        SharedPreferences prefs = requireContext().getSharedPreferences("DatosUsuario", Context.MODE_PRIVATE);
        Set<String> roles = prefs.getStringSet("roles", new HashSet<>());
        boolean isAdmin = roles.contains("ROLE_ADMIN") || roles.contains("ADMIN");

        if (isAdmin) {
            mostrarOpcionesGestionAdmin(reserva);
        } else {
            confirmarCancelacionUsuario(reserva);
        }
    }

    private void mostrarOpcionesGestionAdmin(ReservaResponseDto reserva) {
        String[] opciones = {"Confirmar Asistencia", "Cancelar Reserva (Admin)", "Ver Detalles Usuario"};
        new com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
                .setTitle("Gestión de Reserva: " + reserva.getId())
                .setItems(opciones, (dialog, which) -> {
                    if (which == 0) {
                        confirmarAsistencia(reserva);
                    } else if (which == 1) {
                        confirmarCancelacionUsuario(reserva);
                    } else {
                        Toast.makeText(getContext(), "Usuario ID: " + reserva.getUsuarioId(), Toast.LENGTH_SHORT).show();
                    }
                })
                .show();
    }

    private void confirmarAsistencia(ReservaResponseDto reserva) {
        RetrofitClient.getApiService().confirmarAsistencia(reserva.getId()).enqueue(new Callback<ApiResponseDto<Void>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<Void>> call, Response<ApiResponseDto<Void>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Asistencia confirmada", Toast.LENGTH_SHORT).show();
                }
            }
            @Override public void onFailure(Call<ApiResponseDto<Void>> call, Throwable t) {}
        });
    }

    private void confirmarCancelacionUsuario(ReservaResponseDto reserva) {
        new com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
                .setTitle("Cancelar Reserva")
                .setMessage("¿Estás seguro de que deseas cancelar esta reserva?")
                .setPositiveButton("Sí, cancelar", (dialog, which) -> {
                    ReservasRepository.getInstance().cancelarReserva(reserva.getId(), new ReservasRepository.RepositoryCallback<Void>() {
                        @Override
                        public void onSuccess(Void result) {
                            Toast.makeText(getContext(), "Reserva cancelada con éxito", Toast.LENGTH_SHORT).show();
                            actualizarListaPorDia(diaSeleccionadoCodigo);
                        }

                        @Override
                        public void onError(String errorMessage) {
                            Toast.makeText(getContext(), "Error al cancelar: " + errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("No", null)
                .show();
    }
}