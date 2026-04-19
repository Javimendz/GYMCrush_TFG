package com.example.gymcrush.ui.auth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;
import androidx.lifecycle.ViewModelProvider;

import com.example.gymcrush.R;
import com.example.gymcrush.ui.main.DesarrolloActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.HashSet;
import java.util.Set;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;
    private TextInputEditText etUsername, etPassword;
    private Button btnEntrar;
    private TextView tvIrARegistro;
    private MaterialButton btnGoogleLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Manejar la pantalla de carga (Splash Screen)
        SplashScreen.installSplashScreen(this);
        
        // Forzar el tema principal para asegurar la compatibilidad con AppCompatActivity
        setTheme(R.style.Theme_GYMCrush);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity_principal); 

        // 1. Inicializar Vistas
        initViews();

        // 2. Inicializar ViewModel
        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        // 3. Configurar Observadores
        setupObservers();

        // 4. Configurar Eventos de Clic
        setupListeners();
    }

    private void initViews() {
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnEntrar = findViewById(R.id.btnEntrar);
        tvIrARegistro = findViewById(R.id.tvIrARegistro);
        btnGoogleLogin = findViewById(R.id.btnGoogleLogin);
    }

    private void setupObservers() {
        loginViewModel.getLoginResult().observe(this, authResponseDto -> {
            if (authResponseDto != null) {
                SharedPreferences prefs = getSharedPreferences("DatosUsuario", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                
                String nombreAMostrar = authResponseDto.getNombre();
                if (nombreAMostrar == null || nombreAMostrar.isEmpty()) {
                    nombreAMostrar = authResponseDto.getUsername();
                }
                
                editor.putString("nombre_usuario", nombreAMostrar);
                editor.putString("auth_token", authResponseDto.getAccessToken());
                editor.putLong("user_id", authResponseDto.getId());
                
                // Guardar Roles
                if (authResponseDto.getRoles() != null) {
                    Set<String> rolesSet = new HashSet<>(authResponseDto.getRoles());
                    editor.putStringSet("roles", rolesSet);
                }

                editor.apply();

                Toast.makeText(this, "¡Bienvenido, " + nombreAMostrar + "!", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(LoginActivity.this, DesarrolloActivity.class);
                startActivity(intent);
                finish();
            }
        });

        loginViewModel.getError().observe(this, mensajeError -> {
            if (mensajeError != null) {
                Toast.makeText(this, mensajeError, Toast.LENGTH_LONG).show();
                btnEntrar.setEnabled(true); 
            }
        });

        loginViewModel.getIsLoading().observe(this, isLoading -> {
            if (isLoading) {
                btnEntrar.setEnabled(false);
                btnEntrar.setText("Cargando...");
            } else {
                btnEntrar.setEnabled(true);
                btnEntrar.setText("Entrar");
            }
        });
    }

    private void setupListeners() {
        btnEntrar.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            loginViewModel.login(username, password);
        });

        tvIrARegistro.setOnClickListener(v -> {
             Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
             startActivity(intent);
        });

        btnGoogleLogin.setOnClickListener(v -> {
            Toast.makeText(this, "Próximamente: Inicio con Google", Toast.LENGTH_SHORT).show();
        });
    }
}