package com.example.gymcrush.ui.rutinas;

import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.gymcrush.R;
import com.example.gymcrush.ui.ejercicios.Ejercicio;
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

        // Lógica de progreso y estado
        if (rutina.completada) {
            holder.tvEstado.setText("Rutina Completada ✓");
            holder.tvEstado.setTextColor(Color.parseColor("#4CAF50")); // Verde
        } else {
            // Calcular progreso parcial
            List<Ejercicio> ejercicios = RepositorioRutinas.getEjerciciosDeRutina(rutina.nombre);
            if (ejercicios != null) {
                int total = ejercicios.size();
                int hechos = 0;
                for (Ejercicio e : ejercicios) {
                    if (e.isSeleccionado()) hechos++;
                }
                
                if (hechos > 0) {
                    holder.tvEstado.setText("Progreso: " + hechos + "/" + total + " completados");
                    holder.tvEstado.setTextColor(Color.parseColor("#FF9800")); // Naranja
                } else {
                    holder.tvEstado.setText("Empezar Entreno");
                    holder.tvEstado.setTextColor(Color.parseColor("#E91E63")); // Rosa original
                }
            } else {
                holder.tvEstado.setText("Empezar Entreno");
                holder.tvEstado.setTextColor(Color.parseColor("#E91E63"));
            }
        }

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
        TextView tvNombre, tvDetalles, tvEstado;
        ImageView imgRutina;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombreRutina);
            tvDetalles = itemView.findViewById(R.id.tvDetallesRutina);
            tvEstado = itemView.findViewById(R.id.tvEstadoRutina);
            imgRutina = itemView.findViewById(R.id.imgRutina);
        }
    }
}