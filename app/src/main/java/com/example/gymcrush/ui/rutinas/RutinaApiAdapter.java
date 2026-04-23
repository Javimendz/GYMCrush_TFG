package com.example.gymcrush.ui.rutinas;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.gymcrush.R;
import com.example.gymcrush.data.remote.dto.RutinaResponseDto;
import com.google.android.material.button.MaterialButton;
import com.bumptech.glide.Glide;
import java.util.List;

public class RutinaApiAdapter extends RecyclerView.Adapter<RutinaApiAdapter.ViewHolder> {

    private List<RutinaResponseDto> rutinas;
    private OnRutinaClickListener listener;

    public RutinaApiAdapter(List<RutinaResponseDto> rutinas, OnRutinaClickListener listener) {
        this.rutinas = rutinas;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rutina_item_lista, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RutinaResponseDto rutina = rutinas.get(position);
        holder.tvNombre.setText(rutina.getNombreEntrenamiento());
        holder.tvDetalles.setText(rutina.getDuracion() + " min | Intensidad: " + rutina.getIntensidad());
        
        // Cargar imagen con Glide
        if (rutina.getUrlGif() != null && !rutina.getUrlGif().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(rutina.getUrlGif())
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .into(holder.imgRutina);
        } else {
            holder.imgRutina.setImageResource(R.drawable.ic_launcher_foreground);
        }

        if (rutina.getCompletado()) {
            holder.tvEstado.setText("COMPLETADA ✓");
            holder.tvEstado.setTextColor(0xFF4CAF50); // Verde
            holder.itemView.setAlpha(0.6f);
            holder.tvNombre.setPaintFlags(holder.tvNombre.getPaintFlags() | android.graphics.Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            holder.tvEstado.setText("PENDIENTE");
            holder.tvEstado.setTextColor(0xFFE91E63); // Rosa/Fucsia
            holder.itemView.setAlpha(1.0f);
            holder.tvNombre.setPaintFlags(holder.tvNombre.getPaintFlags() & (~android.graphics.Paint.STRIKE_THRU_TEXT_FLAG));
        }

        holder.itemView.setOnClickListener(v -> {
            if (!rutina.getCompletado() && listener != null) {
                listener.onCompletar(rutina);
            }
        });

        holder.btnMasOpciones.setOnClickListener(v -> {
            android.widget.PopupMenu popup = new android.widget.PopupMenu(v.getContext(), v);
            if (rutina.getCompletado()) {
                popup.getMenu().add(0, 1, 0, "Desmarcar como completada");
            } else {
                popup.getMenu().add(0, 2, 0, "Marcar como completada");
            }
            popup.getMenu().add(0, 3, 0, "Eliminar de la agenda");
            
            popup.setOnMenuItemClickListener(item -> {
                if (listener != null) {
                    if (item.getItemId() == 1 || item.getItemId() == 2) {
                        listener.onCompletar(rutina);
                    } else if (item.getItemId() == 3) {
                        listener.onEliminar(rutina);
                    }
                }
                return true;
            });
            popup.show();
        });
    }

    @Override
    public int getItemCount() {
        return rutinas.size();
    }

    public void updateList(List<RutinaResponseDto> newList) {
        this.rutinas = newList;
        notifyDataSetChanged();
    }

    public interface OnRutinaClickListener {
        void onCompletar(RutinaResponseDto rutina);
        void onEliminar(RutinaResponseDto rutina);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvDetalles, tvEstado;
        ImageView imgRutina, btnMasOpciones;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombreRutina);
            tvDetalles = itemView.findViewById(R.id.tvDetallesRutina);
            tvEstado = itemView.findViewById(R.id.tvEstadoRutina);
            imgRutina = itemView.findViewById(R.id.imgRutina);
            btnMasOpciones = itemView.findViewById(R.id.btnMasOpciones);
        }
    }
}