package com.example.gymcrush.ui.notificaciones;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.gymcrush.R;
import java.util.ArrayList;
import java.util.List;

public class NotificacionesFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notificaciones, container, false);

        // Configurar botón Volver
        View btnVolver = view.findViewById(R.id.btnVolverNotificaciones);
        btnVolver.setOnClickListener(v -> {
            if (getParentFragmentManager() != null) {
                getParentFragmentManager().popBackStack();
            }
        });

        RecyclerView rv = view.findViewById(R.id.rvNotificaciones);
        View layoutEmpty = view.findViewById(R.id.layoutEmpty);

        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        
        // Datos del Gimnasio (Simulados)
        // En una app real, esto vendría de una base de datos o API del gimnasio
        List<Notificacion> lista = new ArrayList<>();
        lista.add(new Notificacion("¡Nueva promo en batidos!", "Solo esta semana, 2x1 en todos los batidos de proteínas post-entreno.", "10:30 AM"));
        lista.add(new Notificacion("Mantenimiento de Máquinas", "La zona de peso libre estará cerrada mañana de 8:00 a 10:00 por mantenimiento.", "Ayer"));
        lista.add(new Notificacion("Evento Especial: Masterclass", "Inscríbete en la recepción para la Masterclass de Zumba del próximo sábado.", "Lunes"));

        if (lista.isEmpty()) {
            layoutEmpty.setVisibility(View.VISIBLE);
            rv.setVisibility(View.GONE);
        } else {
            layoutEmpty.setVisibility(View.GONE);
            rv.setVisibility(View.VISIBLE);
            rv.setAdapter(new NotificacionesAdapter(lista));
        }

        return view;
    }

    public static class Notificacion {
        String titulo, mensaje, fecha;
        public Notificacion(String titulo, String mensaje, String fecha) {
            this.titulo = titulo;
            this.mensaje = mensaje;
            this.fecha = fecha;
        }
    }

    private static class NotificacionesAdapter extends RecyclerView.Adapter<NotificacionesAdapter.ViewHolder> {
        List<Notificacion> items;
        NotificacionesAdapter(List<Notificacion> items) { this.items = items; }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notificacion, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Notificacion n = items.get(position);
            holder.tvTitulo.setText(n.titulo);
            holder.tvMensaje.setText(n.mensaje);
            holder.tvFecha.setText(n.fecha);
        }

        @Override
        public int getItemCount() { return items.size(); }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvTitulo, tvMensaje, tvFecha;
            ViewHolder(View v) {
                super(v);
                tvTitulo = v.findViewById(R.id.tvTituloNotificacion);
                tvMensaje = v.findViewById(R.id.tvMensajeNotificacion);
                tvFecha = v.findViewById(R.id.tvFechaNotificacion);
            }
        }
    }
}
