package com.example.gymcrush;

public class Config {
    // Si usas EMULADOR: 10.0.2.2
    // Si usas DISPOSITIVO FÍSICO: Pon la IP de tu PC (ej: 192.168.1.XX)
    // Asegúrate de que el servidor esté escuchando en 0.0.0.0 y no solo en localhost
    //public static final String IP_SERVIDOR = "https://gymcrush-tfg.onrender.com/api/v1/";
    //public static final String PUERTO = "8080";


    // 2. Para WebSockets en producción, se usa wss:// (Secure WebSocket)
    // Render gestiona esto por el puerto estándar (443), no el 8080.
    public static final String BASE_URL = "https://gymcrush-tfg.onrender.com/";
    public static final String WS_URL = "wss://gymcrush-tfg.onrender.com/ws/websocket";

    //public static final String BASE_URL = "http://" + IP_SERVIDOR + ":" + PUERTO + "/";
    //public static final String BASE_URL = "http://" + IP_SERVIDOR + ":" + PUERTO + "/";
    //public static final String BASE_URL = "http://" + IP_SERVIDOR + ":" + PUERTO + "/";
    //public static final String BASE_URL = "http://" + IP_SERVIDOR + ":" + PUERTO + "/";
   // public static final String WS_URL = "wss://gymcrush-tfg.onrender.com/ws/websocket";
}
