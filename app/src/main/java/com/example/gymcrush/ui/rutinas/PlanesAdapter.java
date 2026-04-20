package com.example.gymcrush.ui.rutinas;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.gymcrush.R;
import com.example.gymcrush.data.remote.dto.PlanResponseDto;
import com.google.android.material.button.MaterialButton;
import java.util.List;

public class PlanesAdapter extends RecyclerView.Adapter<PlanesAdapter.ViewHolder> {

    private List<PlanResponseDto> planes;
    private OnPlanClickListener listener;
    private boolean isAdmin = false;

    public interface OnPlanClickListener {
        void onSuscribir(PlanResponseDto plan);
        void onEditar(PlanResponseDto plan);
        void onEliminar(PlanResponseDto plan);
    }

    public PlanesAdapter(List<PlanResponseDto> planes, OnPlanClickListener listener) {
        this.planes = planes;
        this.listener = listener;
    }

    public void setAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_plan_entrenamiento, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PlanResponseDto plan = planes.get(position);
        holder.tvNombre.setText(plan.getNombre() != null ? plan.getNombre() : "Plan sin nombre");
        
        String objetivo = plan.getObjetivo() != null ? plan.getObjetivo() : "General";
        holder.tvObjetivo.setText("Objetivo: " + objetivo.toUpperCase());
        
        String nivel = plan.getNivel() != null ? plan.getNivel() : "INTERMEDIO";
        holder.tvNivel.setText(nivel.toUpperCase());
        
        holder.btnSuscribir.setOnClickListener(v -> {
            if (listener != null) listener.onSuscribir(plan);
        });

        if (isAdmin) {
            holder.btnEditar.setVisibility(View.VISIBLE);
            holder.btnEliminar.setVisibility(View.VISIBLE);
            holder.btnEditar.setOnClickListener(v -> {
                if (listener != null) listener.onEditar(plan);
            });
            holder.btnEliminar.setOnClickListener(v -> {
                if (listener != null) listener.onEliminar(plan);
            });
        } else {
            holder.btnEditar.setVisibility(View.GONE);
            holder.btnEliminar.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return planes != null ? planes.size() : 0;
    }

    public void updateList(List<PlanResponseDto> newList) {
        this.planes = newList != null ? newList : new java.util.ArrayList<>();
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvObjetivo, tvNivel;
        MaterialButton btnSuscribir, btnEditar, btnEliminar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombrePlan);
            tvObjetivo = itemView.findViewById(R.id.tvObjetivoPlan);
            tvNivel = itemView.findViewById(R.id.tvNivelPlan);
            btnSuscribir = itemView.findViewById(R.id.btnSuscribirPlan);
            btnEditar = itemView.findViewById(R.id.btnEditarPlan);
            btnEliminar = itemView.findViewById(R.id.btnEliminarPlan);
        }
    }
}
