package com.example.gymcrush.ui.rutinas;

import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.gymcrush.R;
import com.example.gymcrush.ui.ejercicios.Ejercicio;
import java.util.List;

public class RutinaGuardadaAdapter extends RecyclerView.Adapter<RutinaGuardadaAdapter.ViewHolder> {

    private List<RepositorioRutinas.RutinaModel> listaRutinas;
    private boolean forViewPager = false;
    private OnRutinaGuardadaClickListener listener;

    public interface OnRutinaGuardadaClickListener {
        void onDelete(RepositorioRutinas.RutinaModel rutina);
    }

    public RutinaGuardadaAdapter(List<RepositorioRutinas.RutinaModel> listaRutinas) {
        this.listaRutinas = listaRutinas;
    }

    public RutinaGuardadaAdapter(List<RepositorioRutinas.RutinaModel> listaRutinas, OnRutinaGuardadaClickListener listener) {
        this.listaRutinas = listaRutinas;
        this.listener = listener;
    }

    public RutinaGuardadaAdapter(List<RepositorioRutinas.RutinaModel> listaRutinas, boolean forViewPager) {
        this.listaRutinas = listaRutinas;
        this.forViewPager = forViewPager;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rutina_item_lista, parent, false);
        if (forViewPager) {
            view.setLayoutParams(new RecyclerView.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            ));
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RepositorioRutinas.RutinaModel rutina = listaRutinas.get(position);
        holder.tvNombre.setText(rutina.nombre);

        String desc = rutina.descripcion;
        if (desc == null || desc.isEmpty() || desc.equals("Rutina personalizada")) {
            desc = "Rutina personalizada";
        }
        holder.tvDetalles.setText(desc);

        // Tags de Duración e Intensidad
        if (rutina.duracion != null && !rutina.duracion.isEmpty()) {
            holder.layoutDuracionTag.setVisibility(View.VISIBLE);
            holder.tvDuracionTag.setText(rutina.duracion.contains("min") ? rutina.duracion : rutina.duracion + " min");
        } else {
            holder.layoutDuracionTag.setVisibility(View.GONE);
        }

        if (rutina.dificultad != null && !rutina.dificultad.isEmpty()) {
            holder.layoutIntensidadTag.setVisibility(View.VISIBLE);
            holder.tvIntensidadTag.setText(rutina.dificultad.toUpperCase());
        } else {
            holder.layoutIntensidadTag.setVisibility(View.GONE);
        }
        
        // Cargar imagen con Glide si hay URL (Soporte para miniaturas de video)
        if (rutina.urlVideo != null && !rutina.urlVideo.isEmpty()) {
            String urlToLoad = rutina.urlVideo;
            if (urlToLoad.contains("youtube.com") || urlToLoad.contains("youtu.be")) {
                String videoId = "";
                if (urlToLoad.contains("v=")) {
                    videoId = urlToLoad.split("v=")[1].split("&")[0];
                } else if (urlToLoad.contains("youtu.be/")) {
                    videoId = urlToLoad.split("youtu.be/")[1].split("\\?")[0];
                }
                if (!videoId.isEmpty()) {
                    urlToLoad = "https://img.youtube.com/vi/" + videoId + "/0.jpg";
                }
            }

            com.bumptech.glide.Glide.with(holder.itemView.getContext())
                    .load(urlToLoad)
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .error(rutina.imagenResId != 0 ? rutina.imagenResId : R.drawable.ic_launcher_background)
                    .centerCrop()
                    .into(holder.imgRutina);
        } else {
            holder.imgRutina.setImageResource(rutina.imagenResId != 0 ? rutina.imagenResId : R.drawable.ic_launcher_background);
        }

        // Lógica de progreso y estado
        holder.tvEstado.setTextColor(Color.WHITE);
        if (rutina.completada) {
            holder.tvEstado.setText("Rutina Completada ✓");
            holder.tvEstado.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#4CAF50")));
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
                    holder.tvEstado.setText("Progreso: " + hechos + "/" + total);
                    holder.tvEstado.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#FF9800")));
                } else {
                    holder.tvEstado.setText("Empezar Entreno");
                    holder.tvEstado.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#CD0277")));
                }
            } else {
                holder.tvEstado.setText("Empezar Entreno");
                holder.tvEstado.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#CD0277")));
            }
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), DetalleRutinaActivity.class);
            intent.putExtra("NOMBRE_RUTINA", rutina.nombre);
            intent.putExtra("DESCRIPCION", rutina.descripcion);
            intent.putExtra("URL_VIDEO", rutina.urlVideo);
            intent.putExtra("RUTINA_ID", rutina.id);
            intent.putExtra("DURACION", rutina.duracion);
            intent.putExtra("INTENSIDAD", rutina.dificultad);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            v.getContext().startActivity(intent);
        });

        holder.btnMasOpciones.setOnClickListener(v -> {
            android.widget.PopupMenu popup = new android.widget.PopupMenu(v.getContext(), v);
            popup.getMenu().add("Eliminar rutina");
            popup.setOnMenuItemClickListener(item -> {
                if (item.getTitle().equals("Eliminar rutina")) {
                    if (listener != null) listener.onDelete(rutina);
                }
                return true;
            });
            popup.show();
        });
    }

    public void updateList(List<RepositorioRutinas.RutinaModel> newList) {
        this.listaRutinas = newList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return listaRutinas.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvDetalles, tvEstado;
        TextView tvDuracionTag, tvIntensidadTag;
        ImageView imgRutina, btnMasOpciones;
        LinearLayout layoutDuracionTag, layoutIntensidadTag;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombreRutina);
            tvDetalles = itemView.findViewById(R.id.tvDetallesRutina);
            tvEstado = itemView.findViewById(R.id.tvEstadoRutina);
            tvDuracionTag = itemView.findViewById(R.id.tvDuracionTagItem);
            tvIntensidadTag = itemView.findViewById(R.id.tvIntensidadTagItem);
            imgRutina = itemView.findViewById(R.id.imgRutina);
            btnMasOpciones = itemView.findViewById(R.id.btnMasOpciones);
            layoutDuracionTag = itemView.findViewById(R.id.layoutDuracionTag);
            layoutIntensidadTag = itemView.findViewById(R.id.layoutIntensidadTag);
        }
    }
}