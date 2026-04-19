package com.example.gymcrush.ui.salud;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gymcrush.R;
import com.example.gymcrush.data.remote.dto.SaludResponseDto;

import java.util.ArrayList;
import java.util.List;

public class HistorialSaludFragment extends Fragment {

    private RecyclerView rvHistorial;
    private HistorialAdapter adapter;
    private SaludViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_historial_salud, container, false);

        viewModel = new ViewModelProvider(requireActivity()).get(SaludViewModel.class);

        ImageView btnBack = view.findViewById(R.id.btnBackHistorial);
        rvHistorial = view.findViewById(R.id.rvHistorial);

        btnBack.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });

        setupRecyclerView();
        observarViewModel();

        return view;
    }

    private void setupRecyclerView() {
        adapter = new HistorialAdapter(new ArrayList<>());
        rvHistorial.setLayoutManager(new LinearLayoutManager(getContext()));
        rvHistorial.setAdapter(adapter);
    }

    private void observarViewModel() {
        viewModel.getHistorialSalud().observe(getViewLifecycleOwner(), historial -> {
            if (historial != null) {
                adapter.setHistorial(historial);
            }
        });
    }
}
