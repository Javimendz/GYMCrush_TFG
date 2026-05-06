package com.example.gymcrush.data.remote;

import android.util.Log;
import com.example.gymcrush.data.remote.dto.NotificacionResponseDto;
import com.google.gson.Gson;
import io.reactivex.disposables.Disposable;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;
import ua.naiksoftware.stomp.dto.StompHeader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StompManager {
    private static final String TAG = "STOMP_MANAGER";
    private StompClient mStompClient;
    private Disposable mRestDisposable;
    private final Gson gson = new Gson();

    public interface OnNotificationReceivedListener {
        void onNotification(NotificacionResponseDto notification);
    }

    public void connect(String url, Long usuarioId, String token, OnNotificationReceivedListener listener) {
        // La URL suele ser ws://10.0.2.2:8080/ws/websocket para el emulador
        // Se eliminan los handshakeHeaders para evitar el error HTTP 500 durante el handshake.
        // La autenticación se maneja a nivel de protocolo STOMP en connectHeaders.
        mStompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, url);

        List<StompHeader> headers = new ArrayList<>();
        if (token != null && !token.isEmpty()) {
            headers.add(new StompHeader("Authorization", "Bearer " + token));
        }

        mStompClient.lifecycle().subscribe(lifecycleEvent -> {
            switch (lifecycleEvent.getType()) {
                case OPENED:
                    Log.d(TAG, "STOMP connection opened");
                    subscribeToNotifications(usuarioId, listener);
                    break;
                case ERROR:
                    Log.e(TAG, "STOMP connection error", lifecycleEvent.getException());
                    break;
                case CLOSED:
                    Log.d(TAG, "STOMP connection closed");
                    break;
            }
        }, throwable -> Log.e(TAG, "Lifecycle error", throwable));

        mStompClient.connect(headers);
    }

    private void subscribeToNotifications(Long usuarioId, OnNotificationReceivedListener listener) {
        mRestDisposable = mStompClient.topic("/topic/notificaciones/" + usuarioId)
                .subscribe(topicMessage -> {
                    Log.d(TAG, "Mensaje recibido: " + topicMessage.getPayload());
                    NotificacionResponseDto notification = gson.fromJson(topicMessage.getPayload(), NotificacionResponseDto.class);
                    if (listener != null) {
                        listener.onNotification(notification);
                    }
                }, throwable -> {
                    Log.e(TAG, "Error en suscripción", throwable);
                });
    }

    public void disconnect() {
        if (mRestDisposable != null) mRestDisposable.dispose();
        if (mStompClient != null) mStompClient.disconnect();
    }
}
