package com.example.gymcrush.ui.support;

import com.example.gymcrush.Config;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import android.app.AlertDialog;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gymcrush.R;
import com.example.gymcrush.data.remote.ApiService;
import com.example.gymcrush.data.remote.RetrofitClient;
import com.example.gymcrush.data.remote.dto.ApiResponseDto;
import com.example.gymcrush.data.remote.dto.TicketResponseDto;
import com.example.gymcrush.ui.social.ChatAdapter;
import com.example.gymcrush.ui.social.MensajeChat;
import com.example.gymcrush.ui.social.SocialFragment.ChatMessageDto;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;
import ua.naiksoftware.stomp.dto.StompHeader;

public class TicketChatFragment extends Fragment implements ChatAdapter.OnMessageOptionsListener {

    private static final String TAG = "TicketChatFragment";
    // Usamos la configuración centralizada para el WebSocket
    private static final String WS_URL = Config.WS_URL;

    private RecyclerView rvChat;
    private ChatAdapter adapter;
    private List<MensajeChat> listaMensajes;
    private EditText etMensaje;
    private TextView tvSubtitle;
    private ImageView btnOptions;

    private Long ticketId;
    private String ticketAsunto;
    private String nombreUsuario;
    private Long usuarioId;
    private boolean isAdmin;

    private StompClient mStompClient;
    private CompositeDisposable compositeDisposable;
    private Gson gson;
    private ApiService apiService;

    public static TicketChatFragment newInstance(Long ticketId, String asunto) {
        TicketChatFragment fragment = new TicketChatFragment();
        Bundle args = new Bundle();
        args.putLong("ticket_id", ticketId);
        args.putString("asunto", asunto);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            ticketId = getArguments().getLong("ticket_id");
            ticketAsunto = getArguments().getString("asunto");
        }
        gson = new Gson();
        compositeDisposable = new CompositeDisposable();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ticket_chat, container, false);

        rvChat = view.findViewById(R.id.rvChat);
        etMensaje = view.findViewById(R.id.etChatMessage);
        tvSubtitle = view.findViewById(R.id.tvChatSubtitle);
        btnOptions = view.findViewById(R.id.btnChatOptions);
        TextView tvTitle = view.findViewById(R.id.tvChatTitle);

        apiService = RetrofitClient.getApiService();

        tvTitle.setText("Ticket #" + ticketId);
        tvSubtitle.setText(ticketAsunto != null ? ticketAsunto : "Cargando...");

        SharedPreferences prefs = requireContext().getSharedPreferences("DatosUsuario", Context.MODE_PRIVATE);
        nombreUsuario = prefs.getString("nombre_usuario", "Usuario");
        usuarioId = prefs.getLong("user_id", -1L);
        String token = prefs.getString("auth_token", "");
        java.util.Set<String> roles = prefs.getStringSet("roles", new java.util.HashSet<>());
        isAdmin = roles.contains("ROLE_ADMIN") || roles.contains("ADMIN");

        listaMensajes = new ArrayList<>();
        adapter = new ChatAdapter(listaMensajes, nombreUsuario, usuarioId, this);
        rvChat.setLayoutManager(new LinearLayoutManager(getContext()));
        rvChat.setAdapter(adapter);

        view.findViewById(R.id.btnBackChat).setOnClickListener(v -> {
            if (getActivity() != null) getActivity().getSupportFragmentManager().popBackStack();
        });

        view.findViewById(R.id.btnSendChat).setOnClickListener(v -> enviarMensaje());

        cargarHistorial();
        setupOptionsButton();
        initStomp(token);

        return view;
    }

    private void cargarHistorial() {
        Log.d(TAG, "Cargando historial para ticket: " + ticketId);
        apiService.getHistorialTicket(String.valueOf(ticketId)).enqueue(new retrofit2.Callback<List<ChatMessageDto>>() {
            @Override
            public void onResponse(retrofit2.Call<List<ChatMessageDto>> call, retrofit2.Response<List<ChatMessageDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listaMensajes.clear();
                    for (ChatMessageDto dto : response.body()) {
                        boolean esMio = false;
                        if (dto.usuarioId != null && dto.usuarioId != 0) {
                            esMio = dto.usuarioId.equals(usuarioId);
                        } else if (dto.remitente != null && nombreUsuario != null) {
                            esMio = dto.remitente.equalsIgnoreCase(nombreUsuario);
                        }

                        MensajeChat mensaje = new MensajeChat(dto.id, dto.getRemitenteNombre(), dto.usuarioId, dto.contenido, dto.getHoraFormateada());
                        mensaje.setEsMio(esMio);
                        listaMensajes.add(mensaje);
                    }
                    adapter.notifyDataSetChanged();
                    if (!listaMensajes.isEmpty()) {
                        rvChat.scrollToPosition(listaMensajes.size() - 1);
                    }
                } else {
                    Log.e(TAG, "Error al cargar historial: " + response.code());
                }
            }

            @Override
            public void onFailure(retrofit2.Call<List<ChatMessageDto>> call, Throwable t) {
                Log.e(TAG, "Error de red cargando historial", t);
            }
        });
    }

    private void setupOptionsButton() {
        btnOptions.setVisibility(View.VISIBLE);
        btnOptions.setOnClickListener(v -> {
            if (isAdmin) {
                mostrarDialogoEstadosAdmin();
            } else {
                mostrarDialogoEstadosUsuario();
            }
        });
    }

    private void mostrarDialogoEstadosAdmin() {
        // Ajustado para coincidir con EnumEstadoTicket del backend (ABIERTO, CERRADO, PENDIENTE, RESUELTO)
        String[] opcionesMostrar = {"Pendiente", "Abierto", "Resuelto", "Cerrado"};
        String[] estadosBackend = {"PENDIENTE", "ABIERTO", "RESUELTO", "CERRADO"};

        new AlertDialog.Builder(getContext())
                .setTitle("Cambiar Estado del Ticket")
                .setItems(opcionesMostrar, (dialog, which) -> {
                    actualizarEstado(estadosBackend[which]);
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void mostrarDialogoEstadosUsuario() {
        new AlertDialog.Builder(getContext())
                .setTitle("Gestión de Ticket")
                .setMessage("¿Quieres marcar este ticket como resuelto?")
                .setPositiveButton("Sí, Resuelto", (dialog, which) -> {
                    actualizarEstado("RESUELTO");
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void actualizarEstado(String nuevoEstado) {
        tvSubtitle.setText("Actualizando estado...");
        apiService.resolverTicket(ticketId, nuevoEstado).enqueue(new retrofit2.Callback<ApiResponseDto<TicketResponseDto>>() {
            @Override
            public void onResponse(retrofit2.Call<ApiResponseDto<TicketResponseDto>> call, retrofit2.Response<ApiResponseDto<TicketResponseDto>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Estado actualizado a " + nuevoEstado, Toast.LENGTH_SHORT).show();
                    tvSubtitle.setText("Estado: " + nuevoEstado + " - " + ticketAsunto);
                } else {
                    Log.e(TAG, "Error 400/500 al actualizar estado: " + response.code());
                    Toast.makeText(getContext(), "Error al actualizar (Enum mismatch?)", Toast.LENGTH_SHORT).show();
                    tvSubtitle.setText("Error - " + ticketAsunto);
                }
            }

            @Override
            public void onFailure(retrofit2.Call<ApiResponseDto<TicketResponseDto>> call, Throwable t) {
                Toast.makeText(getContext(), "Error de red", Toast.LENGTH_SHORT).show();
                tvSubtitle.setText("Error de red");
            }
        });
    }

    private void initStomp(String token) {
        if (token == null || token.isEmpty()) return;

        // Se eliminan los handshakeHeaders para evitar el error HTTP 500 durante el handshake.
        // La autenticación se maneja a nivel de protocolo STOMP en connectHeaders.
        mStompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, WS_URL);

        List<StompHeader> connectHeaders = new ArrayList<>();
        connectHeaders.add(new StompHeader("Authorization", "Bearer " + token));
        connectHeaders.add(new StompHeader("userId", String.valueOf(usuarioId)));

        compositeDisposable.add(mStompClient.lifecycle()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(lifecycleEvent -> {
                    switch (lifecycleEvent.getType()) {
                        case OPENED:
                            Log.i(TAG, "Conexión chat ticket abierta");
                            tvSubtitle.setText("En línea - " + ticketAsunto);
                            break;
                        case ERROR:
                            Log.e(TAG, "Error Stomp", lifecycleEvent.getException());
                            tvSubtitle.setText("Error de conexión");
                            break;
                        case CLOSED:
                            tvSubtitle.setText("Desconectado");
                            break;
                    }
                }));

        // Suscripción robusta con logs mejorados
        compositeDisposable.add(mStompClient.topic("/topic/tickets." + ticketId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(topicMessage -> {
                    Log.i(TAG, "NUEVO MENSAJE RECIBIDO: " + topicMessage.getPayload());
                    try {
                        ChatMessageDto dto = gson.fromJson(topicMessage.getPayload(), ChatMessageDto.class);
                        if (dto != null) {
                            // Uso de las variables correctas: usuarioId y nombreUsuario
                            boolean esMio = false;
                            if (dto.usuarioId != null && dto.usuarioId != 0) {
                                esMio = dto.usuarioId.equals(usuarioId);
                            } else if (dto.remitente != null && nombreUsuario != null) {
                                esMio = dto.remitente.equalsIgnoreCase(nombreUsuario);
                            }

                            Log.d(TAG, "Mensaje procesado - Es mío: " + esMio + " | Emisor: " + dto.remitente);

                            MensajeChat mensaje = new MensajeChat(
                                    dto.id,
                                    dto.remitente != null ? dto.remitente : "Soporte",
                                    dto.usuarioId,
                                    dto.contenido,
                                    dto.getHoraFormateada()
                            );
                            mensaje.setEsMio(esMio);

                            getActivity().runOnUiThread(() -> {
                                listaMensajes.add(mensaje);
                                adapter.notifyItemInserted(listaMensajes.size() - 1);
                                rvChat.scrollToPosition(listaMensajes.size() - 1);
                            });
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parseando mensaje: " + topicMessage.getPayload(), e);
                    }
                }, throwable -> {
                    Log.e(TAG, "Error crítico en la suscripción al ticket #" + ticketId, throwable);
                    // Intentar reconectar si la suscripción muere
                    Toast.makeText(getContext(), "Error de conexión en el chat", Toast.LENGTH_SHORT).show();
                }));

        mStompClient.connect(connectHeaders);
    }

    private void enviarMensaje() {
        String texto = etMensaje.getText().toString().trim();
        if (!texto.isEmpty() && mStompClient != null && mStompClient.isConnected()) {
            // El nuevo backend usa ChatRequestDto que espera contenido y tipo
            Map<String, Object> payload = new HashMap<>();
            payload.put("contenido", texto);
            payload.put("tipo", "CHAT"); // Cambiado de TEXTO a CHAT para consistencia con el TFG

            // Ajustado para usar el MessageMapping "/chat.ticket.{ticketId}"
            compositeDisposable.add(mStompClient.send("/app/chat.ticket." + ticketId, gson.toJson(payload))
                    .subscribe(() -> {
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> etMensaje.setText(""));
                        }
                    }, throwable -> Log.e(TAG, "Error al enviar mensaje a ticket", throwable)));
        }
    }

    @Override
    public void onEliminar(int position) { }

    @Override
    public void onEditar(int position) { }

    @Override
    public void onCopiar(int position) { }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mStompClient != null) mStompClient.disconnect();
        if (compositeDisposable != null) compositeDisposable.dispose();
    }
}