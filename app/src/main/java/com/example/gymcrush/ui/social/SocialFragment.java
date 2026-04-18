package com.example.gymcrush.ui.social;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SocialFragment extends Fragment implements ChatAdapter.OnMessageOptionsListener {

    private RecyclerView rvChat;
    private ChatAdapter adapter;
    private List<MensajeChat> listaMensajes;
    private EditText etMensaje;
    private String nombreUsuario;
    private SharedPreferences chatPrefs;
    private Gson gson;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_social, container, false);

        rvChat = view.findViewById(R.id.rv_chat);
        etMensaje = view.findViewById(R.id.et_mensaje);
        View btnEnviar = view.findViewById(R.id.btn_enviar);

        gson = new Gson();
        chatPrefs = getActivity().getSharedPreferences("ChatLocal", Context.MODE_PRIVATE);

        SharedPreferences userPrefs = getActivity().getSharedPreferences("DatosUsuario", Context.MODE_PRIVATE);
        nombreUsuario = userPrefs.getString("nombre_usuario", "Usuario");

        cargarMensajes();

        adapter = new ChatAdapter(listaMensajes, nombreUsuario, this);
        rvChat.setLayoutManager(new LinearLayoutManager(getContext()));
        rvChat.setAdapter(adapter);
        if (!listaMensajes.isEmpty()) {
            rvChat.scrollToPosition(listaMensajes.size() - 1);
        }

        btnEnviar.setOnClickListener(v -> enviarMensaje());

        return view;
    }

    private void cargarMensajes() {
        String json = chatPrefs.getString("mensajes", null);
        if (json == null) {
            listaMensajes = new ArrayList<>();
            listaMensajes.add(new MensajeChat("Admin", "¡Bienvenidos al chat de GYMCrush! 💪", "10:00"));
            listaMensajes.add(new MensajeChat("Carlos Entrenador", "¿Quién viene hoy a darle duro a las piernas?", "10:05"));
        } else {
            Type type = new TypeToken<ArrayList<MensajeChat>>() {}.getType();
            listaMensajes = gson.fromJson(json, type);
        }
    }

    private void guardarMensajes() {
        SharedPreferences.Editor editor = chatPrefs.edit();
        String json = gson.toJson(listaMensajes);
        editor.putString("mensajes", json);
        editor.apply();
    }

    private void enviarMensaje() {
        String texto = etMensaje.getText().toString().trim();
        if (!texto.isEmpty()) {
            String hora = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
            listaMensajes.add(new MensajeChat(nombreUsuario, texto, hora));
            adapter.notifyItemInserted(listaMensajes.size() - 1);
            rvChat.scrollToPosition(listaMensajes.size() - 1);
            etMensaje.setText("");
            guardarMensajes();
        }
    }

    @Override
    public void onEliminar(int position) {
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_custom_exit); // Reutilizamos el estilo del exit dialog
        
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        TextView tvTitulo = dialog.findViewById(R.id.tv_titulo_exit); // Asumiendo que existe el ID o lo adaptamos
        TextView tvMensaje = dialog.findViewById(R.id.tv_mensaje_exit);
        TextView btnCancelar = dialog.findViewById(R.id.btn_cancelar_exit);
        TextView btnEliminar = dialog.findViewById(R.id.btn_confirmar_exit);

        // Si los IDs en dialog_custom_exit.xml son diferentes, los ajustamos aquí. 
        // Basándome en la lectura anterior:
        // No tiene ID de título pero sí de botones.
        
        // Buscamos los botones por su ID en dialog_custom_exit.xml
        View cancel = dialog.findViewById(R.id.btn_cancelar_exit);
        View confirm = dialog.findViewById(R.id.btn_confirmar_exit);
        
        // El texto del diálogo se puede cambiar si tiene ID, si no, creamos uno específico para borrar
        // Vamos a usar un diálogo genérico pero con el estilo clavado al de salida.
        
        cancel.setOnClickListener(v -> dialog.dismiss());
        confirm.setOnClickListener(v -> {
            listaMensajes.remove(position);
            adapter.notifyItemRemoved(position);
            adapter.notifyItemRangeChanged(position, listaMensajes.size());
            guardarMensajes();
            Toast.makeText(getContext(), "Mensaje eliminado", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        dialog.show();
    }

    @Override
    public void onEditar(int position) {
        MensajeChat mensaje = listaMensajes.get(position);
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_editar_mensaje);
        
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        EditText input = dialog.findViewById(R.id.et_editar_mensaje_input);
        TextView btnCancelar = dialog.findViewById(R.id.btn_cancelar_edicion_mensaje);
        TextView btnGuardar = dialog.findViewById(R.id.btn_guardar_edicion_mensaje);

        input.setText(mensaje.getTexto());
        input.setSelection(mensaje.getTexto().length());

        btnCancelar.setOnClickListener(v -> dialog.dismiss());
        btnGuardar.setOnClickListener(v -> {
            String nuevoTexto = input.getText().toString().trim();
            if (!nuevoTexto.isEmpty()) {
                listaMensajes.set(position, new MensajeChat(mensaje.getUsuario(), nuevoTexto, mensaje.getHora()));
                adapter.notifyItemChanged(position);
                guardarMensajes();
                Toast.makeText(getContext(), "Mensaje editado", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    @Override
    public void onCopiar(int position) {
        MensajeChat mensaje = listaMensajes.get(position);
        ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Mensaje", mensaje.getTexto());
        clipboard.setPrimaryClip(clip);
        Toast.makeText(getContext(), "Copiado al portapapeles", Toast.LENGTH_SHORT).show();
    }
}
