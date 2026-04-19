package com.example.gymcrush.ui.dieta;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.gymcrush.R;
import com.example.gymcrush.data.remote.dto.NutricionResponseDto;
import java.util.ArrayList;
import java.util.List;

public class HistorialNutricionAdapter extends RecyclerView.Adapter<HistorialNutricionAdapter.ViewHolder> {

    private List<NutricionResponseDto> historial = new ArrayList<>();
    private final OnPlanClickListener listener;

    public interface OnPlanClickListener {
        void onPlanClick(NutricionResponseDto plan);
    }

    public HistorialNutricionAdapter(OnPlanClickListener listener) {
        this.listener = listener;
    }

    public void setHistorial(List<NutricionResponseDto> historial) {
        this.historial = historial;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_nutricion_historial, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NutricionResponseDto plan = historial.get(position);
        holder.bind(plan, listener);
    }

    @Override
    public int getItemCount() {
        return historial.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvFecha, tvNombre, tvCalorias;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFecha = itemView.findViewById(R.id.tvFechaHistorial);
            tvNombre = itemView.findViewById(R.id.tvNombrePlanHistorial);
            tvCalorias = itemView.findViewById(R.id.tvCaloriasHistorial);
        }

        public void bind(NutricionResponseDto plan, OnPlanClickListener listener) {
            String fecha = plan.getFechaGeneracion() != null ? plan.getFechaGeneracion() : "Fecha desconocida";
            // Si tiene nombre específico lo usamos, si no el tipo
            String titulo = plan.getNombreDieta() != null && !plan.getNombreDieta().isEmpty() 
                    ? plan.getNombreDieta() 
                    : (plan.getTipoDieta() != null ? plan.getTipoDieta() : "Plan Personalizado");
            
            tvFecha.setText(fecha);
            tvNombre.setText(titulo);
            tvCalorias.setText(plan.getCaloriasObjetivo() + " kcal / día");
            itemView.setOnClickListener(v -> listener.onPlanClick(plan));
        }
    }
}
