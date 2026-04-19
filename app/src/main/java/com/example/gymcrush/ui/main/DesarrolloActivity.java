package com.example.gymcrush.ui.main;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.example.gymcrush.R;
import com.example.gymcrush.data.remote.ApiService;
import com.example.gymcrush.data.remote.RetrofitClient;
import com.example.gymcrush.data.remote.dto.ApiResponseDto;
import com.example.gymcrush.data.remote.dto.PerfilResponseDto;
import com.example.gymcrush.ui.auth.LoginActivity;
import com.example.gymcrush.ui.clases.ClasesFragment;
import com.example.gymcrush.ui.ejercicios.EjerciciosFragment;
import com.example.gymcrush.ui.rutinas.RutinasFragment;
import com.example.gymcrush.ui.perfil.PerfilFragment;
import com.example.gymcrush.ui.contacto.ContactoFragment;
import com.example.gymcrush.ui.reservas.ReservasFragment;
import com.example.gymcrush.ui.servicios.ContratacionesFragment;
import com.example.gymcrush.ui.equipo.EquipoFragment;
import com.example.gymcrush.ui.servicios.SuplementacionFragment;
import com.example.gymcrush.ui.dieta.DietaFragment;
import com.example.gymcrush.ui.social.SocialFragment;
import com.example.gymcrush.ui.notificaciones.NotificacionesFragment;
import com.example.gymcrush.ui.salud.SaludFragment;
import com.example.gymcrush.ui.actividades.ActividadesFragment;
import com.example.gymcrush.ui.staff.StaffFragment;
import com.example.gymcrush.ui.view.EspacioFragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DesarrolloActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap map;
    private DrawerLayout drawer;
    private View homeContent;
    private BottomNavigationView bottomNav;
    private ImageView profileImage;
    private ImageView ivHeaderPhoto;
    
    // Widget Nutrición
    private View cardNutricionWidget;
    private TextView tvNombrePlanWidget, tvCaloriasWidget;
    private com.google.android.material.progressindicator.LinearProgressIndicator progressProt, progressCarb, progressGras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_desarrollo_prueba);

        // 1. Configurar Fecha Actual
        TextView tvFecha = findViewById(R.id.tvFecha);
        Date calendario = Calendar.getInstance().getTime();
        SimpleDateFormat formato = new SimpleDateFormat("EEEE, d MMMM", new Locale("es", "ES"));
        String fechaFormateada = formato.format(calendario);
        tvFecha.setText(fechaFormateada.substring(0, 1).toUpperCase() + fechaFormateada.substring(1));

        // 2. Configurar Saludo e Imagen con SharedPreferences
        profileImage = findViewById(R.id.profile_image);
        
        // Widget Nutrición initialization
        cardNutricionWidget = findViewById(R.id.cardNutricionWidget);
        tvNombrePlanWidget = findViewById(R.id.tvNombrePlanWidget);
        tvCaloriasWidget = findViewById(R.id.tvCaloriasWidget);
        progressProt = findViewById(R.id.progressProt);
        progressCarb = findViewById(R.id.progressCarb);
        progressGras = findViewById(R.id.progressGras);
        
        cardNutricionWidget.setOnClickListener(v -> cargarFragmento(new DietaFragment()));

        // 3. Configurar Menú Lateral (Drawer)
        ImageView btnMenu = findViewById(R.id.btnMenuLateral);
        drawer = findViewById(R.id.drawer_layout_root);
        btnMenu.setOnClickListener(v -> drawer.openDrawer(GravityCompat.START));

        NavigationView navigationView = findViewById(R.id.navigation_view);
        View headerView = navigationView.getHeaderView(0);
        ivHeaderPhoto = headerView.findViewById(R.id.img_logo_header);
        
        cargarDatosYFotoPerfil();

        // Configurar roles y listener del menú
        SharedPreferences prefs = getSharedPreferences("DatosUsuario", MODE_PRIVATE);
        java.util.Set<String> roles = prefs.getStringSet("roles", new java.util.HashSet<>());
        boolean isAdmin = roles.contains("ROLE_ADMIN") || roles.contains("ADMIN");
        
        if (isAdmin) {
            navigationView.getMenu().findItem(R.id.nav_staff).setVisible(true);
        }

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_salir) {
                mostrarDialogoCerrarSesion();
            } else if (id == R.id.nav_qr) {
                mostrarDialogoQR();
            } else if (id == R.id.nav_espacio) {
                cargarFragmento(new EspacioFragment());
            } else if (id == R.id.nav_reservas) {
                cargarFragmento(new ReservasFragment());
            } else if (id == R.id.nav_salud) {
                cargarFragmento(new SaludFragment());
            } else if (id == R.id.nav_rutinas_drawer) {
                cargarFragmento(new RutinasFragment());
            } else if (id == R.id.nav_contrataciones) {
                cargarFragmento(new ContratacionesFragment());
            } else if (id == R.id.nav_equipo) {
                cargarFragmento(new EquipoFragment());
            } else if (id == R.id.nav_contacto) {
                cargarFragmento(new ContactoFragment());
            } else if (id == R.id.nav_sumplementacion) {
                cargarFragmento(new SuplementacionFragment());
            } else if (id == R.id.nav_historial_dieta) {
                cargarFragmento(new DietaFragment());
            } else if (id == R.id.nav_clases) {
                cargarFragmento(new ClasesFragment());
            } else if (id == R.id.nav_staff) {
                cargarFragmento(new StaffFragment());
            }
            drawer.closeDrawer(GravityCompat.START);
            return true;
        });

        // 4. Configurar Bottom Navigation
        homeContent = findViewById(R.id.home_content);
        bottomNav = findViewById(R.id.botton_nav);

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                mostrarHome();
                return true;
            } else if (id == R.id.nav_workout) {
                cargarFragmento(new EjerciciosFragment());
                return true;
            } else if (id == R.id.nav_social) {
                cargarFragmento(new SocialFragment());
                return true;
            } else if (id == R.id.nav_clases) {
                cargarFragmento(new ClasesFragment());
                return true;
            } else if (id == R.id.nav_perfil) {
                cargarFragmento(new PerfilFragment());
                return true;
            }
            return false;
        });

        // 5. Google Maps
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) mapFragment.getMapAsync(this);

        getSupportFragmentManager().addOnBackStackChangedListener(() -> {
            if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                homeContent.setVisibility(View.VISIBLE);
                cargarDatosYFotoPerfil(); // Recargar datos al volver al home
            }
        });
    }

    private void cargarDatosYFotoPerfil() {
        SharedPreferences prefs = getSharedPreferences("DatosUsuario", MODE_PRIVATE);
        long userId = prefs.getLong("user_id", -1);
        String nombreLocal = prefs.getString("nombre_usuario", "Usuario");

        // Mostrar nombre local mientras carga del servidor
        TextView tvSaludo = findViewById(R.id.tvSaludo);
        if (tvSaludo != null) tvSaludo.setText("¡HOLA, " + nombreLocal.toUpperCase() + "!");
        
        NavigationView navigationView = findViewById(R.id.navigation_view);
        View headerView = navigationView.getHeaderView(0);
        TextView tvNombreHeader = headerView.findViewById(R.id.tv_nombre_header);
        if (tvNombreHeader != null) tvNombreHeader.setText(nombreLocal);

        if (userId == -1) return;

        ApiService apiService = RetrofitClient.getApiService();
        
        // Cargar Plan Nutricional para el Widget
        apiService.obtenerUltimoPlan(userId).enqueue(new Callback<ApiResponseDto<com.example.gymcrush.data.remote.dto.NutricionResponseDto>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<com.example.gymcrush.data.remote.dto.NutricionResponseDto>> call, Response<ApiResponseDto<com.example.gymcrush.data.remote.dto.NutricionResponseDto>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getDatos() != null) {
                    com.example.gymcrush.data.remote.dto.NutricionResponseDto plan = response.body().getDatos();
                    cardNutricionWidget.setVisibility(View.VISIBLE);
                    tvNombrePlanWidget.setText(plan.getNombreDieta());
                    tvCaloriasWidget.setText(plan.getCaloriasObjetivo() + " kcal");
                    
                    if (plan.getDietas() != null) {
                        // Simulación de progreso basada en el objetivo
                        progressProt.setProgress(75); 
                        progressCarb.setProgress(40);
                        progressGras.setProgress(60);
                    }
                } else {
                    // En lugar de ocultar, mostramos estado por defecto para todos
                    tvNombrePlanWidget.setText("Sin plan activo");
                    tvCaloriasWidget.setText("Consulta con tu nutricionista");
                    progressProt.setProgress(0);
                    progressCarb.setProgress(0);
                    progressGras.setProgress(0);
                }
            }

            @Override
            public void onFailure(Call<ApiResponseDto<com.example.gymcrush.data.remote.dto.NutricionResponseDto>> call, Throwable t) {
                // En caso de error, también lo dejamos visible con info por defecto
                tvNombrePlanWidget.setText("Plan no disponible");
                tvCaloriasWidget.setText("Revisa tu conexión");
            }
        });

        apiService.getPerfilById(userId).enqueue(new Callback<ApiResponseDto<PerfilResponseDto>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<PerfilResponseDto>> call, Response<ApiResponseDto<PerfilResponseDto>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getDatos() != null) {
                    PerfilResponseDto perfil = response.body().getDatos();
                    String fotoBase64 = perfil.getFoto();
                    String nombreReal = perfil.getNombre();

                    if (nombreReal != null && !nombreReal.isEmpty()) {
                        if (tvSaludo != null) tvSaludo.setText("¡HOLA, " + nombreReal.toUpperCase() + "!");
                        if (tvNombreHeader != null) tvNombreHeader.setText(nombreReal);
                    }

                    if (fotoBase64 != null && !fotoBase64.isEmpty()) {
                        byte[] decodedString = Base64.decode(fotoBase64, Base64.DEFAULT);
                        
                        // Cargar en imagen principal
                        if (profileImage != null) {
                            Glide.with(DesarrolloActivity.this)
                                    .asBitmap()
                                    .load(decodedString)
                                    .circleCrop()
                                    .placeholder(R.drawable.logo)
                                    .into(profileImage);
                        }
                        
                        // Cargar en imagen del header
                        if (ivHeaderPhoto != null) {
                            Glide.with(DesarrolloActivity.this)
                                    .asBitmap()
                                    .load(decodedString)
                                    .circleCrop()
                                    .placeholder(R.drawable.logo)
                                    .into(ivHeaderPhoto);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponseDto<PerfilResponseDto>> call, Throwable t) {
                // Si falla la red, ya tenemos los datos básicos de SharedPreferences cargados por defecto
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        cargarDatosYFotoPerfil();
    }

    private void cargarFragmento(Fragment fragment) {
        homeContent.setVisibility(View.GONE);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void mostrarHome() {
        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        homeContent.setVisibility(View.VISIBLE);
    }

    private void mostrarDialogoQR() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_qr_gym);
        if (dialog.getWindow() != null) dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        SharedPreferences prefs = getSharedPreferences("DatosUsuario", MODE_PRIVATE);
        String username = prefs.getString("nombre_usuario", "Usuario");
        String userId = String.valueOf(prefs.getLong("user_id", -1));
        String qrContent = "GYMCRUSH_" + username + "_" + userId + "_" + UUID.randomUUID().toString();

        ImageView ivQr = dialog.findViewById(R.id.ivQrCode);
        try {
            BitMatrix bitMatrix = new MultiFormatWriter().encode(qrContent, BarcodeFormat.QR_CODE, 500, 500);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            ivQr.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }

        View btnCerrar = dialog.findViewById(R.id.btnCerrarQr);
        if (btnCerrar != null) {
            btnCerrar.setOnClickListener(v -> dialog.dismiss());
        }

        dialog.show();
    }

    private void mostrarDialogoCerrarSesion() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_custom_exit);
        if (dialog.getWindow() != null) dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog.findViewById(R.id.btn_cancelar_exit).setOnClickListener(v -> dialog.dismiss());
        dialog.findViewById(R.id.btn_confirmar_exit).setOnClickListener(v -> {
            SharedPreferences prefs = getSharedPreferences("DatosUsuario", MODE_PRIVATE);
            prefs.edit().clear().apply();
            
            Intent intent = new Intent(DesarrolloActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        dialog.show();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
        LatLng gymLocation = new LatLng(39.4699, -0.3763); // Valencia centro (ejemplo)
        map.addMarker(new MarkerOptions().position(gymLocation).title("GYMCrush Valencia"));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(gymLocation, 15f));
    }
}
