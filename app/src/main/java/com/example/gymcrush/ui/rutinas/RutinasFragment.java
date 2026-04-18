package com.example.gymcrush.ui.rutinas;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.gymcrush.R;
import com.google.android.material.button.MaterialButton;

public class RutinasFragment extends Fragment {

    private RecyclerView rv;
    private RutinaGuardadaAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View vista = inflater.inflate(R.layout.rutina_fragment_lista, container, false);

        rv = vista.findViewById(R.id.rvMisRutinas);
        MaterialButton btn = vista.findViewById(R.id.btnIrACrear);

        // Configurar la lista
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setNestedScrollingEnabled(false); // Para que el scroll sea fluido con el NestedScrollView

        // Obtenemos las rutinas del repositorio rediseñado
        adapter = new RutinaGuardadaAdapter(RepositorioRutinas.getRutinas());
        rv.setAdapter(adapter);

        // Botón para ir a crear rutina
        btn.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), CrearRutinaActivity.class);
            startActivity(intent);
        });

        return vista;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }
}