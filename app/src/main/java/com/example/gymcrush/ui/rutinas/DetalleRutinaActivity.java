package com.example.gymcrush.ui.rutinas;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import com.example.gymcrush.R;
import com.example.gymcrush.ui.ejercicios.Ejercicio;
import com.example.gymcrush.ui.ejercicios.EjercicioAdapter;
import java.util.ArrayList;
import java.util.List;

public class DetalleRutinaActivity extends AppCompatActivity {

    private List<Ejercicio> listaEjercicios;
    private String nombreRutina;
    private String urlVideo;
    private String descripcion;
    private String intensidad;
    private String duracion;
    private Long rutinaId;
    private ExoPlayer player;
    private PlayerView playerView;
    private ImageView imgDetalle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rutina_activity_detalle);

        // 1. Recogemos los datos del Intent
        rutinaId = getIntent().getLongExtra("RUTINA_ID", -1L);
        nombreRutina = getIntent().getStringExtra("NOMBRE_RUTINA");
        urlVideo = getIntent().getStringExtra("URL_VIDEO");
        descripcion = getIntent().getStringExtra("DESCRIPCION");
        intensidad = getIntent().getStringExtra("INTENSIDAD");
        
        // Intentamos obtener duración como String o como int
        Object durObj = getIntent().getExtras().get("DURACION");
        if (durObj instanceof Integer) {
            duracion = durObj + " min";
        } else if (durObj instanceof String) {
            duracion = (String) durObj;
            if (!duracion.contains("min")) duracion += " min";
        } else {
            duracion = "-- min";
        }

        // 2. Referencias de UI
        TextView tvTitulo = findViewById(R.id.tvTituloDetalle);
        TextView tvDesc = findViewById(R.id.tvDescripcionDetalle);
        TextView tvDuracion = findViewById(R.id.tvDuracionDetalle);
        TextView tvIntensidad = findViewById(R.id.tvIntensidadDetalle);
        imgDetalle = findViewById(R.id.imgDetalle);
        playerView = findViewById(R.id.playerView);
        TextView tvLabelEjercicios = findViewById(R.id.tvLabelEjercicios);
        RecyclerView rv = findViewById(R.id.rvEjerciciosDetalle);

        // 3. Establecer textos básicos
        if (tvTitulo != null) tvTitulo.setText(nombreRutina != null ? nombreRutina : "Detalle de Rutina");
        if (tvDesc != null) {
            tvDesc.setText(descripcion != null && !descripcion.isEmpty() ? descripcion : "Sin descripción disponible para este entrenamiento.");
        }
        
        if (tvDuracion != null) {
            tvDuracion.setText(duracion);
        }
        
        if (tvIntensidad != null) {
            tvIntensidad.setText("Intensidad: " + (intensidad != null ? intensidad : "Media"));
        }

        // 4. Cargar imagen o Video
        if (urlVideo != null && !urlVideo.isEmpty()) {
            String urlLower = urlVideo.toLowerCase();
            if (urlLower.contains(".mp4") || urlLower.contains("video") || urlLower.contains("pexels.com/video")) {
                setupVideoPlayer(urlVideo);
            } else {
                cargarImagen(urlVideo);
            }
        } else {
            if (imgDetalle != null) {
                imgDetalle.setVisibility(View.VISIBLE);
                imgDetalle.setImageResource(R.drawable.ic_launcher_background);
            }
            if (playerView != null) playerView.setVisibility(View.GONE);
        }

        // 5. Configurar lista de sub-ejercicios si existen
        if (rv != null) {
            rv.setLayoutManager(new LinearLayoutManager(this));
            
            // Intentamos obtener la lista de ejercicios de varias fuentes
            listaEjercicios = RepositorioRutinas.getEjerciciosDeRutina(nombreRutina);

            // Si es una rutina con múltiples ejercicios (como la de brazos que mencionas)
            if (listaEjercicios != null && !listaEjercicios.isEmpty()) {
                tvLabelEjercicios.setVisibility(View.VISIBLE);
                
                // Ocultar descripción y video principal para dar prioridad a la lista de ejercicios
                if (tvDesc != null) tvDesc.setVisibility(View.GONE);
                if (playerView != null) playerView.setVisibility(View.GONE);
                if (imgDetalle != null) imgDetalle.setVisibility(View.GONE);
                findViewById(R.id.cardImagen).setVisibility(View.GONE);

                EjercicioAdapter adapter = new EjercicioAdapter(listaEjercicios, true);
                rv.setAdapter(adapter);
            } else {
                tvLabelEjercicios.setVisibility(View.GONE);
                listaEjercicios = new ArrayList<>();
            }
        }

        // 6. Botón COMPLETAR RUTINA
        Button btnCompletar = findViewById(R.id.btnCompletarRutina);
        long asignacionId = getIntent().getLongExtra("ASIGNACION_ID", -1L);

        btnCompletar.setOnClickListener(v -> {
            // Si hay sub-ejercicios, verificamos progreso localmente
            if (!listaEjercicios.isEmpty()) {
                int completados = 0;
                for (Ejercicio e : listaEjercicios) {
                    if (e.isSeleccionado()) completados++;
                }
                
                if (completados == 0) {
                    Toast.makeText(this, "Marca al menos un ejercicio como completado", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            
            // Si tenemos un ID de asignación del backend, llamamos a la API
            if (asignacionId != -1L) {
                completarRutinaEnServidor(asignacionId);
            } else {
                // Caso local o manual
                RepositorioRutinas.marcarComoCompletada(nombreRutina);
                Toast.makeText(this, "¡Felicidades! Entrenamiento finalizado", Toast.LENGTH_LONG).show();
                finish();
            }
        });

        findViewById(R.id.btnVolver).setOnClickListener(v -> finish());
    }

    private void setupVideoPlayer(String url) {
        if (player != null) player.release();
        player = new ExoPlayer.Builder(this).build();
        playerView.setPlayer(player);
        playerView.setVisibility(View.VISIBLE);
        if (imgDetalle != null) imgDetalle.setVisibility(View.GONE);

        MediaItem mediaItem = MediaItem.fromUri(url);
        player.setMediaItem(mediaItem);
        player.setRepeatMode(Player.REPEAT_MODE_ALL);
        player.prepare();
        player.play();
    }

    private void cargarImagen(String url) {
        playerView.setVisibility(View.GONE);
        if (imgDetalle != null) {
            imgDetalle.setVisibility(View.VISIBLE);
            
            String urlToLoad = url;
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

            Glide.with(this)
                    .load(urlToLoad)
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .error(R.drawable.ic_launcher_background)
                    .into(imgDetalle);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.release();
            player = null;
        }
    }

    private void completarRutinaEnServidor(long id) {
        com.example.gymcrush.data.remote.ApiService apiService = com.example.gymcrush.data.remote.RetrofitClient.getApiService();
        apiService.completarRutina(id).enqueue(new retrofit2.Callback<com.example.gymcrush.data.remote.dto.ApiResponseDto<com.example.gymcrush.data.remote.dto.RutinaResponseDto>>() {
            @Override
            public void onResponse(retrofit2.Call<com.example.gymcrush.data.remote.dto.ApiResponseDto<com.example.gymcrush.data.remote.dto.RutinaResponseDto>> call, retrofit2.Response<com.example.gymcrush.data.remote.dto.ApiResponseDto<com.example.gymcrush.data.remote.dto.RutinaResponseDto>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(DetalleRutinaActivity.this, "¡Buen trabajo! Ejercicio guardado", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Toast.makeText(DetalleRutinaActivity.this, "Error al completar: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<com.example.gymcrush.data.remote.dto.ApiResponseDto<com.example.gymcrush.data.remote.dto.RutinaResponseDto>> call, Throwable t) {
                Toast.makeText(DetalleRutinaActivity.this, "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
