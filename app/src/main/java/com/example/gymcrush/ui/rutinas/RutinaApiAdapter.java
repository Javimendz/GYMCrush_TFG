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

    // CORRECCIÓN: Método para actualizar la lista completa de forma segura
    public void updateList(List<RutinaResponseDto> newList) {
        this.listaRutinas.clear();
        if (newList != null) {
            this.listaRutinas.addAll(newList);
        }
        notifyDataSetChanged(); // Notifica que toda la lista ha cambiado
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

        holder.tvNombre.setText(rutina.getNombreEntrenamiento());

        // Formatear detalles: "4 sets x 12 reps"
        String detalles = String.format("%s sets x %s reps",
                rutina.getSeries() != null ? rutina.getSeries() : "0",
                rutina.getRepeticiones() != null ? rutina.getRepeticiones() : "0");
        holder.tvDetalles.setText(detalles);

        // Manejo de estado visual (Completado o no)
        if (rutina.getCompletado()) {
            holder.tvEstado.setText("COMPLETADO");
            holder.tvEstado.setBackgroundResource(R.drawable.bg_estado_completado); // Asegúrate de tener este drawable
            holder.imgCheck.setVisibility(View.VISIBLE);
        } else {
            holder.tvEstado.setText("EMPEZAR");
            holder.tvEstado.setBackgroundResource(R.drawable.bg_estado_badge);
            holder.imgCheck.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> listener.onClick(rutina));
        holder.btnOpciones.setOnClickListener(v -> listener.onEliminar(rutina));
    }

    @Override
    public int getItemCount() {
        // CORRECCIÓN: Siempre debe devolver el tamaño real de la lista
        return listaRutinas != null ? listaRutinas.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvDetalles, tvEstado;
        ImageView imgCheck, btnOpciones, imgRutina;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombreRutina);
            tvDetalles = itemView.findViewById(R.id.tvDetallesRutina);
            tvEstado = itemView.findViewById(R.id.tvEstadoRutina);
            imgCheck = itemView.findViewById(R.id.imgStatusCheck);
            btnOpciones = itemView.findViewById(R.id.btnMasOpciones);
            imgRutina = itemView.findViewById(R.id.imgRutina);
        }
    }
}