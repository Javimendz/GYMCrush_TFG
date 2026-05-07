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
    private String videoUrl, titulo, descripcion, subtitulo, etiquetaExtra;
    private Long tutorialId, userId;
    private Integer duracionMin;

    private final Handler progressHandler = new Handler(Looper.getMainLooper());
    private Runnable progressRunnable;
    private long currentPosition = 0;

    // VARIABLES DE CONTROL
    private boolean puedeGuardarProgreso = false;
    private long progresoInicialMs = 0;
    private int ultimoProgresoGuardado = -1; // Filtro Anti-Spam para la BD

    @UnstableApi
    public static ReproductorFragment newInstance(Long tutorialId, String url, String titulo, String descripcion, String subtitulo, Integer duracionMin, String etiquetaExtra) {
        ReproductorFragment fragment = new ReproductorFragment();
        Bundle args = new Bundle();
        args.putLong("tutorialId", tutorialId != null ? tutorialId : -1L);
        args.putString("url", url);
        args.putString("titulo", titulo);
        args.putString("descripcion", descripcion);
        args.putString("subtitulo", subtitulo);
        if (duracionMin != null) args.putInt("duracionMin", duracionMin);
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
        playerView = view.findViewById(R.id.player_view);

        TextView tvTitulo = view.findViewById(R.id.tvTituloVideo);
        tvTitulo.setText(titulo != null ? titulo.toUpperCase() : "TUTORIAL");

        // PASO 1: Pedimos los datos al servidor.
        recuperarProgresoDeServidor();

        view.findViewById(R.id.btnCerrarReproductor).setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        return view;
    }

    private void recuperarProgresoDeServidor() {
        if (userId == -1L || tutorialId == null || tutorialId == -1L) {
            iniciarReproductorSeguro();
            return;
        }

        RetrofitClient.getApiService().obtenerProgreso(userId, tutorialId).enqueue(new Callback<ApiResponseDto<VisualizacionResponseDto>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<VisualizacionResponseDto>> call, Response<ApiResponseDto<VisualizacionResponseDto>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getDatos() != null) {
                    Integer segundos = response.body().getDatos().getProgresoSegundos();
                    if (segundos != null && segundos > 0) {
                        progresoInicialMs = segundos * 1000L;
                        Log.d("VideoProgreso", "Servidor confirma reanudación en segundo: " + segundos);
                    }
                    actualizarContadorUI(response.body().getDatos());
                }

                puedeGuardarProgreso = true;

                // PASO 2: Ahora que tenemos la respuesta, CREAMOS el reproductor.
                if (player == null) iniciarReproductorSeguro();
            }

            @Override
            public void onFailure(Call<ApiResponseDto<VisualizacionResponseDto>> call, Throwable t) {
                Log.e("VideoProgreso", "Error API: " + t.getMessage());
                puedeGuardarProgreso = true;
                if (player == null) iniciarReproductorSeguro();
            }
        });
    }

    @OptIn(markerClass = UnstableApi.class)
    private void iniciarReproductorSeguro() {
        if (!isAdded() || getContext() == null) return;

        String userAgent = "Mozilla/5.0 (Linux; Android 10; K) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Mobile Safari/537.36";
        DefaultHttpDataSource.Factory httpDataSourceFactory = new DefaultHttpDataSource.Factory()
                .setUserAgent(userAgent)
                .setAllowCrossProtocolRedirects(true);
        DefaultMediaSourceFactory mediaSourceFactory = new DefaultMediaSourceFactory(requireContext())
                .setDataSourceFactory(httpDataSourceFactory);

        player = new ExoPlayer.Builder(requireContext()).setMediaSourceFactory(mediaSourceFactory).build();
        playerView.setPlayer(player);

        if (videoUrl != null && !videoUrl.isEmpty() && !videoUrl.equals("null")) {
            player.setMediaItem(MediaItem.fromUri(videoUrl));

            if (progresoInicialMs > 0) {
                player.seekTo(progresoInicialMs);
            }

            // AÑADIDO: Si quieres que el video se repita en bucle automáticamente al terminar
            player.setRepeatMode(Player.REPEAT_MODE_ONE);

            player.prepare();
            player.setPlayWhenReady(true);
        }

        player.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int playbackState) {
                // NOTA: Si repeatMode está activado, STATE_ENDED nunca se llamará.
                // El video simplemente volverá a empezar.
                // Si necesitas marcar el video como "completado" en tu BD al terminar,
                // quizás debas quitar el RepeatMode o manejarlo de otra forma.
                if (playbackState == Player.STATE_ENDED) {
                    guardarProgreso(true);
                }
            }
            @Override
            public void onPlayerError(@NonNull PlaybackException error) {
                Log.e("VideoProgreso", "Error reproductor: " + error.getMessage());
            }
        });

        startTrackingProgress();
    }

    private void startTrackingProgress() {
        progressRunnable = new Runnable() {
            @Override
            public void run() {
                if (player != null && player.isPlaying()) {
                    guardarProgreso(false);
                }
                progressHandler.postDelayed(this, 10000);
            }
        };
        progressHandler.postDelayed(progressRunnable, 5000);
    }

    private void guardarProgreso(boolean completado) {
        if (!puedeGuardarProgreso || userId == -1L || tutorialId == null || tutorialId == -1L) return;

        long pos = (player != null) ? player.getCurrentPosition() : currentPosition;
        int seconds = (int) (pos / 1000);

        if (seconds <= 0) {
            Log.w("ProgresoDetalle", "Intento de guardar 0 segundos bloqueado.");
            return;
        }

        if (seconds == ultimoProgresoGuardado && !completado) return;

        ultimoProgresoGuardado = seconds;
        currentPosition = pos;

        Log.d("ProgresoDetalle", "Enviando POST: " + seconds + " segundos");
        VisualizacionRequestDto dto = new VisualizacionRequestDto(userId, tutorialId, seconds, completado);

        RetrofitClient.getApiService().guardarProgreso(dto).enqueue(new Callback<ApiResponseDto<VisualizacionResponseDto>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<VisualizacionResponseDto>> call, Response<ApiResponseDto<VisualizacionResponseDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("ProgresoDetalle", "Progreso guardado correctamente en BD");
                    actualizarContadorUI(response.body().getDatos());
                }
            }
            @Override
            public void onFailure(Call<ApiResponseDto<VisualizacionResponseDto>> call, Throwable t) {
                Log.e("ProgresoDetalle", "Fallo al guardar: " + t.getMessage());
            }
        });
    }

    private void actualizarContadorUI(VisualizacionResponseDto datos) {
        if (getView() != null && datos != null && datos.getContadorReproducciones() != null) {
            TextView tvReproducciones = getView().findViewById(R.id.tvReproducciones);
            if (tvReproducciones != null) {
                tvReproducciones.setText(datos.getContadorReproducciones() + " reproducciones");
                tvReproducciones.setTextColor(android.graphics.Color.parseColor("#00E676"));
                tvReproducciones.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (player != null) {
            guardarProgreso(false);
            player.setPlayWhenReady(false);
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