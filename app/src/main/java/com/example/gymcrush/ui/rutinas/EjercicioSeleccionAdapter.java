package com.example.gymcrush.ui.rutinas;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gymcrush.ui.ejercicios.Ejercicio;
import com.example.gymcrush.R;
import java.util.List;

public class EjercicioSeleccionAdapter extends RecyclerView.Adapter<EjercicioSeleccionAdapter.ViewHolder> {

    private List<Ejercicio> listaEjercicios;

    public EjercicioSeleccionAdapter(List<Ejercicio> listaEjercicios) {
        this.listaEjercicios = listaEjercicios;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ejercicio_item_seleccion, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Ejercicio ejercicio = listaEjercicios.get(position);
        holder.tvNombre.setText(ejercicio.getNombre());

        // Estado del CheckBox
        holder.cbSeleccionado.setOnCheckedChangeListener(null);
        holder.cbSeleccionado.setChecked(ejercicio.isSeleccionado());
        
        // Mostrar/Ocultar campos de series/reps
        holder.layoutDetalles.setVisibility(ejercicio.isSeleccionado() ? View.VISIBLE : View.GONE);

        holder.cbSeleccionado.setOnCheckedChangeListener((buttonView, isChecked) -> {
            ejercicio.setSeleccionado(isChecked);
            holder.layoutDetalles.setVisibility(isChecked ? View.VISIBLE : View.GONE);
        });

        // Guardar Series
        holder.etSeries.setText(ejercicio.getSeries());
        holder.etSeries.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                ejercicio.setSeries(s.toString());
            }
        });

        // Guardar Repeticiones
        holder.etRepeticiones.setText(ejercicio.getRepeticiones());
        holder.etRepeticiones.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                ejercicio.setRepeticiones(s.toString());
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaEjercicios.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre;
        CheckBox cbSeleccionado;
        LinearLayout layoutDetalles;
        EditText etSeries, etRepeticiones;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombreEjercicio);
            cbSeleccionado = itemView.findViewById(R.id.cbSeleccionado);
            layoutDetalles = itemView.findViewById(R.id.layoutDetallesSeleccion);
            etSeries = itemView.findViewById(R.id.etSeries);
            etRepeticiones = itemView.findViewById(R.id.etRepeticiones);
        }
    }
}