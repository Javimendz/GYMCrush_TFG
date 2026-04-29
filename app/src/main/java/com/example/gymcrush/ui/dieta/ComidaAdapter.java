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

    public interface OnComidaClickListener { void onComidaClick(ComidaDiariaResponseDto comida); }
    public interface OnComidaLongClickListener { void onComidaLongClick(ComidaDiariaResponseDto comida); }
    public interface OnHechaClickListener { void onHechaClick(ComidaDiariaResponseDto comida, boolean isChecked); }
    public interface OnMenuClickListener { void onMenuClick(View view, ComidaDiariaResponseDto comida); }

    private List<ComidaDiariaResponseDto> comidas = new ArrayList<>();
    private OnComidaClickListener clickListener;
    private OnComidaLongClickListener longClickListener;
    private OnHechaClickListener hechaClickListener;
    private OnMenuClickListener menuClickListener;

    public void setComidas(List<ComidaDiariaResponseDto> comidas) {
        this.comidas = comidas;
        notifyDataSetChanged();
    }

    public List<ComidaDiariaResponseDto> getComidas() { return comidas; }

    public void setOnComidaClickListener(OnComidaClickListener listener) { this.clickListener = listener; }
    public void setOnComidaLongClickListener(OnComidaLongClickListener listener) { this.longClickListener = listener; }
    public void setOnHechaClickListener(OnHechaClickListener listener) { this.hechaClickListener = listener; }
    public void setOnMenuClickListener(OnMenuClickListener listener) { this.menuClickListener = listener; }

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
    public int getItemCount() { return comidas.size(); }

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

        public void bind(ComidaDiariaResponseDto comida, OnComidaClickListener clickListener,
                         OnComidaLongClickListener longClickListener,
                         OnHechaClickListener hechaClickListener, OnMenuClickListener menuClickListener) {

            tvTitulo.setText(comida.getNombreAlimento() != null ? comida.getNombreAlimento() : "Sin nombre");
            tvDesc.setText(comida.getMomento());

            // 1. Lógica de Macros
            if (comida.getCalorias() != null) {
                tvMacros.setText(String.format(Locale.getDefault(), "%.0f kcal | P:%.0fg | C:%.0fg | G:%.0fg",
                        comida.getCalorias(), comida.getProteina(), comida.getCarbohidratos(), comida.getGrasas()));
                tvMacros.setVisibility(View.VISIBLE);
            } else {
                tvMacros.setVisibility(View.GONE);
            }

            // 2. ESTADO VISUAL Y BLOQUEO (Solución a tu problema)
            cbHecha.setOnCheckedChangeListener(null); // Limpiar listener para evitar bugs de reciclaje
            cbHecha.setChecked(comida.isSelected());

            // Si la comida ya tiene ID (está en la DB), bloqueamos el checkbox para que no se pueda desmarcar
            cbHecha.setEnabled(comida.getId() == null);

            // Efecto de oscurecimiento si está seleccionado
            itemView.setAlpha(comida.isSelected() ? 0.5f : 1.0f);

            cbHecha.setOnCheckedChangeListener((buttonView, isChecked) -> {
                comida.setSelected(isChecked);
                itemView.setAlpha(isChecked ? 0.5f : 1.0f);
                if (hechaClickListener != null) hechaClickListener.onHechaClick(comida, isChecked);
            });

            // 3. Imagen con Glide
            Glide.with(itemView.getContext())
                    .load(comida.getImagenUrl())
                    .centerCrop()
                    .placeholder(R.drawable.ic_compras)
                    .into(ivIcono);

            if (ivMenu != null) ivMenu.setOnClickListener(v -> menuClickListener.onMenuClick(v, comida));
            itemView.setOnClickListener(v -> clickListener.onComidaClick(comida));
            itemView.setOnLongClickListener(v -> {
                if (longClickListener != null) {
                    longClickListener.onComidaLongClick(comida);
                    return true;
                }
                return false;
            });

            // 4. Lógica de Horas
            tvHora.setText(asignarHoraSegunMomento(comida.getMomento()));
        }

        private String asignarHoraSegunMomento(String momento) {
            if (momento == null) return "--:--";
            switch (momento.toUpperCase()) {
                case "DESAYUNO": return "08:30";
                case "ALMUERZO": return "11:30";
                case "COMIDA": return "14:30";
                case "MERIENDA": return "18:00";
                case "CENA": return "21:30";
                default: return "12:00";
            }
        }
    }
}