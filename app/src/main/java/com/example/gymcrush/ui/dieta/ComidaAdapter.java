package com.example.gymcrush.ui.dieta;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.gymcrush.R;
import com.example.gymcrush.data.remote.dto.ComidaDiariaResponseDto;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ComidaAdapter extends RecyclerView.Adapter<ComidaAdapter.ComidaViewHolder> {

    public interface OnComidaClickListener {
        void onComidaClick(ComidaDiariaResponseDto comida);
    }

    public interface OnComidaLongClickListener {
        void onComidaLongClick(ComidaDiariaResponseDto comida);
    }

    public interface OnHechaClickListener {
        void onHechaClick(ComidaDiariaResponseDto comida, boolean isChecked);
    }

    public interface OnMenuClickListener {
        void onMenuClick(View view, ComidaDiariaResponseDto comida);
    }

    private List<ComidaDiariaResponseDto> comidas = new ArrayList<>();
    private OnComidaClickListener clickListener;
    private OnComidaLongClickListener longClickListener;
    private OnHechaClickListener hechaClickListener;
    private OnMenuClickListener menuClickListener;

    public void setComidas(List<ComidaDiariaResponseDto> comidas) {
        this.comidas = comidas;
        notifyDataSetChanged();
    }

    public void setOnComidaClickListener(OnComidaClickListener listener) {
        this.clickListener = listener;
    }

    public void setOnComidaLongClickListener(OnComidaLongClickListener listener) {
        this.longClickListener = listener;
    }

    public void setOnHechaClickListener(OnHechaClickListener listener) {
        this.hechaClickListener = listener;
    }

    public void setOnMenuClickListener(OnMenuClickListener listener) {
        this.menuClickListener = listener;
    }

    @NonNull
    @Override
    public ComidaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comida_dieta, parent, false);
        return new ComidaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ComidaViewHolder holder, int position) {
        ComidaDiariaResponseDto comida = comidas.get(position);
        holder.bind(comida, clickListener, longClickListener, hechaClickListener, menuClickListener);
    }

    @Override
    public int getItemCount() {
        return comidas.size();
    }

    static class ComidaViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitulo, tvDesc, tvHora, tvMacros;
        ImageView ivIcono, ivMenu;
        CheckBox cbHecha;

        public ComidaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitulo = itemView.findViewById(R.id.tvTituloComida);
            tvDesc = itemView.findViewById(R.id.tvDescripcionComida);
            tvHora = itemView.findViewById(R.id.tvHoraComida);
            tvMacros = itemView.findViewById(R.id.tvMacrosComida);
            ivIcono = itemView.findViewById(R.id.ivIconoComida);
            ivMenu = itemView.findViewById(R.id.ivMenuComida);
            cbHecha = itemView.findViewById(R.id.cbComidaHecha);
        }

        public void bind(ComidaDiariaResponseDto comida, OnComidaClickListener clickListener, OnComidaLongClickListener longClickListener, OnHechaClickListener hechaClickListener, OnMenuClickListener menuClickListener) {
            tvTitulo.setText(comida.getNombreAlimento() != null ? comida.getNombreAlimento() : comida.getMomento());
            tvDesc.setText(comida.getMomento()); // Invertimos para que el plato sea el título
            
            if (comida.getCalorias() != null) {
                String macros = String.format(Locale.getDefault(), "%.0f kcal | P: %.0fg | C: %.0fg | G: %.0fg",
                        comida.getCalorias(),
                        comida.getProteina() != null ? comida.getProteina() : 0,
                        comida.getCarbohidratos() != null ? comida.getCarbohidratos() : 0,
                        comida.getGrasas() != null ? comida.getGrasas() : 0);
                tvMacros.setText(macros);
                tvMacros.setVisibility(View.VISIBLE);
            } else {
                tvMacros.setVisibility(View.GONE);
            }

            itemView.setOnClickListener(v -> {
                if (clickListener != null) {
                    clickListener.onComidaClick(comida);
                }
            });

            if (ivMenu != null) {
                ivMenu.setOnClickListener(v -> {
                    if (menuClickListener != null) {
                        menuClickListener.onMenuClick(v, comida);
                    }
                });
            }

            // Lógica del Checkbox para engagement
            cbHecha.setOnCheckedChangeListener(null); // Reset
            
            // Si el ID es nulo, es una sugerencia (no persistida aún)
            // Si tiene ID, es una comida ya registrada en el consumo diario
            boolean esConsumoReal = (comida.getId() != null);
            cbHecha.setChecked(esConsumoReal);
            itemView.setAlpha(esConsumoReal ? 0.6f : 1.0f);
            
            // Ahora permitimos marcar y desmarcar
            // cbHecha.setEnabled(!esConsumoReal); 

            cbHecha.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    itemView.setAlpha(0.6f);
                } else {
                    itemView.setAlpha(1.0f);
                }
                
                if (hechaClickListener != null) {
                    hechaClickListener.onHechaClick(comida, isChecked);
                }
            });

            if (comida.getImagenUrl() != null && !comida.getImagenUrl().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(comida.getImagenUrl())
                        .centerCrop()
                        .placeholder(R.drawable.ic_compras)
                        .error(R.drawable.ic_compras)
                        .into(ivIcono);
            } else {
                ivIcono.setImageResource(R.drawable.ic_compras);
            }
            
            // ... resto del código de la hora
            String hora = "--:--";
            if (comida.getMomento() != null) {
                switch (comida.getMomento().toUpperCase()) {
                    case "DESAYUNO": hora = "08:30"; break;
                    case "ALMUERZO": case "MEDIA MAÑANA": hora = "11:30"; break;
                    case "COMIDA": hora = "14:30"; break;
                    case "MERIENDA": hora = "18:00"; break;
                    case "CENA": hora = "21:30"; break;
                }
            }
            tvHora.setText(hora);
        }
    }
}
