package com.example.gymcrush.ui.rutinas;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.gymcrush.R;
import com.example.gymcrush.data.remote.dto.DetallePlanResponseDto;
import java.util.List;

public class DetallePlanAdapter extends RecyclerView.Adapter<DetallePlanAdapter.ViewHolder> {

    private List<DetallePlanResponseDto> detalles;
    private OnDeleteClickListener deleteListener;

    public interface OnDeleteClickListener {
        void onDelete(DetallePlanResponseDto detalle);
    }

    public DetallePlanAdapter(List<DetallePlanResponseDto> detalles, OnDeleteClickListener deleteListener) {
        this.detalles = detalles;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_detalle_plan, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DetallePlanResponseDto detalle = detalles.get(position);
        holder.tvNombre.setText(detalle.getNombreEntrenamiento());
        holder.tvDiaOrden.setText("Día " + detalle.getDiaSemana() + " - Orden " + detalle.getOrden());
        holder.btnEliminar.setOnClickListener(v -> deleteListener.onDelete(detalle));
    }

    @Override
    public int getItemCount() {
        return detalles.size();
    }

    public void updateList(List<DetallePlanResponseDto> newList) {
        this.detalles = newList;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvDiaOrden;
        ImageButton btnEliminar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombreEjercicioDetalle);
            tvDiaOrden = itemView.findViewById(R.id.tvDiaOrdenDetalle);
            btnEliminar = itemView.findViewById(R.id.btnEliminarEjercicioDetalle);
        }
    }
}
