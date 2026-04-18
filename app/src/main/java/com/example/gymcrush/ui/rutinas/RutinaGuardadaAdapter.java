package com.example.gymcrush.ui.rutinas;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.gymcrush.R;
import java.util.List;

public class RutinaGuardadaAdapter extends RecyclerView.Adapter<RutinaGuardadaAdapter.ViewHolder> {

    private List<RepositorioRutinas.RutinaModel> listaRutinas;

    public RutinaGuardadaAdapter(List<RepositorioRutinas.RutinaModel> listaRutinas) {
        this.listaRutinas = listaRutinas;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rutina_item_lista, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RepositorioRutinas.RutinaModel rutina = listaRutinas.get(position);
        holder.tvNombre.setText(rutina.nombre);
        holder.tvDetalles.setText(rutina.duracion + " | Dificultad: " + rutina.dificultad);
        holder.imgRutina.setImageResource(rutina.imagenResId);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), DetalleRutinaActivity.class);
            intent.putExtra("NOMBRE_RUTINA", rutina.nombre);
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return listaRutinas.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvDetalles;
        ImageView imgRutina;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombreRutina);
            tvDetalles = itemView.findViewById(R.id.tvDetallesRutina);
            imgRutina = itemView.findViewById(R.id.imgRutina);
        }
    }
}