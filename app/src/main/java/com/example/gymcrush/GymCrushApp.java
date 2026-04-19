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
        
        // Initialize components only after super.onCreate()
        try {
            // Inicializar Retrofit con el contexto de la aplicación
            RetrofitClient.init(this);

            // Initialize Google Maps SDK safely
            initMaps();
        } catch (Exception e) {
            // Log or handle initialization errors to prevent app crash
        }
    }

    private void initMaps() {
        try {
            GoogleApiAvailability availability = GoogleApiAvailability.getInstance();
            int resultCode = availability.isGooglePlayServicesAvailable(this);
            
            if (resultCode == ConnectionResult.SUCCESS) {
                // Use LATEST renderer for better compatibility with newer GMS versions
                // and fall back to LEGACY if needed.
                MapsInitializer.initialize(this, Renderer.LATEST, this);
            }
        } catch (Exception e) {
            // Fallback if initialization fails
        }
    }

    @Override
    public void onMapsSdkInitialized(MapsInitializer.Renderer renderer) {
        // Manejar inicialización de mapas si es necesario
    }
}
