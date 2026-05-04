package com.example.gymcrush.ui.social;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gymcrush.R;
import com.example.gymcrush.data.remote.ApiService;
import com.example.gymcrush.data.remote.RetrofitClient;
import com.example.gymcrush.data.remote.dto.ApiResponseDto;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;
import ua.naiksoftware.stomp.dto.StompHeader;
import ua.naiksoftware.stomp.dto.StompMessage;

public class SocialFragment extends Fragment implements ChatAdapter.OnMessageOptionsListener {

    private static final String TAG = "SocialFragment";
    private static final String WS_URL = "ws://10.0.2.2:8080/ws/websocket";

    private RecyclerView rvChat;
    private ChatAdapter adapter;
    private List<MensajeChat> listaMensajes;
    private EditText etMensaje;
    private String nombreUsuario;
    private Long usuarioId;
    private Gson gson;
    private ApiService apiService;

    private StompClient mStompClient;
    private CompositeDisposable compositeDisposable;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_social, container, false);

        rvChat = view.findViewById(R.id.rv_chat);
        etMensaje = view.findViewById(R.id.et_mensaje);
        View btnEnviar = view.findViewById(R.id.btn_enviar);

        gson = new Gson();
        listaMensajes = new ArrayList<>();

        SharedPreferences userPrefs = getActivity().getSharedPreferences("DatosUsuario", Context.MODE_PRIVATE);
        nombreUsuario = userPrefs.getString("nombre_usuario", "Usuario");
        usuarioId = userPrefs.getLong("user_id", -1L);
        String token = userPrefs.getString("auth_token", "");

        adapter = new ChatAdapter(listaMensajes, nombreUsuario, usuarioId, this);
        rvChat.setLayoutManager(new LinearLayoutManager(getContext()));
        rvChat.setAdapter(adapter);

        btnEnviar.setOnClickListener(v -> enviarMensaje());

        apiService = RetrofitClient.getApiService();
        cargarHistorial();
        initStomp(token);

        return view;
    }

    private void cargarHistorial() {
        Log.d(TAG, "Cargando historial de chat...");
        apiService.getHistorialChat().enqueue(new Callback<List<ChatMessageDto>>() {
            @Override
            public void onResponse(Call<List<ChatMessageDto>> call, Response<List<ChatMessageDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<ChatMessageDto> historial = response.body();
                    Log.d(TAG, "Historial recibido: " + historial.size() + " mensajes");
                    
                    listaMensajes.clear();
                    for (int i = historial.size() - 1; i >= 0; i--) {
                        ChatMessageDto dto = historial.get(i);
                        listaMensajes.add(new MensajeChat(dto.id, dto.getRemitenteNombre(), dto.usuarioId, dto.contenido, dto.getHoraFormateada()));
                    }
                    adapter.notifyDataSetChanged();
                    if (!listaMensajes.isEmpty()) {
                        rvChat.scrollToPosition(listaMensajes.size() - 1);
                    }
                } else {
                    Log.e(TAG, "Error en la respuesta del historial: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<ChatMessageDto>> call, Throwable t) {
                Log.e(TAG, "Error cargando historial", t);
            }
        });
    }

    private void initStomp(String token) {
        Map<String, String> handshakeHeaders = new HashMap<>();
        if (token != null && !token.isEmpty()) {
            handshakeHeaders.put("Authorization", "Bearer " + token);
        }

        mStompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, WS_URL, handshakeHeaders);

        List<StompHeader> headers = new ArrayList<>();
        if (token != null && !token.isEmpty()) {
            headers.add(new StompHeader("Authorization", "Bearer " + token));
        }

        compositeDisposable = new CompositeDisposable();

        compositeDisposable.add(mStompClient.lifecycle()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(lifecycleEvent -> {
                    switch (lifecycleEvent.getType()) {
                        case OPENED:
                            Log.d(TAG, "Stomp connection opened");
                            break;
                        case ERROR:
                            Log.e(TAG, "Stomp connection error", lifecycleEvent.getException());
                            break;
                        case CLOSED:
                            Log.d(TAG, "Stomp connection closed");
                            break;
                    }
                }, throwable -> Log.e(TAG, "Lifecycle error", throwable)));

        // Suscribirse a mensajes nuevos
        compositeDisposable.add(mStompClient.topic("/topic/sala-publica")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(topicMessage -> {
                    Log.d(TAG, "Received message: " + topicMessage.getPayload());
                    ChatMessageDto dto = gson.fromJson(topicMessage.getPayload(), ChatMessageDto.class);
                    MensajeChat mensaje = new MensajeChat(dto.id, dto.getRemitenteNombre(), dto.usuarioId, dto.contenido, dto.getHoraFormateada());
                    
                    getActivity().runOnUiThread(() -> {
                        listaMensajes.add(mensaje);
                        adapter.notifyItemInserted(listaMensajes.size() - 1);
                        rvChat.scrollToPosition(listaMensajes.size() - 1);
                    });
                }, throwable -> Log.e(TAG, "Error on subscribe", throwable)));

        // Suscribirse a borrados
        compositeDisposable.add(mStompClient.topic("/topic/chat.borrados")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(topicMessage -> {
                    PeticionBorrado peticion = gson.fromJson(topicMessage.getPayload(), PeticionBorrado.class);
                    eliminarMensajeDeLista(peticion.id);
                }, throwable -> Log.e(TAG, "Error on topic/chat.borrados", throwable)));

        mStompClient.connect(headers);
    }

    private void eliminarMensajeDeLista(String id) {
        if (id == null) return;
        getActivity().runOnUiThread(() -> {
            for (int i = 0; i < listaMensajes.size(); i++) {
                if (id.equals(listaMensajes.get(i).getId())) {
                    listaMensajes.remove(i);
                    adapter.notifyItemRemoved(i);
                    break;
                }
            }
        });
    }

    private void enviarMensaje() {
        String texto = etMensaje.getText().toString().trim();
        if (!texto.isEmpty() && mStompClient.isConnected()) {
            ChatMessageDto dto = new ChatMessageDto();
            dto.remitente = nombreUsuario;
            dto.contenido = texto;
            dto.usuarioId = usuarioId;

            compositeDisposable.add(mStompClient.send("/app/chat.enviar", gson.toJson(dto))
                    .subscribe(() -> {
                        Log.d(TAG, "Message sent with userId: " + usuarioId);
                        getActivity().runOnUiThread(() -> etMensaje.setText(""));
                    }, throwable -> Log.e(TAG, "Error sending message", throwable)));
        }
    }

    @Override
    public void onEliminar(int position) {
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_custom_exit);
        if (dialog.getWindow() != null) dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        View cancel = dialog.findViewById(R.id.btn_cancelar_exit);
        View confirm = dialog.findViewById(R.id.btn_confirmar_exit);

        cancel.setOnClickListener(v -> dialog.dismiss());
        confirm.setOnClickListener(v -> {
            MensajeChat mensaje = listaMensajes.get(position);
            if (mensaje.getId() != null) {
                PeticionBorrado peticion = new PeticionBorrado();
                peticion.id = mensaje.getId();
                mStompClient.send("/app/chat.borrar", gson.toJson(peticion)).subscribe(() -> {
                    Log.d(TAG, "Borrado enviado");
                }, throwable -> Log.e(TAG, "Error enviando borrado", throwable));
            }
            dialog.dismiss();
        });
        dialog.show();
    }

    @Override
    public void onEditar(int position) {
        // Por ahora el backend no tiene endpoint de editar, se podría implementar similar al de borrar
        Toast.makeText(getContext(), "Edición no soportada en el servidor todavía", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCopiar(int position) {
        MensajeChat mensaje = listaMensajes.get(position);
        ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Mensaje", mensaje.getTexto());
        clipboard.setPrimaryClip(clip);
        Toast.makeText(getContext(), "Copiado al portapapeles", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mStompClient != null) mStompClient.disconnect();
        if (compositeDisposable != null) compositeDisposable.dispose();
    }

    // Clases DTO internas para coincidir con el Backend
    public static class ChatMessageDto {
        public String id;
        public String contenido;
        public Long usuarioId;

        @SerializedName(value = "remitente", alternate = {"emisor"})
        public String remitente;

        @SerializedName(value = "hora", alternate = {"fechaEnvio"})
        public String hora;

        public String getRemitenteNombre() {
            return remitente != null ? remitente : "Desconocido";
        }

        public String getHoraFormateada() {
            if (hora != null && hora.contains("T")) {
                try {
                    // Formato ISO del servidor: 2026-04-18T22:18:44.619494
                    LocalDateTime dt = LocalDateTime.parse(hora);
                    return dt.format(DateTimeFormatter.ofPattern("HH:mm"));
                } catch (Exception e) {
                    return hora;
                }
            }
            return hora;
        }
    }

    private static class PeticionBorrado {
        String id;
    }
}
