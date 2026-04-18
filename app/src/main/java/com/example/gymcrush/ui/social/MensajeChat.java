package com.example.gymcrush.ui.social;

public class MensajeChat {
    private String usuario;
    private String texto;
    private String hora;

    public MensajeChat(String usuario, String texto, String hora) {
        this.usuario = usuario;
        this.texto = texto;
        this.hora = hora;
    }

    public String getUsuario() { return usuario; }
    public String getTexto() { return texto; }
    public String getHora() { return hora; }
}
