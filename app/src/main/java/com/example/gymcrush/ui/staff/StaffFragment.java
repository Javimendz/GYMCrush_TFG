package com.example.gymcrush.ui.staff;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gymcrush.R;
import com.example.gymcrush.data.remote.RetrofitClient;
import com.example.gymcrush.data.remote.dto.ApiResponseDto;
import com.example.gymcrush.data.remote.dto.PerfilResponseDto;
import com.example.gymcrush.data.remote.dto.RegisterRequest;
import com.example.gymcrush.data.remote.dto.UsuarioResponseDto;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StaffFragment extends Fragment {

    private RecyclerView rvStaff;
    private ProgressBar progressBar;
    private FloatingActionButton fabAdd;
    private StaffAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_staff, container, false);

        rvStaff = view.findViewById(R.id.rvStaff);
        progressBar = view.findViewById(R.id.pbStaff);
        fabAdd = view.findViewById(R.id.fabAddStaff);

        rvStaff.setLayoutManager(new LinearLayoutManager(getContext()));
        
        view.findViewById(R.id.btnBackStaff).setOnClickListener(v -> {
            if (getActivity() != null) getActivity().getSupportFragmentManager().popBackStack();
        });

        fabAdd.setOnClickListener(v -> mostrarDialogoNuevoEntrenador());

        cargarStaff();

        return view;
    }

    private void cargarStaff() {
        progressBar.setVisibility(View.VISIBLE);
        RetrofitClient.getApiService().findAllPerfiles().enqueue(new Callback<ApiResponseDto<List<PerfilResponseDto>>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<List<PerfilResponseDto>>> call, Response<ApiResponseDto<List<PerfilResponseDto>>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    adapter = new StaffAdapter(response.body().getDatos());
                    rvStaff.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<ApiResponseDto<List<PerfilResponseDto>>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Error al cargar staff", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mostrarDialogoNuevoEntrenador() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_admin_nuevo_staff, null);
        EditText etUsername = view.findViewById(R.id.etStaffUsername);
        EditText etNombre = view.findViewById(R.id.etStaffNombre);
        EditText etCorreo = view.findViewById(R.id.etStaffCorreo);
        EditText etPass = view.findViewById(R.id.etStaffPass);

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Nuevo Entrenador")
                .setView(view)
                .setPositiveButton("Crear", (dialog, which) -> {
                    String user = etUsername.getText().toString();
                    String nom = etNombre.getText().toString();
                    String mail = etCorreo.getText().toString();
                    String pass = etPass.getText().toString();

                    if (user.isEmpty() || nom.isEmpty() || mail.isEmpty() || pass.isEmpty()) {
                        Toast.makeText(getContext(), "Completa todos los campos", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    RegisterRequest request = new RegisterRequest();
                    request.setUsername(user);
                    request.setNombre(nom);
                    request.setCorreo(mail);
                    request.setPassword(pass);
                    request.setRoles(new HashSet<>(Collections.singletonList("entrenador")));

                    RetrofitClient.getApiService().register(request).enqueue(new Callback<ApiResponseDto<UsuarioResponseDto>>() {
                        @Override
                        public void onResponse(Call<ApiResponseDto<UsuarioResponseDto>> call, Response<ApiResponseDto<UsuarioResponseDto>> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(getContext(), "Entrenador creado", Toast.LENGTH_SHORT).show();
                                cargarStaff();
                            } else {
                                Toast.makeText(getContext(), "Error: " + response.message(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ApiResponseDto<UsuarioResponseDto>> call, Throwable t) {
                            Toast.makeText(getContext(), "Fallo de conexión", Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}
