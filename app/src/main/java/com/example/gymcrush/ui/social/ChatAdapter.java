package com.example.gymcrush.ui.social;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.gymcrush.R;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private List<MensajeChat> mensajes;
    private String usuarioActual;
    private Long usuarioIdActual;
    private OnMessageOptionsListener optionsListener;

    public interface OnMessageOptionsListener {
        void onEliminar(int position);
        void onEditar(int position);
        void onCopiar(int position);
    }

    public ChatAdapter(List<MensajeChat> mensajes, String usuarioActual, Long usuarioIdActual, OnMessageOptionsListener optionsListener) {
        this.mensajes = mensajes;
        this.usuarioActual = usuarioActual;
        this.usuarioIdActual = usuarioIdActual;
        this.optionsListener = optionsListener;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mensaje_chat, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        MensajeChat mensaje = mensajes.get(position);
        holder.tvUsuario.setText(mensaje.getUsuario());
        holder.tvTexto.setText(mensaje.getTexto());
        holder.tvHora.setText(mensaje.getHora());

        // Mejora de la comprobación de autoría
        boolean esMio = false;
        
        // 1. Comparación por ID (más fiable)
        if (usuarioIdActual != null && usuarioIdActual != -1L && mensaje.getUsuarioId() != null) {
            // Comparamos los valores long directamente
            esMio = usuarioIdActual.longValue() == mensaje.getUsuarioId().longValue();
        }
        
        // 2. Fallback por nombre de usuario si el ID no está o no coincide
        if (!esMio && mensaje.getUsuario() != null && usuarioActual != null) {
            String nombreMsg = mensaje.getUsuario().trim().toLowerCase();
            String nombreYo = usuarioActual.trim().toLowerCase();
            esMio = nombreMsg.equals(nombreYo);
        }

        // Log para depurar si hay problemas de reconocimiento
        android.util.Log.d("ChatAdapter", "Msg de: " + mensaje.getUsuario() + " (ID:" + mensaje.getUsuarioId() + ") | Yo: " + usuarioActual + " (ID:" + usuarioIdActual + ") | esMio: " + esMio);

        final boolean finalEsMio = esMio;
        
        // Aplicamos el click largo tanto al texto como al contenedor para asegurar que se detecte
        View.OnLongClickListener longClickListener = v -> {
            showCustomOptionsDialog(v, position, finalEsMio);
            return true;
        };
        
        holder.itemView.setOnLongClickListener(longClickListener);
        holder.tvTexto.setOnLongClickListener(longClickListener);
    }

    private void showCustomOptionsDialog(View view, int position, boolean esMio) {
        Dialog dialog = new Dialog(view.getContext());
        dialog.setContentView(R.layout.dialog_mensaje_opciones);
        
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        View btnCopiar = dialog.findViewById(R.id.btn_opcion_copiar);
        View btnEditar = dialog.findViewById(R.id.btn_opcion_editar);
        View btnEliminar = dialog.findViewById(R.id.btn_opcion_eliminar);
        View btnCancelar = dialog.findViewById(R.id.btn_opcion_cancelar);

        // Si el mensaje no es mío, ocultamos Editar y Eliminar
        if (!esMio) {
            btnEditar.setVisibility(View.GONE);
            btnEliminar.setVisibility(View.GONE);
        }

        btnCopiar.setOnClickListener(v -> {
            optionsListener.onCopiar(position);
            dialog.dismiss();
        });

        btnEditar.setOnClickListener(v -> {
            optionsListener.onEditar(position);
            dialog.dismiss();
        });

        btnEliminar.setOnClickListener(v -> {
            optionsListener.onEliminar(position);
            dialog.dismiss();
        });

        btnCancelar.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    @Override
    public int getItemCount() {
        return mensajes.size();
    }

    static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView tvUsuario, tvTexto, tvHora;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUsuario = itemView.findViewById(R.id.tv_usuario_chat);
            tvTexto = itemView.findViewById(R.id.tv_texto_mensaje);
            tvHora = itemView.findViewById(R.id.tv_hora_mensaje);
        }
    }
}
