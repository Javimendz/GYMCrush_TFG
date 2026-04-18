package com.example.gymcrush;

import android.app.Application;
import com.example.gymcrush.data.remote.RetrofitClient;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.MapsInitializer.Renderer;
import com.google.android.gms.maps.OnMapsSdkInitializedCallback;

public class GymCrushApp extends Application implements OnMapsSdkInitializedCallback {

    private static GymCrushApp instance;

    public static GymCrushApp getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        
        // Inicializar Retrofit con el contexto de la aplicación
        RetrofitClient.init(this);

        // Initialize Google Maps SDK with the latest renderer
        MapsInitializer.initialize(this, Renderer.LATEST, this);
    }

    @Override
    public void onMapsSdkInitialized(MapsInitializer.Renderer renderer) {
        // Manejar inicialización de mapas si es necesario
    }
}
