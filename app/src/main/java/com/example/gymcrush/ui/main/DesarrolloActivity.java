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
import com.example.gymcrush.data.remote.dto.ExerciseApiRequestDto;
import com.example.gymcrush.data.remote.dto.FatSecretRecipeDto;
import com.example.gymcrush.data.remote.dto.FatSecretResponseDto;
import com.example.gymcrush.data.remote.dto.PerfilResponseDto;
import com.example.gymcrush.ui.ejercicios.ReproductorFragment;
import com.google.gson.Gson;
import com.example.gymcrush.ui.auth.LoginActivity;
import com.example.gymcrush.ui.clases.ClasesFragment;
import com.example.gymcrush.ui.ejercicios.EjerciciosFragment;
import com.example.gymcrush.ui.rutinas.PlanesFragment;
import com.example.gymcrush.ui.rutinas.RutinasFragment;
import com.example.gymcrush.ui.perfil.PerfilFragment;
import com.example.gymcrush.ui.contacto.ContactoFragment;
import com.example.gymcrush.ui.reservas.ReservasFragment;
import com.example.gymcrush.ui.servicios.ContratacionesFragment;
import com.example.gymcrush.ui.equipo.EquipoFragment;
import com.example.gymcrush.ui.servicios.SuplementacionFragment;
import com.example.gymcrush.ui.dieta.DietaFragment;
import com.yarolegovich.slidingrootnav.SlidingRootNav;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;
import com.example.gymcrush.ui.social.SocialFragment;
import com.example.gymcrush.ui.notificaciones.NotificacionesFragment;
import com.example.gymcrush.ui.salud.SaludFragment;
import com.example.gymcrush.ui.actividades.ActividadesFragment;
import com.example.gymcrush.ui.staff.StaffFragment;
import java.util.Collections;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
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
    private TextView tvIANewsTitle, tvIANewsContent;
    private com.google.android.material.progressindicator.LinearProgressIndicator progressProt, progressCarb, progressGras;
    private SlidingRootNav slidingRootNav;

    // Carrusel
    private androidx.viewpager2.widget.ViewPager2 viewPagerCarousel, viewPagerExerciseCarousel;
    private CarouselAdapter carouselAdapter, exerciseCarouselAdapter;
    private List<CarouselAdapter.CarouselItem> carouselItemsList = new ArrayList<>();
    private List<CarouselAdapter.CarouselItem> exerciseCarouselItemsList = new ArrayList<>();

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
        tvIANewsTitle = findViewById(R.id.tvIANewsTitle);
        tvIANewsContent = findViewById(R.id.tvIANewsContent);
        progressProt = findViewById(R.id.progressProt);
        progressCarb = findViewById(R.id.progressCarb);
        progressGras = findViewById(R.id.progressGras);
        
        cardNutricionWidget.setOnClickListener(v -> cargarFragmento(new DietaFragment()));

        // 3. Configurar Menú Lateral Moderno con SlidingRootNav
        ImageView btnMenu = findViewById(R.id.btnMenuLateral);
        
        slidingRootNav = new SlidingRootNavBuilder(this)
                .withMenuLayout(R.layout.menu_left_drawer) // Crearemos este layout
                .withMenuLocked(false)
                .withMenuOpened(false)
                .withContentClickableWhenMenuOpened(false)
                .withSavedState(savedInstanceState)
                .inject();

        btnMenu.setOnClickListener(v -> {
            if (slidingRootNav.isMenuOpened()) {
                slidingRootNav.closeMenu();
            } else {
                slidingRootNav.openMenu();
            }
        });

        // Configurar clics y datos en el nuevo menú
        setupModernMenu();

        NavigationView navigationView = findViewById(R.id.navigation_view);
        // Ocultamos el NavigationView viejo pero lo dejamos por si acaso o lo borramos después
        navigationView.setVisibility(View.GONE);
        
        cargarDatosYFotoPerfil();

        // Configurar roles y listener del menú
        SharedPreferences prefs = getSharedPreferences("DatosUsuario", MODE_PRIVATE);
        Set<String> roles = prefs.getStringSet("roles", new HashSet<>());
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
            } else if (id == R.id.nav_planes_entrenamiento) {
                cargarFragmento(new PlanesFragment());
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
            } else if (id == R.id.nav_perfil) {
                cargarFragmento(new PerfilFragment());
                return true;
            }
            return false;
        });

        // 5. Google Maps
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        LottieAnimationView notificationsAnim = findViewById(R.id.btnNotificationsAnim);
        notificationsAnim.setOnClickListener(v -> {
            notificationsAnim.playAnimation();
            cargarFragmento(new NotificacionesFragment());
        });

        // Configurar el carrusel
        setupCarousel();
        setupExerciseCarousel();
        cargarNoticiasIA();

        // Listener para manejar la visibilidad del Home al volver de fragmentos
        getSupportFragmentManager().addOnBackStackChangedListener(() -> {
            if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                homeContent.setVisibility(View.VISIBLE);
                bottomNav.setSelectedItemId(R.id.nav_home);
            }
        });
    }

    private void cargarNoticiasIA() {
        // En una app real, aquí llamarías a un endpoint que use GPT o similar.
        // Simulamos la respuesta de la IA basada en el contexto del usuario.
        new android.os.Handler().postDelayed(() -> {
            if (isFinishing()) return;

            String[] titulos = {
                    "Optimización de Hipertrofia",
                    "Tendencias en Biohacking",
                    "Recuperación Inteligente",
                    "IA en el Levantamiento"
            };
            
            String[] noticias = {
                    "Nuevos estudios sugieren que la fase excéntrica lenta (3s) incrementa la síntesis proteica un 15% más que el ritmo convencional.",
                    "El uso de luz roja tras entrenamientos de alta intensidad podría reducir el DOMS en un 20%. ¡Pruébalo esta semana!",
                    "Tu ritmo de entrenamiento indica que hoy es un día óptimo para series de aproximación pesadas. Enfócate en la técnica del peso muerto.",
                    "La inteligencia artificial predice que tu mejor rendimiento hoy será en la ventana de las 18:00. Hidrátate bien."
            };

            int random = new java.util.Random().nextInt(titulos.length);
            tvIANewsTitle.setText(titulos[random]);
            tvIANewsContent.setText(noticias[random]);
            
            // Animación suave de aparición
            findViewById(R.id.cardIANews).setAlpha(0);
            findViewById(R.id.cardIANews).animate().alpha(1).setDuration(800).start();

        }, 2000);
    }

    private void setupCarousel() {
        viewPagerCarousel = findViewById(R.id.viewPagerCarousel);
        
        // 1. Inicializar con datos de carga para que no se vea vacío
        carouselItemsList.clear();
        carouselItemsList.add(new CarouselAdapter.CarouselItem(
                "CONSEJO DEL DÍA", 
                "Cargando sugerencias...", 
                "Estamos preparando las mejores recetas para ti", 
                R.drawable.ic_dieta, 
                R.drawable.bg_login_modern, 
                -1
        ));
        
        carouselAdapter = new CarouselAdapter(carouselItemsList);
        viewPagerCarousel.setAdapter(carouselAdapter);

        // 2. Configuración visual de carrusel (Tarjetas laterales visibles)
        viewPagerCarousel.setOffscreenPageLimit(3);
        viewPagerCarousel.setClipToPadding(false);
        viewPagerCarousel.setClipChildren(false);
        
        // Añadir espacio entre tarjetas y efecto de escala
        float nextItemVisiblePx = getResources().getDimension(R.dimen.viewpager_next_item_visible);
        float currentItemHorizontalMarginPx = getResources().getDimension(R.dimen.viewpager_current_item_horizontal_margin);
        float pageTranslationX = nextItemVisiblePx + currentItemHorizontalMarginPx;
        
        androidx.viewpager2.widget.CompositePageTransformer compositePageTransformer = new androidx.viewpager2.widget.CompositePageTransformer();
        compositePageTransformer.addTransformer(new androidx.viewpager2.widget.MarginPageTransformer(10));
        compositePageTransformer.addTransformer((page, position) -> {
            float r = 1 - Math.abs(position);
            page.setScaleY(0.95f + r * 0.05f);
        });
        viewPagerCarousel.setPageTransformer(compositePageTransformer);

        cargarDatosDinamicosCarrusel();
    }

    private void setupExerciseCarousel() {
        viewPagerExerciseCarousel = findViewById(R.id.viewPagerExerciseCarousel);
        
        exerciseCarouselItemsList.add(new CarouselAdapter.CarouselItem(
                "ENTRENAMIENTO", 
                "Cargando ejercicios...", 
                "Buscando las mejores rutinas para ti", 
                R.drawable.ic_workout,
                R.drawable.bg_login_modern, 
                -1
        ));
        
        exerciseCarouselAdapter = new CarouselAdapter(exerciseCarouselItemsList);
        exerciseCarouselAdapter.setOnItemClickListener(item -> {
            cargarFragmento(ReproductorFragment.newInstance(
                    item.getId(),
                    item.getImageUrl(),
                    item.getTitle(),
                    item.getFullDescription() != null ? item.getFullDescription() : item.getDescription()
            ));
        });
        viewPagerExerciseCarousel.setAdapter(exerciseCarouselAdapter);

        viewPagerExerciseCarousel.setOffscreenPageLimit(3);
        viewPagerExerciseCarousel.setClipToPadding(false);
        viewPagerExerciseCarousel.setClipChildren(false);

        androidx.viewpager2.widget.CompositePageTransformer compositePageTransformer = new androidx.viewpager2.widget.CompositePageTransformer();
        compositePageTransformer.addTransformer(new androidx.viewpager2.widget.MarginPageTransformer(10));
        compositePageTransformer.addTransformer((page, position) -> {
            float r = 1 - Math.abs(position);
            page.setScaleY(0.95f + r * 0.05f);
        });
        viewPagerExerciseCarousel.setPageTransformer(compositePageTransformer);

        cargarEjerciciosRecomendados();
    }

    private void cargarEjerciciosRecomendados() {
        ApiService apiService = RetrofitClient.getApiService();
        apiService.buscarEnBiblioteca("chest").enqueue(new Callback<ApiResponseDto<List<ExerciseApiRequestDto>>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<List<ExerciseApiRequestDto>>> call, Response<ApiResponseDto<List<ExerciseApiRequestDto>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getDatos() != null && !response.body().getDatos().isEmpty()) {
                    List<ExerciseApiRequestDto> exercises = response.body().getDatos();
                    exerciseCarouselItemsList.clear();
                    
                    for (int i = 0; i < Math.min(exercises.size(), 8); i++) {
                        ExerciseApiRequestDto ex = exercises.get(i);
                        long id = 0;
                        try {
                            id = Long.parseLong(ex.getId());
                        } catch (Exception e) {
                            id = Math.abs(ex.getId().hashCode());
                        }

                        String detail = "Equipo: " + ex.getEquipment() + " | " + ex.getTarget();
                        if (ex.getInstructions() != null && !ex.getInstructions().isEmpty()) {
                            StringBuilder sb = new StringBuilder();
                            for (String s : ex.getInstructions()) {
                                sb.append(s).append(" ");
                            }
                            detail = sb.toString().trim();
                        }

                        exerciseCarouselItemsList.add(new CarouselAdapter.CarouselItem(
                                ex.getBodyPart().toUpperCase(),
                                ex.getName(),
                                "Equipo: " + ex.getEquipment() + " | " + ex.getTarget(),
                                ex.getGifUrl(),
                                null, // URL de receta no aplica
                                R.drawable.ic_workout,
                                R.drawable.bg_login_modern,
                                100,
                                id,
                                detail
                        ));
                    }
                    runOnUiThread(() -> {
                        if (exerciseCarouselAdapter != null) {
                            exerciseCarouselAdapter.updateItems(new ArrayList<>(exerciseCarouselItemsList));
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<ApiResponseDto<List<ExerciseApiRequestDto>>> call, Throwable t) {
                // Fallback silencioso
            }
        });
    }

    private void cargarDatosDinamicosCarrusel() {
        ApiService apiService = RetrofitClient.getApiService();
        
        // Cargar Recetas de FatSecret para el Carrusel
        apiService.buscarAlimentos("diet").enqueue(new Callback<ApiResponseDto<String>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<String>> call, Response<ApiResponseDto<String>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getDatos() != null) {
                    try {
                        String jsonString = response.body().getDatos();
                        FatSecretResponseDto fatSecretData = new Gson().fromJson(jsonString, FatSecretResponseDto.class);
                        
                        if (fatSecretData != null && fatSecretData.getRecipes() != null && fatSecretData.getRecipes().getRecipe() != null) {
                            carouselItemsList.clear();
                            List<FatSecretRecipeDto> recipes = fatSecretData.getRecipes().getRecipe();
                            
                            // Ordenar: primero las que tienen imagen, luego las que no
                            Collections.sort(recipes, (r1, r2) -> {
                                boolean hasImg1 = r1.getRecipeImage() != null && !r1.getRecipeImage().isEmpty();
                                boolean hasImg2 = r2.getRecipeImage() != null && !r2.getRecipeImage().isEmpty();
                                if (hasImg1 && !hasImg2) return -1;
                                if (!hasImg1 && hasImg2) return 1;
                                return 0;
                            });

                            for (int i = 0; i < Math.min(recipes.size(), 8); i++) {
                                FatSecretRecipeDto recipe = recipes.get(i);
                                String macros = "";
                                if (recipe.getRecipeNutrition() != null) {
                                    macros = recipe.getRecipeNutrition().getCalories() + " kcal | P:" + 
                                             recipe.getRecipeNutrition().getProtein() + "g C:" + 
                                             recipe.getRecipeNutrition().getCarbohydrate() + "g";
                                }

                                carouselItemsList.add(new CarouselAdapter.CarouselItem(
                                        "RECETA SALUDABLE",
                                        recipe.getRecipeName(),
                                        macros.isEmpty() ? recipe.getRecipeDescription() : macros,
                                        recipe.getRecipeImage(), // Usamos la URL tal cual viene
                                        recipe.getRecipeUrl(),
                                        R.drawable.ic_dieta,
                                        R.drawable.bg_login_modern,
                                        100
                                ));
                            }
                            actualizarAdaptador(carouselItemsList);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponseDto<String>> call, Throwable t) {
                runOnUiThread(() -> {
                    carouselItemsList.clear();
                    carouselItemsList.add(new CarouselAdapter.CarouselItem(
                            "SIN CONEXIÓN",
                            "Recetas no disponibles",
                            "No se pudo conectar con el servidor de nutrición",
                            R.drawable.ic_dieta,
                            R.drawable.bg_login_modern,
                            0
                    ));
                    actualizarAdaptador(carouselItemsList);
                });
            }
        });

        // Actualizar el Widget de Nutrición (mantenemos la lógica de tu plan)
        SharedPreferences prefs = getSharedPreferences("DatosUsuario", MODE_PRIVATE);
        long usuarioId = prefs.getLong("usuario_id", -1);
        if (usuarioId != -1) {
            apiService.obtenerUltimoPlan(usuarioId).enqueue(new Callback<ApiResponseDto<com.example.gymcrush.data.remote.dto.NutricionResponseDto>>() {
                @Override
                public void onResponse(Call<ApiResponseDto<com.example.gymcrush.data.remote.dto.NutricionResponseDto>> call, Response<ApiResponseDto<com.example.gymcrush.data.remote.dto.NutricionResponseDto>> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().getDatos() != null) {
                        com.example.gymcrush.data.remote.dto.NutricionResponseDto plan = response.body().getDatos();
                        tvNombrePlanWidget.setText(plan.getNombreDieta());
                        tvCaloriasWidget.setText(plan.getCaloriasObjetivo() + " kcal");
                        actualizarProgresoMacros(plan);
                    }
                }
                @Override public void onFailure(Call<ApiResponseDto<com.example.gymcrush.data.remote.dto.NutricionResponseDto>> call, Throwable t) {}
            });
        }
    }

    private void actualizarProgresoMacros(com.example.gymcrush.data.remote.dto.NutricionResponseDto plan) {
        double p = 0, c = 0, g = 0;
        if (plan.getComidas() != null) {
            for (com.example.gymcrush.data.remote.dto.ComidaDiariaResponseDto item : plan.getComidas()) {
                p += (item.getProteina() != null) ? item.getProteina() : 0;
                c += (item.getCarbohidratos() != null) ? item.getCarbohidratos() : 0;
                g += (item.getGrasas() != null) ? item.getGrasas() : 0;
            }
        }
        progressProt.setProgress((int) p);
        progressCarb.setProgress((int) c);
        progressGras.setProgress((int) g);
    }


    private void actualizarAdaptador(List<CarouselAdapter.CarouselItem> items) {
        runOnUiThread(() -> {
            if (carouselAdapter != null) {
                carouselAdapter.updateItems(new ArrayList<>(items));
            }
        });
    }

    private void setupModernMenu() {
        View menuView = slidingRootNav.getLayout().findViewById(R.id.menu_items_container).getRootView();
        
        ivHeaderPhoto = menuView.findViewById(R.id.img_logo_header_modern);
        TextView tvNombre = menuView.findViewById(R.id.tv_nombre_header_modern);
        
        // Actualizar datos del header en el menú
        SharedPreferences prefs = getSharedPreferences("DatosUsuario", MODE_PRIVATE);
        tvNombre.setText(prefs.getString("nombre_usuario", "Usuario GymCrush"));

        menuView.findViewById(R.id.menu_qr).setOnClickListener(v -> {
            slidingRootNav.closeMenu();
            mostrarDialogoQR();
        });
        menuView.findViewById(R.id.menu_rutinas).setOnClickListener(v -> {
            slidingRootNav.closeMenu();
            cargarFragmento(new RutinasFragment());
        });
        menuView.findViewById(R.id.menu_dieta).setOnClickListener(v -> {
            slidingRootNav.closeMenu();
            cargarFragmento(new DietaFragment());
        });
        menuView.findViewById(R.id.menu_clases).setOnClickListener(v -> {
            slidingRootNav.closeMenu();
            cargarFragmento(new ClasesFragment());
        });
        menuView.findViewById(R.id.menu_reservas).setOnClickListener(v -> {
            slidingRootNav.closeMenu();
            cargarFragmento(new ReservasFragment());
        });
        menuView.findViewById(R.id.menu_salud).setOnClickListener(v -> {
            slidingRootNav.closeMenu();
            cargarFragmento(new SaludFragment());
        });
        menuView.findViewById(R.id.menu_suplementacion).setOnClickListener(v -> {
            slidingRootNav.closeMenu();
            cargarFragmento(new SuplementacionFragment());
        });
        menuView.findViewById(R.id.menu_equipo).setOnClickListener(v -> {
            slidingRootNav.closeMenu();
            cargarFragmento(new EquipoFragment());
        });
        menuView.findViewById(R.id.menu_contacto).setOnClickListener(v -> {
            slidingRootNav.closeMenu();
            cargarFragmento(new ContactoFragment());
        });
        menuView.findViewById(R.id.menu_espacio).setOnClickListener(v -> {
            slidingRootNav.closeMenu();
            cargarFragmento(new EspacioFragment());
        });
        menuView.findViewById(R.id.menu_salir).setOnClickListener(v -> {
            slidingRootNav.closeMenu();
            mostrarDialogoCerrarSesion();
        });
    }

    private void cargarDatosYFotoPerfil() {
        SharedPreferences prefs = getSharedPreferences("DatosUsuario", MODE_PRIVATE);
        String nombre = prefs.getString("nombre_usuario", "Usuario");
        TextView tvSaludo = findViewById(R.id.tvSaludo);
        tvSaludo.setText("¡A tope, " + nombre + "!");

        String token = prefs.getString("auth_token", "");
        if (token.isEmpty()) return;

        ApiService apiService = RetrofitClient.getApiService();
        long usuarioId = prefs.getLong("usuario_id", -1);
        if (usuarioId == -1) return;

        apiService.getPerfilById(usuarioId).enqueue(new Callback<ApiResponseDto<PerfilResponseDto>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<PerfilResponseDto>> call, Response<ApiResponseDto<PerfilResponseDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    PerfilResponseDto perfil = response.body().getDatos();
                    if (perfil.getFoto() != null) {
                        byte[] decodedString = Base64.decode(perfil.getFoto(), Base64.DEFAULT);
                        Glide.with(DesarrolloActivity.this)
                                .load(decodedString)
                                .placeholder(R.drawable.logo)
                                .into(profileImage);
                        
                        Glide.with(DesarrolloActivity.this)
                                .load(decodedString)
                                .circleCrop()
                                .into(ivHeaderPhoto);
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponseDto<PerfilResponseDto>> call, Throwable t) {
                Toast.makeText(DesarrolloActivity.this, "Error al cargar perfil", Toast.LENGTH_SHORT).show();
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
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragment_container, fragment);
        ft.addToBackStack(null);
        ft.commit();
    }

    private void mostrarHome() {
        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        homeContent.setVisibility(View.VISIBLE);
    }

    private void mostrarDialogoQR() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_qr_gym);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ImageView ivQR = dialog.findViewById(R.id.ivQrCode);
        String userId = UUID.randomUUID().toString();

        try {
            MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
            BitMatrix bitMatrix = multiFormatWriter.encode(userId, BarcodeFormat.QR_CODE, 500, 500);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            ivQR.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }

        dialog.findViewById(R.id.btnCerrarQr).setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void mostrarDialogoCerrarSesion() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_custom_exit);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog.findViewById(R.id.btn_confirmar_exit).setOnClickListener(v -> {
            SharedPreferences prefs = getSharedPreferences("DatosUsuario", MODE_PRIVATE);
            prefs.edit().clear().apply();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        dialog.findViewById(R.id.btn_cancelar_exit).setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        LatLng gymLocation = new LatLng(39.4699, -0.3763);
        map.addMarker(new MarkerOptions().position(gymLocation).title("GYMCrush Valencia"));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(gymLocation, 15f));
    }
}