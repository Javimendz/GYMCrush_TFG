package com.example.gymcrush.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.gymcrush.R;
import com.example.gymcrush.ui.staff.StaffFragment;

public class AdminDashboardFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_dashboard, container, false);

        view.findViewById(R.id.cardManageUsers).setOnClickListener(v -> {
            // Navegar a gestión de usuarios (usamos StaffFragment por ahora o uno nuevo)
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new StaffFragment())
                    .addToBackStack(null)
                    .commit();
        });

        view.findViewById(R.id.cardManageClasses).setOnClickListener(v -> {
            Toast.makeText(getContext(), "Módulo de gestión de clases en desarrollo", Toast.LENGTH_SHORT).show();
        });

        view.findViewById(R.id.cardMasterPlans).setOnClickListener(v -> {
             Toast.makeText(getContext(), "Módulo de planes maestros en desarrollo", Toast.LENGTH_SHORT).show();
        });

        view.findViewById(R.id.cardSystemConfig).setOnClickListener(v -> {
            Toast.makeText(getContext(), "Configuración del sistema", Toast.LENGTH_SHORT).show();
        });

        return view;
    }
}
