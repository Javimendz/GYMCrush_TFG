package com.example.gymcrush;

import android.app.Application;
import com.example.gymcrush.data.remote.RetrofitClient;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.MapsInitializer.Renderer;
import com.google.android.gms.maps.OnMapsSdkInitializedCallback;

public class GymCrushApp extends Application implements OnMapsSdkInitializedCallback {

    private static GymCrushApp instance;

    public static GymCrushApp getInstance() {
        return instance;
    }

    @Override
    protected void attachBaseContext(android.content.Context base) {
        super.attachBaseContext(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        
        try {
            // Inicializar Retrofit con el contexto de la aplicación
            RetrofitClient.init(this);
        } catch (Exception e) {
            // Log or handle initialization errors
        }
    }

    @Override
    public void onMapsSdkInitialized(MapsInitializer.Renderer renderer) {
        // Manejar inicialización de mapas si es necesario
    }
}
