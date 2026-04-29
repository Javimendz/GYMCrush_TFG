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
import java.util.ArrayList;
import java.util.List;

public class RutinaApiAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    private List<Object> items = new ArrayList<>();
    private OnRutinaClickListener listener;

    public RutinaApiAdapter(List<RutinaResponseDto> rutinas, OnRutinaClickListener listener) {
        setRutinas(rutinas);
        this.listener = listener;
    }

    private void setRutinas(List<RutinaResponseDto> rutinas) {
        this.items.clear();
        String currentPlan = null;
        for (RutinaResponseDto r : rutinas) {
            String planName = r.getPlanNombre() != null ? r.getPlanNombre() : "RUTINAS LIBRES";
            if (currentPlan == null || !currentPlan.equals(planName)) {
                currentPlan = planName;
                items.add(currentPlan);
            }
            items.add(r);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position) instanceof String ? TYPE_HEADER : TYPE_ITEM;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_agenda_header, parent, false);
            return new HeaderViewHolder(view);
        }
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rutina_item_lista, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderViewHolder) {
            ((HeaderViewHolder) holder).tvTitle.setText((String) items.get(position));
        } else {
            RutinaResponseDto rutina = (RutinaResponseDto) items.get(position);
            ItemViewHolder itemHolder = (ItemViewHolder) holder;
            itemHolder.tvNombre.setText(rutina.getNombreEntrenamiento());
            itemHolder.tvDetalles.setText(rutina.getDuracion() + " min | Intensidad: " + rutina.getIntensidad());
            
            // Cargar imagen con Glide (Soporte para miniaturas de video y YouTube)
            String urlToLoad = rutina.getUrlVideo();
            if (urlToLoad != null && !urlToLoad.isEmpty()) {
                com.bumptech.glide.request.RequestOptions options = new com.bumptech.glide.request.RequestOptions()
                        .frame(1000000) // 1 segundo
                        .centerCrop();

                // Si es YouTube, extraemos el thumbnail directamente
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

                Glide.with(itemHolder.itemView.getContext())
                        .asBitmap()
                        .load(urlToLoad)
                        .apply(options)
                        .placeholder(R.drawable.ic_launcher_foreground)
                        .error(R.drawable.ic_launcher_foreground)
                        .into(itemHolder.imgRutina);
            } else {
                itemHolder.imgRutina.setImageResource(R.drawable.ic_launcher_foreground);
            }

            if (rutina.getCompletado()) {
                itemHolder.tvEstado.setText("COMPLETADA ✓");
                itemHolder.tvEstado.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFF4CAF50));
                itemHolder.tvEstado.setTextColor(0xFFFFFFFF);
                itemHolder.itemView.setAlpha(0.6f);
                itemHolder.tvNombre.setPaintFlags(itemHolder.tvNombre.getPaintFlags() | android.graphics.Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                itemHolder.tvEstado.setText("EMPEZAR ENTRENAMIENTO");
                itemHolder.tvEstado.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFFCD0277));
                itemHolder.tvEstado.setTextColor(0xFFFFFFFF);
                itemHolder.itemView.setAlpha(1.0f);
                itemHolder.tvNombre.setPaintFlags(itemHolder.tvNombre.getPaintFlags() & (~android.graphics.Paint.STRIKE_THRU_TEXT_FLAG));
            }

            itemHolder.itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onClick(rutina);
                }
            });

            itemHolder.btnMasOpciones.setOnClickListener(v -> {
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
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void updateList(List<RutinaResponseDto> newList) {
        setRutinas(newList);
        notifyDataSetChanged();
    }

    public interface OnRutinaClickListener {
        void onCompletar(RutinaResponseDto rutina);
        void onEliminar(RutinaResponseDto rutina);
        void onClick(RutinaResponseDto rutina);
    }

    public static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvHeaderTitle);
        }
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvDetalles, tvEstado;
        ImageView imgRutina, btnMasOpciones;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombreRutina);
            tvDetalles = itemView.findViewById(R.id.tvDetallesRutina);
            tvEstado = itemView.findViewById(R.id.tvEstadoRutina);
            imgRutina = itemView.findViewById(R.id.imgRutina);
            btnMasOpciones = itemView.findViewById(R.id.btnMasOpciones);
        }
    }
}
