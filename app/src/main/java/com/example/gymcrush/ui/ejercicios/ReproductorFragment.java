package com.example.gymcrush.ui.ejercicios;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.OptIn;
import androidx.fragment.app.Fragment;
import androidx.media3.common.MediaItem;
import androidx.media3.common.PlaybackException;
import androidx.media3.common.Player;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.datasource.DataSource;
import androidx.media3.datasource.DefaultHttpDataSource;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory;
import androidx.media3.ui.PlayerView;

import com.example.gymcrush.R;
import com.example.gymcrush.data.remote.RetrofitClient;
import com.example.gymcrush.data.remote.dto.ApiResponseDto;
import com.example.gymcrush.data.remote.dto.VisualizacionRequestDto;
import com.example.gymcrush.data.remote.dto.VisualizacionResponseDto;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReproductorFragment extends Fragment {

    private PlayerView playerView;
    private ExoPlayer player;
    private String videoUrl;
    private String titulo;
    private String descripcion;
    private Long tutorialId;
    private Long userId;
    private String subtitulo;
    private Integer duracionMin;
    private String etiquetaExtra;
    private final Handler progressHandler = new Handler(Looper.getMainLooper());
    private Runnable progressRunnable;
    private long currentPosition = 0;


    @UnstableApi
    public static ReproductorFragment newInstance(Long tutorialId, String url, String titulo, String descripcion, String subtitulo, Integer duracionMin, String etiquetaExtra) {
        ReproductorFragment fragment = new ReproductorFragment();
        Bundle args = new Bundle();
        if (tutorialId != null) {
            args.putLong("tutorialId", tutorialId);
        } else {
            args.putLong("tutorialId", -1L);
        }
        args.putString("url", url);
        args.putString("titulo", titulo);
        args.putString("descripcion", descripcion);
        args.putString("subtitulo", subtitulo);
        if (duracionMin != null) {
            args.putInt("duracionMin", duracionMin);
        }
        args.putString("etiquetaExtra", etiquetaExtra);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            tutorialId = getArguments().getLong("tutorialId");
            videoUrl = getArguments().getString("url");
            titulo = getArguments().getString("titulo");
            descripcion = getArguments().getString("descripcion");
            subtitulo = getArguments().getString("subtitulo");
            duracionMin = getArguments().containsKey("duracionMin") ? getArguments().getInt("duracionMin") : null;
            etiquetaExtra = getArguments().getString("etiquetaExtra");
        }
        SharedPreferences prefs = requireActivity().getSharedPreferences("DatosUsuario", Context.MODE_PRIVATE);
        userId = prefs.getLong("user_id", -1L);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reproductor, container, false);

        // 2. ENLAZAMOS TODAS LAS VISTAS
        playerView = view.findViewById(R.id.player_view);
        TextView tvTitulo = view.findViewById(R.id.tvTituloVideo);
        TextView tvDesc = view.findViewById(R.id.tvDescripcionVideo);
        TextView tvSubtitulo = view.findViewById(R.id.tvSubtituloVideo);
        TextView tvDuracionTag = view.findViewById(R.id.tvDuracionTag);
        TextView tvIntensidadTag = view.findViewById(R.id.tvIntensidadTag);

        // 3. INYECTAMOS LOS DATOS Y MANEJAMOS VISIBILIDAD (Ocultar si están vacíos)
        tvTitulo.setText(titulo != null ? titulo.toUpperCase() : "TUTORIAL");
        tvDesc.setText(descripcion != null ? descripcion : "Sin descripción disponible.");

        // Subtítulo
        if (subtitulo != null && !subtitulo.isEmpty()) {
            tvSubtitulo.setText(subtitulo);
            tvSubtitulo.setVisibility(View.VISIBLE);
        } else {
            tvSubtitulo.setVisibility(View.GONE);
        }

        // Etiqueta Amarilla (Duración)
        if (duracionMin != null && duracionMin > 0) {
            tvDuracionTag.setText(duracionMin + " min");
            tvDuracionTag.setVisibility(View.VISIBLE);
        } else {
            tvDuracionTag.setVisibility(View.GONE);
        }

        // Etiqueta Azul (Extra / Intensidad / Categoría)
        if (etiquetaExtra != null && !etiquetaExtra.isEmpty()) {
            tvIntensidadTag.setText(etiquetaExtra);
            tvIntensidadTag.setVisibility(View.VISIBLE);
        } else {
            tvIntensidadTag.setVisibility(View.GONE);
        }

        // 4. INICIALIZAMOS REPRODUCTOR Y BOTÓN
        initializePlayer();

        view.findViewById(R.id.btnCerrarReproductor).setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        return view;
    }

    @OptIn(markerClass = UnstableApi.class)
    private void initializePlayer() {
        // 1. CONFIGURACIÓN DE CABECERAS (Vital para Pexels)
        // Usamos un User-Agent de navegador moderno para que el servidor no nos rechace
        String userAgent = "Mozilla/5.0 (Linux; Android 10; K) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Mobile Safari/537.36";

        DefaultHttpDataSource.Factory httpDataSourceFactory = new DefaultHttpDataSource.Factory()
                .setUserAgent(userAgent)
                .setAllowCrossProtocolRedirects(true)
                .setConnectTimeoutMs(15000)
                .setReadTimeoutMs(15000);

        // 2. CREACIÓN DEL MEDIA SOURCE
        // Esto le dice a ExoPlayer cómo debe "stremear" los datos
        DefaultMediaSourceFactory mediaSourceFactory = new DefaultMediaSourceFactory(requireContext())
                .setDataSourceFactory(httpDataSourceFactory);

        player = new ExoPlayer.Builder(requireContext())
                .setMediaSourceFactory(mediaSourceFactory)
                .build();

        playerView.setPlayer(player);

        // 3. CARGA DE LA URL
        if (videoUrl != null && !videoUrl.isEmpty() && !videoUrl.equals("null")) {
            MediaItem mediaItem = MediaItem.fromUri(videoUrl);
            player.setMediaItem(mediaItem);
            player.prepare();
            player.setPlayWhenReady(true);
        } else {
            // Si no carga, es porque videoUrl llega como null desde la BD
            Toast.makeText(requireContext(), "Error: Este ejercicio no tiene un video asignado en la base de datos", Toast.LENGTH_LONG).show();
        }

        // 4. LISTENERS DE ESTADO Y ERROR
        player.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int playbackState) {
                if (playbackState == Player.STATE_ENDED) {
                    guardarProgreso(true);
                }
            }

            @Override
            public void onPlayerError(@NonNull PlaybackException error) {
                // Si ves "UnknownHostException" aquí, es por el DNS del emulador
                String errorMsg = "Error: " + error.getErrorCodeName();
                if (error.getCause() != null) {
                    Log.e("ExoPlayer", "Causa raíz: " + error.getCause().getMessage());
                }
                Toast.makeText(requireContext(), "Fallo de red: Comprueba el internet del emulador", Toast.LENGTH_LONG).show();
            }
        });

        startTrackingProgress();
    }

    private void startTrackingProgress() {
        progressRunnable = new Runnable() {
            @Override
            public void run() {
                if (player != null && player.isPlaying()) {
                    currentPosition = player.getCurrentPosition();
                    guardarProgreso(false);
                }
                progressHandler.postDelayed(this, 10000); // Subimos a 10s para no saturar el servidor
            }
        };
        progressHandler.post(progressRunnable);
    }

    private void guardarProgreso(boolean completado) {
        if (userId == -1L || tutorialId == null || tutorialId == -1L) return;
        int seconds = (int) (currentPosition / 1000);
        VisualizacionRequestDto dto = new VisualizacionRequestDto(userId, tutorialId, seconds, completado);

        RetrofitClient.getApiService().guardarProgreso(dto).enqueue(new Callback<ApiResponseDto<VisualizacionResponseDto>>() {
            @Override public void onResponse(Call<ApiResponseDto<VisualizacionResponseDto>> call, Response<ApiResponseDto<VisualizacionResponseDto>> response) {
                if (response.isSuccessful()) Log.d("Progreso", "Guardado ok");
            }
            @Override public void onFailure(Call<ApiResponseDto<VisualizacionResponseDto>> call, Throwable t) {
                Log.e("Progreso", "Error al guardar: " + t.getMessage());
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        if (player != null) {
            player.setPlayWhenReady(false);
            guardarProgreso(false); // Guardamos al pausar
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releasePlayer();
    }

    private void releasePlayer() {
        if (progressRunnable != null) progressHandler.removeCallbacks(progressRunnable);
        if (player != null) {
            player.release();
            player = null;
        }
    }
}