package com.example.gymcrush.ui.ejercicios;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.gymcrush.R;
import com.example.gymcrush.data.remote.dto.TutorialResponseDto;

import java.util.List;

public class EjercicioAdapter extends RecyclerView.Adapter<EjercicioAdapter.ViewHolder> {

    private List<TutorialResponseDto> listaTutoriales;
    private List<Ejercicio> listaEjercicios;
    private boolean mostrarReps = false;

    public EjercicioAdapter(List<TutorialResponseDto> listaTutoriales) {
        this.listaTutoriales = listaTutoriales;
    }

    public EjercicioAdapter(List<Ejercicio> listaEjercicios, boolean mostrarReps) {
        this.listaEjercicios = listaEjercicios;
        this.mostrarReps = mostrarReps;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.ejercicio_item_biblioteca, parent, false);
        return new ViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String nombre, categoria, descripcion, duracion, urlVideo;
        Long id = -1L;
        int imagenRes = R.drawable.logo;

        if (listaTutoriales != null) {
            TutorialResponseDto t = listaTutoriales.get(position);
            nombre = t.getTitulo();
            categoria = t.getNombreCategoria();
            descripcion = t.getDescripcion();
            duracion = "Duración: " + t.getDuracionMin() + " min";
            urlVideo = t.getUrlVideo();
            id = t.getId();
            holder.txtRepsSeries.setVisibility(View.GONE);
        } else {
            Ejercicio e = listaEjercicios.get(position);
            nombre = e.getNombre();
            categoria = e.getCategoria();
            descripcion = e.getDescripcion();
            duracion = e.getDuracion();
            urlVideo = e.getUrlVideo();
            if (e.getImagenResId() != 0) {
                imagenRes = e.getImagenResId();
            }

            if (mostrarReps) {
                holder.txtRepsSeries.setVisibility(View.VISIBLE);
                holder.txtRepsSeries.setText(e.getSeries() + " Series x " + e.getRepeticiones() + " Reps");
            } else {
                holder.txtRepsSeries.setVisibility(View.GONE);
            }
        }
        
        holder.txtNombre.setText(nombre);
        holder.txtCategoria.setText(categoria);
        holder.txtDescripcion.setText(descripcion);
        holder.txtDificultad.setText(duracion);
        
        if (urlVideo != null && (urlVideo.endsWith(".mp4") || urlVideo.contains("gif"))) {
            Glide.with(holder.itemView.getContext())
                    .load(urlVideo)
                    .placeholder(R.drawable.logo)
                    .into(holder.imgFoto);
        } else {
            holder.imgFoto.setImageResource(imagenRes);
        }

        final Long finalId = id;
        final String finalUrl = urlVideo;
        final String finalNombre = nombre;
        final String finalDesc = descripcion;

        holder.itemView.setOnClickListener(v -> {
            if (finalUrl != null && !finalUrl.isEmpty()) {
                ReproductorFragment reproductor = ReproductorFragment.newInstance(
                        finalId,
                        finalUrl,
                        finalNombre,
                        finalDesc
                );

                if (v.getContext() instanceof AppCompatActivity) {
                    AppCompatActivity activity = (AppCompatActivity) v.getContext();
                    
                    // Buscamos un contenedor válido. Primero intentamos el estándar.
                    int containerId = R.id.fragment_container;
                    
                    // Si el contenedor no existe en esta vista, la app fallaría.
                    // Verificamos si existe antes de intentar la transacción.
                    if (activity.findViewById(containerId) != null) {
                        activity.getSupportFragmentManager()
                                .beginTransaction()
                                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out)
                                .replace(containerId, reproductor)
                                .addToBackStack(null)
                                .commit();
                    } else {
                        // Si no hay contenedor, lo abrimos sobre el contenido principal (android.R.id.content)
                        activity.getSupportFragmentManager()
                                .beginTransaction()
                                .replace(android.R.id.content, reproductor)
                                .addToBackStack(null)
                                .commit();
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (listaTutoriales != null) return listaTutoriales.size();
        if (listaEjercicios != null) return listaEjercicios.size();
        return 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtNombre, txtCategoria, txtDescripcion, txtDificultad, txtRepsSeries;
        ImageView imgFoto;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNombre = itemView.findViewById(R.id.tvNombreEj);
            txtCategoria = itemView.findViewById(R.id.tvCatEj);
            txtDescripcion = itemView.findViewById(R.id.tvDescripcionEj);
            txtDificultad = itemView.findViewById(R.id.tvDificultad);
            txtRepsSeries = itemView.findViewById(R.id.tvRepsSeries);
            imgFoto = itemView.findViewById(R.id.imgEjercicioIcono);
        }
    }
}
