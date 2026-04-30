package com.example.gymcrush.ui.rutinas;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
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

        // 1. Mostrar nombre
        holder.tvNombre.setText(rutina.getNombreEntrenamiento() != null ? rutina.getNombreEntrenamiento().toUpperCase() : "ENTRENAMIENTO");
        holder.tvNombre.setVisibility(View.VISIBLE);

        // 2. Mostrar detalles (ej: "3 sets x 12 reps")
        String series = rutina.getSeries() != null ? rutina.getSeries() : "0";
        String reps = rutina.getRepeticiones() != null ? rutina.getRepeticiones() : "0";
        holder.tvDetalles.setText(series + " Series x " + reps + " Reps");

        // 3. Control de etiquetas
        if (rutina.getDuracion() != null && rutina.getDuracion() > 0) {
            holder.layoutDuracionTag.setVisibility(View.VISIBLE);
            holder.tvDuracionTag.setText(rutina.getDuracion() + " min");
        } else {
            holder.layoutDuracionTag.setVisibility(View.GONE);
        }

        if (rutina.getIntensidad() != null && !rutina.getIntensidad().isEmpty() && !rutina.getIntensidad().equalsIgnoreCase("n/a")) {
            holder.layoutIntensidadTag.setVisibility(View.VISIBLE);
            holder.tvIntensidadTag.setText("INTENSIDAD: " + rutina.getIntensidad().toUpperCase());
        } else {
            holder.layoutIntensidadTag.setVisibility(View.GONE);
        }

        // 4. Mostrar estado de completado
        if (rutina.getCompletado() != null && rutina.getCompletado()) {
            holder.imgCheck.setVisibility(View.VISIBLE);
            holder.tvEstado.setText("COMPLETADO");
            holder.tvEstado.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.holo_green_light));
        } else {
            holder.imgCheck.setVisibility(View.GONE);
            holder.tvEstado.setText("PENDIENTE");
            holder.tvEstado.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.white));
        }

        holder.itemView.setOnClickListener(v -> listener.onClick(rutina));
        holder.btnOpciones.setOnClickListener(v -> listener.onEliminar(rutina));

        // 5. Cargar Imagen con Glide (Prioridad a urlImagen, luego urlVideo como fallback)
        String imageUrl = (rutina.getUrlImagen() != null && !rutina.getUrlImagen().isEmpty())
                ? rutina.getUrlImagen()
                : rutina.getUrlVideo();

        if (imageUrl != null && !imageUrl.isEmpty()) {
            String urlToLoad = imageUrl;
            if (urlToLoad.contains("youtube.com") || urlToLoad.contains("youtu.be")) {
                String videoId = "";
                if (urlToLoad.contains("v=")) videoId = urlToLoad.split("v=")[1].split("&")[0];
                else if (urlToLoad.contains("youtu.be/")) videoId = urlToLoad.split("youtu.be/")[1].split("\\?")[0];
                
                if (!videoId.isEmpty()) urlToLoad = "https://img.youtube.com/vi/" + videoId + "/0.jpg";
            }

            Glide.with(holder.itemView.getContext())
                    .load(urlToLoad)
                    .centerCrop()
                    .placeholder(R.drawable.cardio)
                    .error(R.drawable.cardio)
                    .into(holder.imgRutina);
        } else {
            holder.imgRutina.setImageResource(R.drawable.cardio);
        }
    }

    @Override
    public int getItemCount() {
        return listaRutinas.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvDetalles, tvEstado, tvNombrePlan;
        TextView tvDuracionTag, tvIntensidadTag;
        ImageView imgCheck, btnOpciones, imgRutina;
        LinearLayout layoutHeaderPlan, layoutDuracionTag, layoutIntensidadTag;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombreRutina);
            tvDetalles = itemView.findViewById(R.id.tvDetallesRutina);
            tvEstado = itemView.findViewById(R.id.tvEstadoRutina);
            tvNombrePlan = itemView.findViewById(R.id.tvNombrePlanPertenece);
            tvDuracionTag = itemView.findViewById(R.id.tvDuracionTagItem);
            tvIntensidadTag = itemView.findViewById(R.id.tvIntensidadTagItem);
            imgCheck = itemView.findViewById(R.id.imgStatusCheck);
            btnOpciones = itemView.findViewById(R.id.btnMasOpciones);
            imgRutina = itemView.findViewById(R.id.imgRutina);
            layoutHeaderPlan = itemView.findViewById(R.id.layoutHeaderPlan);
            layoutDuracionTag = itemView.findViewById(R.id.layoutDuracionTag);
            layoutIntensidadTag = itemView.findViewById(R.id.layoutIntensidadTag);
        }
    }
}