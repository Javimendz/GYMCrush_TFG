package com.example.gymcrush.ui.rutinas;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.gymcrush.R;
import com.example.gymcrush.data.remote.dto.RutinaResponseDto;
import java.util.List;

public class RutinaApiAdapter extends RecyclerView.Adapter<RutinaApiAdapter.ViewHolder> {

    private List<RutinaResponseDto> listaRutinas;
    private OnRutinaClickListener listener;

    public interface OnRutinaClickListener {
        void onCompletar(RutinaResponseDto rutina);
        void onEliminar(RutinaResponseDto rutina);
        void onClick(RutinaResponseDto rutina);
    }

    public RutinaApiAdapter(List<RutinaResponseDto> listaRutinas, OnRutinaClickListener listener) {
        this.listaRutinas = listaRutinas;
        this.listener = listener;
    }

    public void updateList(List<RutinaResponseDto> newList) {
        this.listaRutinas.clear();
        if (newList != null) this.listaRutinas.addAll(newList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rutina_item_lista, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RutinaResponseDto rutina = listaRutinas.get(position);

        // 1. Evitar el texto "null"
        holder.tvNombre.setText(rutina.getNombreEntrenamiento() != null ? rutina.getNombreEntrenamiento() : "Ejercicio");

        String series = rutina.getSeries() != null ? rutina.getSeries() : "0";
        String reps = rutina.getRepeticiones() != null ? rutina.getRepeticiones() : "0";
        holder.tvDetalles.setText(series + " sets x " + reps + " reps");

        // 2. Cabecera del Plan
        if (rutina.getNombrePlan() != null && !rutina.getNombrePlan().isEmpty()) {
            holder.layoutHeaderPlan.setVisibility(View.VISIBLE);
            holder.tvNombrePlan.setText(rutina.getNombrePlan().toUpperCase());
        } else {
            holder.layoutHeaderPlan.setVisibility(View.GONE);
        }

        // 3. Estado visual
        if (Boolean.TRUE.equals(rutina.getCompletado())) {
            holder.tvEstado.setText("COMPLETADO");
            holder.tvEstado.setAlpha(0.5f);
            holder.imgCheck.setVisibility(View.VISIBLE);
        } else {
            holder.tvEstado.setText("EMPEZAR");
            holder.tvEstado.setAlpha(1.0f);
            holder.imgCheck.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> listener.onClick(rutina));
        holder.btnOpciones.setOnClickListener(v -> listener.onEliminar(rutina));
    }

    @Override
    public int getItemCount() {
        return listaRutinas.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvDetalles, tvEstado, tvNombrePlan;
        ImageView imgCheck, btnOpciones, imgRutina;
        LinearLayout layoutHeaderPlan;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombreRutina);
            tvDetalles = itemView.findViewById(R.id.tvDetallesRutina);
            tvEstado = itemView.findViewById(R.id.tvEstadoRutina);
            tvNombrePlan = itemView.findViewById(R.id.tvNombrePlanPertenece);
            imgCheck = itemView.findViewById(R.id.imgStatusCheck);
            btnOpciones = itemView.findViewById(R.id.btnMasOpciones);
            imgRutina = itemView.findViewById(R.id.imgRutina);
            layoutHeaderPlan = itemView.findViewById(R.id.layoutHeaderPlan);
        }
    }
}