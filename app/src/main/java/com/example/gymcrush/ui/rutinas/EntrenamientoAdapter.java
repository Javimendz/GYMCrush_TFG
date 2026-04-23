package com.example.gymcrush.ui.rutinas;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.gymcrush.R;
import com.example.gymcrush.data.remote.dto.EntrenamientoResponseDto;
import com.bumptech.glide.Glide;
import java.util.List;

public class EntrenamientoAdapter extends RecyclerView.Adapter<EntrenamientoAdapter.ViewHolder> {

    private List<EntrenamientoResponseDto> lista;
    private OnItemClickListener listener;
    private boolean isAdmin = false;

    public interface OnItemClickListener {
        void onStartWorkout(EntrenamientoResponseDto entrenamiento);
        void onManageExercises(EntrenamientoResponseDto entrenamiento);
    }

    public EntrenamientoAdapter(List<EntrenamientoResponseDto> lista) {
        this.lista = lista;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setAdmin(boolean admin) {
        this.isAdmin = admin;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rutina_item_lista, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        EntrenamientoResponseDto item = lista.get(position);
        holder.tvNombre.setText(item.getNombre());
        String categoria = item.getCategoria() != null ? item.getCategoria() : "Sin categoría";
        holder.tvDetalles.setText(categoria + " | " + item.getDuracion() + " min | " + item.getIntensidad());

        // Cargar imagen con Glide
        if (item.getUrlVideo() != null && !item.getUrlVideo().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(item.getUrlVideo())
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .into(holder.imgRutina);
        } else {
            holder.imgRutina.setImageResource(R.drawable.ic_launcher_foreground);
        }

        // El botón de la derecha (tres líneas/menú) servirá para que el admin gestione ejercicios
        holder.btnMasOpciones.setVisibility(isAdmin ? View.VISIBLE : View.GONE);
        holder.btnMasOpciones.setOnClickListener(v -> {
            if (listener != null) listener.onManageExercises(item);
        });

        // El texto "Empezar Entreno" sirve para ver los ejercicios (y empezar para el usuario)
        holder.tvEstado.setOnClickListener(v -> {
            if (listener != null) listener.onStartWorkout(item);
        });
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public void updateLista(List<EntrenamientoResponseDto> nuevaLista) {
        this.lista = nuevaLista;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvDetalles, tvEstado;
        ImageView btnMasOpciones, imgRutina;
        public ViewHolder(@NonNull View v) {
            super(v);
            tvNombre = v.findViewById(R.id.tvNombreRutina);
            tvDetalles = v.findViewById(R.id.tvDetallesRutina);
            tvEstado = v.findViewById(R.id.tvEstadoRutina);
            btnMasOpciones = v.findViewById(R.id.btnMasOpciones);
            imgRutina = v.findViewById(R.id.imgRutina);
        }
    }
}