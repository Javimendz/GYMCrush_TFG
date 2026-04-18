package com.example.gymcrush.ui.actividades;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.gymcrush.R;
import com.example.gymcrush.data.remote.dto.ActividadResponseDto;
import java.util.List;

public class ActividadesAdapter extends RecyclerView.Adapter<ActividadesAdapter.ActividadViewHolder> {

    private List<ActividadResponseDto> actividades;
    private final OnActividadClickListener listener;
    private boolean adminMode = false;

    public interface OnActividadClickListener {
        void onActividadClick(ActividadResponseDto actividad);
    }

    public ActividadesAdapter(List<ActividadResponseDto> actividades, OnActividadClickListener listener) {
        this.actividades = actividades;
        this.listener = listener;
    }

    public void setActividades(List<ActividadResponseDto> actividades) {
        this.actividades = actividades;
        notifyDataSetChanged();
    }

    public void setAdminMode(boolean adminMode) {
        this.adminMode = adminMode;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ActividadViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_actividad, parent, false);
        return new ActividadViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ActividadViewHolder holder, int position) {
        ActividadResponseDto actividad = actividades.get(position);
        holder.tvNombre.setText(actividad.getNombre());
        holder.tvSala.setText(actividad.getSala());
        holder.tvDuracion.setText(actividad.getDuracion() + " min");
        holder.tvPrecio.setText(actividad.getPrecio() + "€");
        holder.tvDescripcion.setText(actividad.getDescripcion());
        
        if (actividad.getCategoria() != null) {
            holder.tvCategoria.setText(actividad.getCategoria().getNombre().toUpperCase());
            holder.tvCategoria.setVisibility(View.VISIBLE);
        } else {
            holder.tvCategoria.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> listener.onActividadClick(actividad));
    }

    @Override
    public int getItemCount() {
        return actividades != null ? actividades.size() : 0;
    }

    public static class ActividadViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvSala, tvDuracion, tvPrecio, tvDescripcion, tvCategoria;

        public ActividadViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombreActividad);
            tvSala = itemView.findViewById(R.id.tvSala);
            tvCategoria = itemView.findViewById(R.id.tvCategoria);
            tvDuracion = itemView.findViewById(R.id.tvDuracion);
            tvPrecio = itemView.findViewById(R.id.tvPrecio);
            tvDescripcion = itemView.findViewById(R.id.tvDescripcion);
        }
    }
}
