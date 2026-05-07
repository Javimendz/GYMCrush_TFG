package com.example.gymcrush.ui.rutinas;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
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
import com.example.gymcrush.data.remote.dto.ApiResponseDto;
import com.example.gymcrush.data.remote.dto.RutinaResponseDto;
import com.example.gymcrush.data.remote.dto.VisualizacionRequestDto;
import com.example.gymcrush.data.remote.dto.VisualizacionResponseDto;
import com.example.gymcrush.data.remote.ApiService;
import com.example.gymcrush.data.remote.RetrofitClient;
import com.example.gymcrush.ui.ejercicios.Ejercicio;
import com.example.gymcrush.ui.ejercicios.EjercicioAdapter;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetalleRutinaActivity extends AppCompatActivity {

    private List<Ejercicio> listaEjercicios;
    private String nombreRutina;
    private String urlVideo;
    private String descripcion;
    private String intensidad;
    private String duracion;
    private Long rutinaId;
    private Long tutorialId;
    private Long userId;
    private ExoPlayer player;
    private PlayerView playerView;
    private ImageView imgDetalle;
    private TextView tvDuracionTagVideo;
    private TextView tvVistas;
    private final Handler progressHandler = new Handler(Looper.getMainLooper());
    private Runnable progressRunnable;
    private long lastSavedPosition = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rutina_activity_detalle);

        // Vincular con los IDs exactos de tu XML
        playerView = findViewById(R.id.playerView);
        imgDetalle = findViewById(R.id.imgDetalle);
        TextView tvTitulo = findViewById(R.id.tvTituloDetalle);
        TextView tvDuracionDetalle = findViewById(R.id.tvDuracionDetalle);
        TextView tvIntensidadDetalle = findViewById(R.id.tvIntensidadDetalle);
        TextView tvDescripcion = findViewById(R.id.tvDescripcionDetalle);
        RecyclerView rvEjercicios = findViewById(R.id.rvEjerciciosDetalle);
        TextView tvLabelEjercicios = findViewById(R.id.tvLabelEjercicios);
        tvDuracionTagVideo = findViewById(R.id.tvDuracionTagVideo);
        tvVistas = findViewById(R.id.tvVistasDetalle);
        Button btnVolver = findViewById(R.id.btnVolver);
        Button btnCompletar = findViewById(R.id.btnCompletarRutina);

        // Obtener ID de usuario
        SharedPreferences prefs = getSharedPreferences("DatosUsuario", Context.MODE_PRIVATE);
        userId = prefs.getLong("user_id", -1L);

        // Obtener datos
        nombreRutina = getIntent().getStringExtra("NOMBRE_RUTINA");
        urlVideo = getIntent().getStringExtra("URL_VIDEO");
        descripcion = getIntent().getStringExtra("DESCRIPCION");
        intensidad = getIntent().getStringExtra("INTENSIDAD");
        tutorialId = getIntent().getLongExtra("TUTORIAL_ID", -1L);
        if (tutorialId == -1L) {
            // Reintentar con RUTINA_ID si no viene TUTORIAL_ID
            tutorialId = getIntent().getLongExtra("RUTINA_ID", -1L);
        }
        if (tutorialId == -1L) tutorialId = null;

        // Si no viene tutorialId por intent, intentar recuperarlo si es un ejercicio de biblioteca
        if (tutorialId == null) {
             tutorialId = (Long) getIntent().getSerializableExtra("EJERCICIO_ID");
        }

        // Manejar duración (String o Int)
        Object durObj = getIntent().getExtras() != null ? getIntent().getExtras().get("DURACION") : null;
        String duracionStr = (durObj != null) ? durObj.toString() : "0";
        if (!duracionStr.contains("min")) duracionStr += " min";

        // Setear Textos
        if (tvTitulo != null) tvTitulo.setText(nombreRutina);
        if (tvDescripcion != null) tvDescripcion.setText(descripcion);
        if (tvDuracionDetalle != null) tvDuracionDetalle.setText(duracionStr);
        if (tvIntensidadDetalle != null) tvIntensidadDetalle.setText(intensidad != null && intensidad.startsWith("Categoría") ? intensidad : "Intensidad: " + (intensidad != null ? intensidad.toUpperCase() : "MEDIA"));

        // Ajustes si viene de la biblioteca (ejercicio individual)
        boolean esEjercicio = getIntent().getBooleanExtra("ES_EJERCICIO_BIBLIOTECA", false);
        if (esEjercicio) {
            if (btnCompletar != null) {
                btnCompletar.setVisibility(View.GONE);
            }
            if (tvLabelEjercicios != null) tvLabelEjercicios.setVisibility(View.GONE);
            if (btnVolver != null) btnVolver.setText("VOLVER A BIBLIOTECA");
            
            // Mostrar tag de duración bajo el video si aplica
            if (tvDuracionTagVideo != null) {
                tvDuracionTagVideo.setText(duracionStr);
                tvDuracionTagVideo.setVisibility(View.VISIBLE);
            }

            // Si es un ejercicio, habilitamos el video directamente si existe
            if (urlVideo != null && !urlVideo.isEmpty()) {
                setupVideoPlayer(urlVideo);
            }
        }

        // Cargar imagen o video inicial (solo si no se ha configurado el reproductor arriba)
        if (!esEjercicio) {
            if (urlVideo != null && !urlVideo.isEmpty()) {
                cargarImagen(urlVideo);
                if (imgDetalle != null) {
                    imgDetalle.setOnClickListener(v -> setupVideoPlayer(urlVideo));
                }
            } else {
                imgDetalle.setImageResource(R.drawable.ic_launcher_background);
                imgDetalle.setVisibility(View.VISIBLE);
                playerView.setVisibility(View.GONE);
            }
        }

        // Configurar RecyclerView
        listaEjercicios = RepositorioRutinas.getEjerciciosDeRutina(nombreRutina);
        if (listaEjercicios != null && !listaEjercicios.isEmpty() && !esEjercicio) {
            if (rvEjercicios != null) {
                rvEjercicios.setLayoutManager(new LinearLayoutManager(this));
                EjercicioAdapter adapter = new EjercicioAdapter(listaEjercicios, true);
                rvEjercicios.setAdapter(adapter);
            }
            if (tvLabelEjercicios != null) tvLabelEjercicios.setVisibility(View.VISIBLE);
        } else {
            if (tvLabelEjercicios != null) tvLabelEjercicios.setVisibility(View.GONE);
            if (rvEjercicios != null) rvEjercicios.setVisibility(View.GONE);
        }

        if (btnVolver != null) {
            btnVolver.setOnClickListener(v -> finish());
        }

        if (btnCompletar != null) {
            btnCompletar.setOnClickListener(v -> {
                long id = getIntent().getLongExtra("RUTINA_ID", -1L);
                if (id != -1) completarRutinaEnServidor(id);
                else finish();
            });
        }
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

        player.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int playbackState) {
                if (playbackState == Player.STATE_ENDED) {
                    guardarProgreso(true);
                }
            }
        });

        startTrackingProgress();
        // Registrar visualización inicial para contar la vista inmediatamente
        // Si no tenemos tutorialId, no podemos guardar progreso, pero intentamos registrarlo
        guardarProgreso(false);
    }

    private void startTrackingProgress() {
        if (tutorialId == null) return;
        progressRunnable = new Runnable() {
            @Override
            public void run() {
                if (player != null && player.isPlaying()) {
                    lastSavedPosition = player.getCurrentPosition();
                    guardarProgreso(false);
                }
                progressHandler.postDelayed(this, 10000);
            }
        };
        progressHandler.postDelayed(progressRunnable, 5000);
    }

    private void guardarProgreso(boolean completado) {
        if (userId == -1L || tutorialId == null) return;
        
        int seconds = (int) (player != null ? player.getCurrentPosition() / 1000 : lastSavedPosition / 1000);
        VisualizacionRequestDto dto = new VisualizacionRequestDto(userId, tutorialId, seconds, completado);

        RetrofitClient.getApiService().guardarProgreso(dto)
                .enqueue(new Callback<ApiResponseDto<VisualizacionResponseDto>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<VisualizacionResponseDto>> call, Response<ApiResponseDto<VisualizacionResponseDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    VisualizacionResponseDto data = response.body().getDatos();
                    if (data != null && data.getContadorReproducciones() != null) {
                        runOnUiThread(() -> {
                            if (tvVistas != null) {
                                tvVistas.setText(data.getContadorReproducciones() + " vistas");
                                tvVistas.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                    Log.d("ProgresoDetalle", "Progreso guardado");
                }
            }

            @Override
            public void onFailure(Call<ApiResponseDto<VisualizacionResponseDto>> call, Throwable t) {
                Log.e("ProgresoDetalle", "Error al guardar progreso: " + t.getMessage());
            }
        });
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
    protected void onPause() {
        super.onPause();
        if (player != null) {
            player.pause();
            guardarProgreso(false);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (player != null) {
            player.stop();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (progressRunnable != null) progressHandler.removeCallbacks(progressRunnable);
        if (player != null) {
            player.release();
            player = null;
        }
    }

    private void completarRutinaEnServidor(long id) {
        ApiService apiService = RetrofitClient.getApiService();
        apiService.completarRutina(id).enqueue(new Callback<ApiResponseDto<RutinaResponseDto>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<RutinaResponseDto>> call, Response<ApiResponseDto<RutinaResponseDto>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(DetalleRutinaActivity.this, "¡Buen trabajo! Ejercicio guardado", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Toast.makeText(DetalleRutinaActivity.this, "Error al completar: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponseDto<RutinaResponseDto>> call, Throwable t) {
                Toast.makeText(DetalleRutinaActivity.this, "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
