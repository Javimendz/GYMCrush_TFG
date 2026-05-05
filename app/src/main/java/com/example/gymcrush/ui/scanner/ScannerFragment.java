package com.example.gymcrush.ui.scanner;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.gymcrush.R;
import com.example.gymcrush.data.remote.RetrofitClient;
import com.example.gymcrush.data.remote.dto.AccesoResponseDto;
import com.example.gymcrush.data.remote.dto.ApiResponseDto;
import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.CompoundBarcodeView;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScannerFragment extends Fragment {

    private CompoundBarcodeView barcodeScanner;
    private ProgressBar progressBar;
    private Button btnFlash;
    private boolean isFlashOn = false;
    private boolean isProcessing = false;

    private final BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            if (result.getText() != null && !isProcessing) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> procesarToken(result.getText()));
                }
            }
        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {
            // Requerido por la interfaz BarcodeCallback
        }
    };

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    barcodeScanner.resume();
                    barcodeScanner.decodeContinuous(callback);
                } else {
                    Toast.makeText(getContext(), "Permiso de cámara denegado", Toast.LENGTH_SHORT).show();
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scanner, container, false);

        barcodeScanner = view.findViewById(R.id.barcodeScanner);
        progressBar = view.findViewById(R.id.progressScanner);
        btnFlash = view.findViewById(R.id.btnToggleFlash);

        // Limpiar el texto por defecto de ZXing
        barcodeScanner.setStatusText("");

        btnFlash.setOnClickListener(v -> {
            if (isFlashOn) {
                barcodeScanner.setTorchOff();
                btnFlash.setText("Encender Linterna");
            } else {
                barcodeScanner.setTorchOn();
                btnFlash.setText("Apagar Linterna");
            }
            isFlashOn = !isFlashOn;
        });

        checkCameraPermission();

        return view;
    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private void procesarToken(String token) {
        isProcessing = true;
        progressBar.setVisibility(View.VISIBLE);

        RetrofitClient.getApiService().procesarQr(token).enqueue(new Callback<ApiResponseDto<AccesoResponseDto>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<AccesoResponseDto>> call, Response<ApiResponseDto<AccesoResponseDto>> response) {
                if (!isAdded()) return;
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponseDto<AccesoResponseDto> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        mostrarResultado(apiResponse.getDatos(), apiResponse.getMensaje());
                    } else {
                        mostrarError(apiResponse.getMensaje());
                    }
                } else {
                    String errorMsg = "Error del servidor: " + response.code();
                    try {
                        if (response.errorBody() != null) {
                            // Intentar extraer el mensaje de error del JSON si el servidor lo envía
                            String errorJson = response.errorBody().string();
                            errorMsg += "\n" + errorJson;
                        }
                    } catch (Exception e) {
                        // Ignorar fallos al leer el errorBody
                    }
                    mostrarError(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<ApiResponseDto<AccesoResponseDto>> call, Throwable t) {
                if (!isAdded()) return;
                progressBar.setVisibility(View.GONE);
                mostrarError("Error de conexión: " + t.getMessage());
            }
        });
    }

    private void mostrarResultado(AccesoResponseDto acceso, String mensajeServidor) {
        if (!isAdded()) return;

        String mensaje;
        if (acceso != null) {
            String username = acceso.getUsername() != null ? acceso.getUsername() : "Usuario";
            String tipo = acceso.getTipo() != null ? acceso.getTipo() : "N/A";
            String fecha = "ENTRADA".equals(tipo) ? acceso.getFechaHoraEntrada() : acceso.getFechaHoraSalida();
            if (fecha == null) fecha = "N/A";

            mensaje = "Usuario: " + username + "\n" +
                    "Tipo: " + tipo + "\n" +
                    "Fecha: " + fecha;
        } else {
            mensaje = mensajeServidor != null ? mensajeServidor : "Acceso procesado correctamente";
        }

        new AlertDialog.Builder(requireContext())
                .setTitle("Acceso Registrado")
                .setMessage(mensaje)
                .setPositiveButton("Aceptar", (dialog, which) -> isProcessing = false)
                .setCancelable(false)
                .show();
    }

    private void mostrarError(String error) {
        if (!isAdded()) return;
        new AlertDialog.Builder(requireContext())
                .setTitle("Error de Acceso")
                .setMessage(error)
                .setPositiveButton("Reintentar", (dialog, which) -> isProcessing = false)
                .setCancelable(false)
                .show();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            barcodeScanner.resume();
            barcodeScanner.decodeContinuous(callback);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        barcodeScanner.pause();
    }
}
