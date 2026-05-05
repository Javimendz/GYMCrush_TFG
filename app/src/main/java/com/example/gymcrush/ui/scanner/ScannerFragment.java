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
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.CompoundBarcodeView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScannerFragment extends Fragment {

    private CompoundBarcodeView barcodeScanner;
    private ProgressBar progressBar;
    private Button btnFlash;
    private boolean isFlashOn = false;
    private boolean isProcessing = false;

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    barcodeScanner.resume();
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

        barcodeScanner.decodeContinuous(callback);

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

    private final BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            if (result.getText() != null && !isProcessing) {
                procesarToken(result.getText());
            }
        }
    };

    private void procesarToken(String token) {
        isProcessing = true;
        progressBar.setVisibility(View.VISIBLE);

        RetrofitClient.getApiService().procesarQr(token).enqueue(new Callback<ApiResponseDto<AccesoResponseDto>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<AccesoResponseDto>> call, Response<ApiResponseDto<AccesoResponseDto>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponseDto<AccesoResponseDto> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        mostrarResultado(apiResponse.getDatos());
                    } else {
                        mostrarError(apiResponse.getMensaje());
                    }
                } else {
                    mostrarError("Error al procesar el código");
                }
            }

            @Override
            public void onFailure(Call<ApiResponseDto<AccesoResponseDto>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                mostrarError("Error de conexión");
            }
        });
    }

    private void mostrarResultado(AccesoResponseDto acceso) {
        String mensaje = "Usuario: " + acceso.getUsername() + "\n" +
                "Tipo: " + acceso.getTipo() + "\n" +
                "Fecha: " + (acceso.getTipo().equals("ENTRADA") ? acceso.getFechaHoraEntrada() : acceso.getFechaHoraSalida());

        new AlertDialog.Builder(requireContext())
                .setTitle("Acceso Registrado")
                .setMessage(mensaje)
                .setPositiveButton("Aceptar", (dialog, which) -> isProcessing = false)
                .setCancelable(false)
                .show();
    }

    private void mostrarError(String error) {
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
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        barcodeScanner.pause();
    }
}
