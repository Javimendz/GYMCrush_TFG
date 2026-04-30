package com.example.gymcrush.ui.ejercicios;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
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
    private boolean isAdmin = false;
    private OnEjercicioAdminListener adminListener;

    public interface OnEjercicioAdminListener {
        void onEdit(TutorialResponseDto tutorial);
        void onDelete(TutorialResponseDto tutorial);
    }

    public EjercicioAdapter(List<TutorialResponseDto> listaTutoriales) {
        this.listaTutoriales = listaTutoriales;
    }

    public EjercicioAdapter(List<TutorialResponseDto> listaTutoriales, boolean isAdmin, OnEjercicioAdminListener listener) {
        this.listaTutoriales = listaTutoriales;
        this.isAdmin = isAdmin;
        this.adminListener = listener;
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
        String nombre = "", categoria = "", descripcion = "", duracion = "", urlVideo = "";
        String musculoTag = ""; // Nueva variable para el tag de músculo/dificultad
        Long id = -1L;
        int imagenRes = R.drawable.logo;

        // Variables para las etiquetas del reproductor
        String subtituloArmado = "";
        Integer duracionMinima = null;
        String etiquetaAzul = "";

        if (listaTutoriales != null) {
            TutorialResponseDto t = listaTutoriales.get(position);
            nombre = t.getTitulo();
            categoria = t.getNombreCategoria();
            descripcion = t.getDescripcion();
            duracion = t.getDuracionMin() + " min"; // Simplificado para el tag
            urlVideo = t.getUrlVideo();
            id = t.getId();
            musculoTag = t.getMusculoObjetivo() != null ? t.getMusculoObjetivo().toUpperCase() : "GENERAL";

            holder.txtRepsSeries.setVisibility(View.GONE);
            holder.cbCompletado.setVisibility(View.GONE);

            // ARMAMOS LOS DATOS EXTRA PARA EL REPRODUCTOR
            String musculo = t.getMusculoObjetivo() != null ? t.getMusculoObjetivo() : "";
            String equipo = t.getEquipamiento() != null ? t.getEquipamiento() : "";
            if (!musculo.isEmpty() && !equipo.isEmpty()) {
                subtituloArmado = musculo + " - " + equipo;
            } else {
                subtituloArmado = musculo + equipo; // Concatena el que quede si falta alguno
            }

            duracionMinima = t.getDuracionMin();

            if (t.getNombreCategoria() != null && !t.getNombreCategoria().isEmpty()) {
                etiquetaAzul = "Categoría: " + t.getNombreCategoria().toUpperCase();
            }

        } else {
            Ejercicio e = listaEjercicios.get(position);
            nombre = e.getNombre();
            categoria = e.getCategoria() != null ? e.getCategoria() : "Personalizado";
            descripcion = e.getDescripcion() != null ? e.getDescripcion() : "Sin descripción";
            duracion = e.getDuracion(); // Ej: "20 min" o "20"
            urlVideo = e.getUrlVideo();
            id = e.getId();

            String dificultad = e.getDificultad() != null ? e.getDificultad() : "MEDIA";
            musculoTag = dificultad.toUpperCase();

            // DATOS PARA EL REPRODUCTOR (Basado en Ejercicio)
            subtituloArmado = categoria;
            
            // Intentamos parsear la duración para el tag del reproductor
            if (duracion != null) {
                try {
                    String cleanDur = duracion.toLowerCase().replace("min", "").trim();
                    duracionMinima = Integer.parseInt(cleanDur);
                } catch (Exception ex) {
                    duracionMinima = 0;
                }
            } else {
                duracionMinima = 0;
            }

            etiquetaAzul = "Intensidad: " + dificultad.toUpperCase();

            if (e.getImagenResId() != 0) {
                imagenRes = e.getImagenResId();
            }

            if (mostrarReps) {
                holder.txtRepsSeries.setVisibility(View.VISIBLE);
                holder.txtRepsSeries.setText(e.getSeries() + " Series x " + e.getRepeticiones() + " Reps");
                holder.cbCompletado.setVisibility(View.VISIBLE);

                // Manejar el estado del checkbox sin disparar listeners recursivos
                holder.cbCompletado.setOnCheckedChangeListener(null);
                holder.cbCompletado.setChecked(e.isSeleccionado());
                holder.cbCompletado.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    e.setSeleccionado(isChecked);
                });
            } else {
                holder.txtRepsSeries.setVisibility(View.GONE);
                holder.cbCompletado.setVisibility(View.GONE);
            }
        }

        holder.txtNombre.setText(nombre);
        holder.txtCategoria.setText(categoria);
        holder.txtDescripcion.setText(descripcion);
        holder.txtDificultad.setText("Duración: " + duracion); // Mantenemos el oculto por compatibilidad
        
        // Actualizamos los nuevos Tags visuales
        holder.tvDuracionTag.setText(duracion);
        holder.tvMusculoTag.setText(musculoTag);

        if (isAdmin && listaTutoriales != null) {
            holder.imgMenu.setVisibility(View.VISIBLE);
            holder.imgMenu.setOnClickListener(v -> {
                android.widget.PopupMenu popup = new android.widget.PopupMenu(v.getContext(), v);
                popup.getMenu().add("Editar");
                popup.getMenu().add("Eliminar");
                popup.setOnMenuItemClickListener(item -> {
                    if (item.getTitle().equals("Editar")) {
                        if (adminListener != null) adminListener.onEdit(listaTutoriales.get(position));
                    } else if (item.getTitle().equals("Eliminar")) {
                        if (adminListener != null) adminListener.onDelete(listaTutoriales.get(position));
                    }
                    return true;
                });
                popup.show();
            });
        } else {
            holder.imgMenu.setVisibility(View.GONE);
        }

        if (urlVideo != null && !urlVideo.isEmpty()) {
            String urlToLoad = urlVideo;
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

            Glide.with(holder.itemView.getContext())
                    .load(urlToLoad)
                    .placeholder(R.drawable.logo)
                    .error(R.drawable.logo)
                    .centerCrop()
                    .into(holder.imgFoto);
        } else {
            holder.imgFoto.setImageResource(imagenRes);
        }

        // VARIABLES FINALES PARA ENVIAR AL ONCLICK
        final Long finalId = id;
        final String finalUrl = urlVideo;
        final String finalNombre = nombre;
        final String finalDesc = descripcion;
        final String finalSubtitulo = subtituloArmado;
        final Integer finalDuracionMin = duracionMinima;
        final String finalEtiquetaAzul = etiquetaAzul;

        holder.itemView.setOnClickListener(v -> {
            if (finalUrl != null && !finalUrl.isEmpty()) {

                // INVOCACIÓN AL REPRODUCTOR ACTUALIZADA
                ReproductorFragment reproductor = ReproductorFragment.newInstance(
                        finalId,
                        finalUrl,
                        finalNombre,
                        finalDesc,
                        finalSubtitulo,
                        finalDuracionMin,
                        finalEtiquetaAzul
                );

                if (v.getContext() instanceof AppCompatActivity) {
                    AppCompatActivity activity = (AppCompatActivity) v.getContext();
                    int containerId = R.id.fragment_container;
                    if (activity.findViewById(containerId) != null) {
                        activity.getSupportFragmentManager()
                                .beginTransaction()
                                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out)
                                .replace(containerId, reproductor)
                                .addToBackStack(null)
                                .commit();
                    } else {
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
        TextView tvDuracionTag, tvMusculoTag;
        ImageView imgFoto, imgMenu;
        CheckBox cbCompletado;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNombre = itemView.findViewById(R.id.tvNombreEj);
            txtCategoria = itemView.findViewById(R.id.tvCatEj);
            txtDescripcion = itemView.findViewById(R.id.tvDescripcionEj);
            txtDificultad = itemView.findViewById(R.id.tvDificultad);
            txtRepsSeries = itemView.findViewById(R.id.tvRepsSeries);
            tvDuracionTag = itemView.findViewById(R.id.tvDuracionTagItem);
            tvMusculoTag = itemView.findViewById(R.id.tvMusculoTagItem);
            imgFoto = itemView.findViewById(R.id.imgEjercicioIcono);
            cbCompletado = itemView.findViewById(R.id.cbCompletado);
            imgMenu = itemView.findViewById(R.id.imgMenuEjercicio);
        }
    }
}