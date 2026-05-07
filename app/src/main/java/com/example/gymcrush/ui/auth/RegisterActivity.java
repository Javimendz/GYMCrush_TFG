package com.example.gymcrush.ui.auth;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gymcrush.R;
import com.example.gymcrush.data.repository.AuthRepository;
import com.example.gymcrush.data.remote.dto.UsuarioResponseDto;
import com.example.gymcrush.data.remote.dto.RegisterRequest;

import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;

public class RegisterActivity extends AppCompatActivity {

    private EditText etNombre, etApellidos, etUsername, etEmail, etPassword, etFechaNacimiento,
            etCiudad, etCodigoPostal, etTelefono, etDireccion, etPais, etDni;
    private AutoCompleteTextView spinnerGenero;
    private AuthRepository authRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity_registro);

        // Inicializar vistas
        etNombre = findViewById(R.id.etNombre);
        etApellidos = findViewById(R.id.etApellidos);
        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmailRegistro);
        etPassword = findViewById(R.id.etPasswordRegistro);
        etFechaNacimiento = findViewById(R.id.etFechaNacimiento);
        spinnerGenero = findViewById(R.id.spinnerGenero);
        etCiudad = findViewById(R.id.etCiudad);
        etCodigoPostal = findViewById(R.id.etCodigoPostal);
        etTelefono = findViewById(R.id.etTelefono);

        etDireccion = findViewById(R.id.etDireccion);
        etPais = findViewById(R.id.etPais);
        etDni = findViewById(R.id.etDni);

        authRepository = new AuthRepository();

        // Configurar Selector de Fecha
        etFechaNacimiento.setOnClickListener(v -> showDatePicker());

        // Configurar Selector de Género
        String[] generos = {"Hombre", "Mujer"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, generos);
        spinnerGenero.setAdapter(adapter);

        // Botón Registrar
        findViewById(R.id.btnRegistrar).setOnClickListener(v -> validarYRegistrar());

        // Botón Volver
        findViewById(R.id.tvVolverLogin).setOnClickListener(v -> finish());
    }

    private void showDatePicker() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR) - 18;
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year1, monthOfYear, dayOfMonth) -> {
            String date = String.format(Locale.getDefault(), "%04d-%02d-%02d", year1, monthOfYear + 1, dayOfMonth);
            etFechaNacimiento.setText(date);
        }, year, month, day);
        datePickerDialog.show();
    }

    private void validarYRegistrar() {
        String nombre = etNombre.getText().toString().trim();
        String apellidos = etApellidos.getText().toString().trim();
        String username = etUsername.getText().toString().trim();
        String correo = etEmail.getText().toString().trim();
        String pass = etPassword.getText().toString().trim();
        String fecha = etFechaNacimiento.getText().toString().trim();
        String genero = spinnerGenero.getText().toString().trim();
        String ciudad = etCiudad.getText().toString().trim();
        String codigoPostal = etCodigoPostal.getText().toString().trim();
        String telefono = etTelefono.getText().toString().trim();
        String direccion = etDireccion.getText().toString().trim();
        String pais = etPais.getText().toString().trim();

        // El DNI siempre en mayúsculas para que coincida con la validación del Backend
        String dni = etDni.getText().toString().trim().toUpperCase();

        //  Validar campos vacíos
        if (nombre.isEmpty() || apellidos.isEmpty() || username.isEmpty() || correo.isEmpty() ||
                pass.isEmpty() || fecha.isEmpty() || genero.isEmpty() || ciudad.isEmpty() ||
                codigoPostal.isEmpty() || telefono.isEmpty() || direccion.isEmpty() || pais.isEmpty() || dni.isEmpty()) {
            Toast.makeText(this, "Todos los campos marcados son obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        //  Validar Username
        if (username.length() < 3 || username.length() > 20) {
            etUsername.setError("El usuario debe tener entre 3 y 20 caracteres");
            etUsername.requestFocus();
            return;
        }

        //  Validar Correo Electrónico (Coincide con @Email)
        if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            etEmail.setError("Formato de correo inválido");
            etEmail.requestFocus();
            return;
        }

        //  Validar Contraseña

        if (pass.length() < 8) {
            etPassword.setError("La contraseña debe tener al menos 8 caracteres");
            etPassword.requestFocus();
            return;
        }

        // Validar Teléfono
        if (!telefono.matches("^[0-9]{9}$")) {
            etTelefono.setError("El teléfono debe tener exactamente 9 dígitos");
            etTelefono.requestFocus();
            return;
        }

        //  Validar DNI
        if (!dni.matches("^[0-9]{8}[TRWAGMYFPDXBNJZSQVHLCKE]$")) {
            etDni.setError("Formato de DNI inválido (8 números y 1 letra)");
            etDni.requestFocus();
            return;
        }

        //  Validar Código Postal
        if (!codigoPostal.matches("^[0-9]{5}$")) {
            etCodigoPostal.setError("El código postal debe tener 5 dígitos");
            etCodigoPostal.requestFocus();
            return;
        }

        // Si pasa todas las validaciones, creamos la petición
        RegisterRequest request = new RegisterRequest();
        request.setNombre(nombre);
        request.setApellidos(apellidos);
        request.setUsername(username);
        request.setEmail(correo);
        request.setPassword(pass);
        request.setFechaNacimiento(fecha);
        request.setGenero(genero);
        request.setRoles(new HashSet<>(Collections.singletonList("ROLE_USUARIO")));
        request.setCiudad(ciudad);
        request.setCodigoPostal(codigoPostal);
        request.setTelefono(telefono);
        request.setDireccion(direccion);
        request.setPais(pais);
        request.setDni(dni);

        authRepository.register(request, new AuthRepository.RegisterCallback() {
            @Override
            public void onSuccess(UsuarioResponseDto usuario) {
                runOnUiThread(() -> {
                    Toast.makeText(RegisterActivity.this, "¡Registro con éxito! Identifícate", Toast.LENGTH_LONG).show();
                    finish();
                });
            }

            @Override
            public void onError(String errorMessage) {
                runOnUiThread(() -> {
                    String mensaje = (errorMessage != null && !errorMessage.isEmpty()) ? errorMessage : "Error desconocido";
                    Toast.makeText(RegisterActivity.this, mensaje, Toast.LENGTH_LONG).show();
                });
            }
        });
    }
}