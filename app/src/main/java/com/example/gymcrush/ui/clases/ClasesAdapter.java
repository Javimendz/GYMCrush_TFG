package com.example.gymcrush.ui.clases;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.gymcrush.R;
import com.example.gymcrush.data.model.Clase;
import com.google.android.material.button.MaterialButton;
import java.util.List;

public class ClasesAdapter extends RecyclerView.Adapter<ClasesAdapter.ClaseViewHolder> {

    private List<Clase> clases;
    private OnClaseClickListener listener;
    private boolean adminMode = false;

    public interface OnClaseClickListener {
        void onApuntarseClick(Clase clase);
        default void onEliminarClick(Clase clase) {}
    }

    public ClasesAdapter(List<Clase> clases, OnClaseClickListener listener) {
        this.clases = clases;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ClaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_clase, parent, false);
        return new ClaseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClaseViewHolder holder, int position) {
        Clase clase = clases.get(position);
        holder.tvHora.setText(clase.getHora());
        holder.tvNombre.setText(clase.getNombre());
        holder.tvEntrenador.setText(clase.getSala());
        holder.tvCupos.setText(clase.getCuposDisponibles() + " plazas libres");

        holder.btnApuntarse.setOnClickListener(v -> {
            if (listener != null) {
                listener.onApuntarseClick(clase);
            }
        });

        if (holder.btnEliminar != null) {
            holder.btnEliminar.setVisibility(adminMode ? View.VISIBLE : View.GONE);
            holder.btnEliminar.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEliminarClick(clase);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return clases.size();
    }

    public void setClases(List<Clase> nuevasClases) {
        this.clases = nuevasClases;
        notifyDataSetChanged();
    }

    public void setAdminMode(boolean adminMode) {
        this.adminMode = adminMode;
        notifyDataSetChanged();
    }

    static class ClaseViewHolder extends RecyclerView.ViewHolder {
        TextView tvHora, tvNombre, tvEntrenador, tvCupos;
        MaterialButton btnApuntarse;
        View btnEliminar;

        public ClaseViewHolder(@NonNull View itemView) {
            super(itemView);
            tvHora = itemView.findViewById(R.id.tvHoraClase);
            tvNombre = itemView.findViewById(R.id.tvNombreClase);
            tvEntrenador = itemView.findViewById(R.id.tvEntrenador); // Ahora usado para Sala
            tvCupos = itemView.findViewById(R.id.tvCupos);
            btnApuntarse = itemView.findViewById(R.id.btnApuntarse);
            btnEliminar = itemView.findViewById(R.id.btnEliminarClase);
        }
    }
}