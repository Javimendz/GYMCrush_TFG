package com.example.gymcrush.ui.dieta;

import android.app.DatePickerDialog;
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
import androidx.fragment.app.FragmentTransaction;
import com.example.gymcrush.R;
import com.google.android.material.slider.Slider;
import java.util.Calendar;

public class DietaFragment extends Fragment {

    private AutoCompleteTextView spinnerEstiloVida, spinnerObjetivo, spinnerHorario, spinnerMacros;
    private TextView tvGastoVal;
    private Slider sliderGasto;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dieta, container, false);

        // Botón volver
        view.findViewById(R.id.btnBackDieta).setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        // Inicializar componentes
        spinnerEstiloVida = view.findViewById(R.id.spinner_estilo_vida);
        spinnerObjetivo = view.findViewById(R.id.spinner_objetivo);
        spinnerHorario = view.findViewById(R.id.spinner_horario);
        spinnerMacros = view.findViewById(R.id.spinner_macros);
        tvGastoVal = view.findViewById(R.id.tv_gasto_diario_val);
        sliderGasto = view.findViewById(R.id.slider_gasto);

        configurarDropdowns();
        configurarSlider();
        configurarDatePicker(view);

        view.findViewById(R.id.btn_generar_dieta).setOnClickListener(v -> {
            String obj = spinnerObjetivo.getText().toString();
            int calorias = (int) sliderGasto.getValue();
            
            if (obj.isEmpty()) {
                Toast.makeText(getContext(), "Por favor, selecciona un objetivo", Toast.LENGTH_SHORT).show();
            } else {
                abrirResultadoDieta(obj, calorias);
            }
        });

        return view;
    }

    private void abrirResultadoDieta(String objetivo, int calorias) {
        ResultadoDietaFragment fragment = ResultadoDietaFragment.newInstance(objetivo, calorias);
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void configurarDropdowns() {
        String[] estilos = {"Sedentario", "Activo", "Muy Activo"};
        String[] objetivos = {"Perder Grasa", "Mantener Peso", "Ganar Músculo"};
        String[] horarios = {"Mañana", "Tarde", "Noche"};
        String[] macros = {"Equilibrada", "Alta en Proteínas", "Baja en Carbohidratos"};

        spinnerEstiloVida.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, estilos));
        spinnerObjetivo.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, objetivos));
        spinnerHorario.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, horarios));
        spinnerMacros.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, macros));
    }

    private void configurarSlider() {
        sliderGasto.addOnChangeListener((slider, value, fromUser) -> {
            tvGastoVal.setText("Gasto diario: " + (int)value + " kcal");
        });
    }

    private void configurarDatePicker(View view) {
        TextView etFecha = view.findViewById(R.id.et_fecha_inicio);
        etFecha.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Aplicando el tema personalizado al DatePickerDialog
            DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), R.style.CustomDatePickerTheme, (view1, year1, monthOfYear, dayOfMonth) -> {
                etFecha.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year1);
            }, year, month, day);
            datePickerDialog.show();
        });
    }
}
