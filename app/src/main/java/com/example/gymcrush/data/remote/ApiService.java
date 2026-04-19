package com.example.gymcrush.data.remote;

import com.example.gymcrush.data.remote.dto.AuthResponse;
import com.example.gymcrush.data.remote.dto.CategoriaRequestDto;
import com.example.gymcrush.data.remote.dto.CategoriaResponseDto;
import com.example.gymcrush.data.remote.dto.CategoriaTutorialRequestDto;
import com.example.gymcrush.data.remote.dto.CategoriaTutorialResponseDto;
import com.example.gymcrush.data.remote.dto.ExerciseApiRequestDto;
import com.example.gymcrush.data.remote.dto.LoginRequest;
import com.example.gymcrush.data.remote.dto.ApiResponseDto;
import com.example.gymcrush.data.remote.dto.PerfilRequestDto;
import com.example.gymcrush.data.remote.dto.PerfilResponseDto;
import com.example.gymcrush.data.remote.dto.RegisterRequest;
import com.example.gymcrush.data.remote.dto.SaludRequestDto;
import com.example.gymcrush.data.remote.dto.SaludResponseDto;
import com.example.gymcrush.data.remote.dto.TutorialRequestDto;
import com.example.gymcrush.data.remote.dto.TutorialResponseDto;
import com.example.gymcrush.data.remote.dto.EntrenamientoRequestDto;
import com.example.gymcrush.data.remote.dto.EntrenamientoResponseDto;
import com.example.gymcrush.data.remote.dto.HorarioRequestDto;
import com.example.gymcrush.data.remote.dto.HorarioResponseDto;
import com.example.gymcrush.data.remote.dto.ActividadRequestDto;
import com.example.gymcrush.data.remote.dto.ActividadResponseDto;
import com.example.gymcrush.data.remote.dto.ReservaResponseDto;
import com.example.gymcrush.data.remote.dto.SalaRequestDto;
import com.example.gymcrush.data.remote.dto.SalaResponseDto;
import com.example.gymcrush.data.remote.dto.UsuarioResponseDto;
import com.example.gymcrush.data.remote.dto.VisualizacionRequestDto;
import com.example.gymcrush.data.remote.dto.VisualizacionResponseDto;
import com.example.gymcrush.data.remote.dto.ComidaDiariaRequestDto;
import com.example.gymcrush.data.remote.dto.ComidaDiariaResponseDto;
import com.example.gymcrush.data.remote.dto.DietaResponseDto;
import com.example.gymcrush.data.remote.dto.NutricionResponseDto;
import com.example.gymcrush.data.remote.dto.PerfilFisicoRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.*;



public interface ApiService {
    // --- NUEVOS ENDPOINTS PARA COMIDAS DIARIAS ---
    @POST("api/v1/comidas")
    Call<ApiResponseDto<ComidaDiariaResponseDto>> añadirComida(@Body ComidaDiariaRequestDto request);

    @DELETE("api/v1/comidas/{id}")
    Call<ApiResponseDto<Void>> eliminarComida(@Path("id") Long id);

    @POST("api/v1/comidas/batch/{planId}")
    Call<ApiResponseDto<Void>> guardarMuchasComidas(@Path("planId") Long planId, @Body List<ComidaDiariaRequestDto> comidas);

    // Endpoint de Inicio de Sesión
    @POST("api/v1/auth/login")
    Call<ApiResponseDto<AuthResponse>> login(@Body LoginRequest loginRequest);

    // Endpoint de Registro
    @POST("api/v1/auth/register")
    Call<ApiResponseDto<UsuarioResponseDto>> register(@Body RegisterRequest registerRequest);

    // Endpoints de Perfil
    @GET("api/v1/perfil/{id}")
    Call<ApiResponseDto<PerfilResponseDto>> getPerfilById(@Path("id") Long id);

    @PUT("api/v1/perfil/{id}")
    Call<ApiResponseDto<PerfilResponseDto>> updatePerfil(@Path("id") Long id, @Body PerfilRequestDto perfilRequestDto);

    @GET("api/v1/perfil")
    Call<ApiResponseDto<List<PerfilResponseDto>>> findAllPerfiles();

    @GET("api/v1/perfil/entrenadores")
    Call<ApiResponseDto<List<PerfilResponseDto>>> findEntrenadores();

    // Endpoints de Salud
    @GET("api/v1/salud/usuario/{id}")
    Call<ApiResponseDto<SaludResponseDto>> getSaludActual(@Path("id") Long id);

    @GET("api/v1/salud/usuario/{id}/historial")
    Call<ApiResponseDto<List<SaludResponseDto>>> getHistorialSalud(@Path("id") Long id);

    @POST("api/v1/salud/usuario/{id}")
    Call<ApiResponseDto<SaludResponseDto>> registrarSalud(@Path("id") Long id, @Body SaludRequestDto saludRequestDto);

    // Endpoints de Tutoriales (Ejercicios)
    @GET("api/v1/tutoriales")
    Call<ApiResponseDto<List<TutorialResponseDto>>> getTutoriales();

    @GET("api/v1/tutoriales/categoria/{catId}")
    Call<ApiResponseDto<List<TutorialResponseDto>>> getTutorialesByCategoria(@Path("catId") Long catId);

    @GET("api/v1/categorias-tutorial")
    Call<ApiResponseDto<List<CategoriaTutorialResponseDto>>> getCategorias();

    @POST("api/v1/categorias-tutorial")
    Call<ApiResponseDto<CategoriaTutorialResponseDto>> crearCategoria(@Body CategoriaTutorialRequestDto categoriaTutorialRequestDto);

    @GET("api/v1/tutoriales/biblioteca/buscar")
    Call<ApiResponseDto<List<ExerciseApiRequestDto>>> buscarEnBiblioteca(@Query("nombre") String nombre);

    // --- ENDPOINTS PARA CATEGORÍAS DE ACTIVIDAD ---
    @GET("api/v1/categorias")
    Call<ApiResponseDto<List<CategoriaResponseDto>>> getCategoriasActividad();

    @POST("api/v1/categorias")
    Call<ApiResponseDto<CategoriaResponseDto>> crearCategoriaActividad(@Body CategoriaRequestDto dto);

    @DELETE("api/v1/categorias/{id}")
    Call<ApiResponseDto<Void>> eliminarCategoriaActividad(@Path("id") Long id);

    @POST("api/v1/tutoriales")
    Call<ApiResponseDto<TutorialResponseDto>> crearTutorial(@Body TutorialRequestDto tutorialRequestDto);

    // Endpoints de Visualizaciones (Progreso)
    @POST("api/v1/visualizaciones/progreso")
    Call<ApiResponseDto<VisualizacionResponseDto>> guardarProgreso(@Body VisualizacionRequestDto visualizacionRequestDto);

    @GET("api/v1/visualizaciones/usuario/{usuarioId}/pendientes")
    Call<ApiResponseDto<List<VisualizacionResponseDto>>> getVideosPendientes(@Path("usuarioId") Long usuarioId);

    // --- NUEVOS ENDPOINTS PARA HORARIOS ---
    @GET("api/v1/horarios")
    Call<ApiResponseDto<List<HorarioResponseDto>>> getTodosLosHorarios();

    @GET("api/v1/horarios/dia/{dia}")
    Call<ApiResponseDto<List<HorarioResponseDto>>> getHorariosPorDia(@Path("dia") String dia);

    @GET("api/v1/horarios/{id}")
    Call<ApiResponseDto<HorarioResponseDto>> getHorarioById(@Path("id") Long id);

    @POST("api/v1/horarios")
    Call<ApiResponseDto<HorarioResponseDto>> crearHorario(@Body HorarioRequestDto horario);

    @PUT("api/v1/horarios/{id}")
    Call<ApiResponseDto<HorarioResponseDto>> actualizarHorario(@Path("id") Long id, @Body HorarioRequestDto horario);

    @DELETE("api/v1/horarios/{id}")
    Call<ApiResponseDto<Void>> eliminarHorario(@Path("id") Long id);

    // --- NUEVOS ENDPOINTS PARA ACTIVIDADES ---
    @GET("api/v1/actividades")
    Call<ApiResponseDto<List<ActividadResponseDto>>> getTodasLasActividades();

    @GET("api/v1/actividades/{id}")
    Call<ApiResponseDto<ActividadResponseDto>> getActividadById(@Path("id") Long id);

    @POST("api/v1/actividades")
    Call<ApiResponseDto<ActividadResponseDto>> crearActividad(@Body ActividadRequestDto dto);

    @PUT("api/v1/actividades/{id}")
    Call<ApiResponseDto<ActividadResponseDto>> actualizarActividad(@Path("id") Long id, @Body ActividadRequestDto dto);

    @DELETE("api/v1/actividades/{id}")
    Call<ApiResponseDto<Void>> eliminarActividad(@Path("id") Long id);

    @GET("api/v1/actividades/buscar")
    Call<ApiResponseDto<List<ActividadResponseDto>>> buscarActividadPorNombre(@Query("nombre") String nombre);

    @GET("api/v1/actividades/dia/{dia}")
    Call<ApiResponseDto<List<ActividadResponseDto>>> getActividadesPorDia(@Path("dia") String dia);

    @GET("api/v1/actividades/precio-max")
    Call<ApiResponseDto<List<ActividadResponseDto>>> filtrarActividadesPorPrecio(@Query("precio") Integer precio);

    // --- NUEVOS ENDPOINTS PARA RESERVAS ---
    @POST("api/v1/reservas/usuario/{uId}/horario/{hId}")
    Call<ApiResponseDto<ReservaResponseDto>> crearReserva(@Path("uId") Long usuarioId, @Path("hId") Long horarioId);

    @GET("api/v1/reservas/usuario/{usuarioId}")
    Call<ApiResponseDto<List<ReservaResponseDto>>> getMisReservas(@Path("usuarioId") Long usuarioId);

    @PATCH("api/v1/reservas/{reservaId}/cancelar")
    Call<ApiResponseDto<Void>> cancelarReserva(@Path("reservaId") Long reservaId);

    @PATCH("api/v1/reservas/{reservaId}/confirmar")
    Call<ApiResponseDto<Void>> confirmarAsistencia(@Path("reservaId") Long reservaId);

    // --- NUEVOS ENDPOINTS PARA SALAS ---
    @GET("api/v1/salas")
    Call<ApiResponseDto<List<SalaResponseDto>>> getTodasLasSalas();

    @GET("api/v1/salas/activas")
    Call<ApiResponseDto<List<SalaResponseDto>>> getSalasActivas();

    @GET("api/v1/salas/{id}")
    Call<ApiResponseDto<SalaResponseDto>> getSalaById(@Path("id") Long id);

    @POST("api/v1/salas")
    Call<ApiResponseDto<SalaResponseDto>> crearSala(@Body SalaRequestDto salaRequestDto);

    @PUT("api/v1/salas/{id}")
    Call<ApiResponseDto<SalaResponseDto>> actualizarSala(@Path("id") Long id, @Body SalaRequestDto salaRequestDto);

    @PATCH("api/v1/salas/{id}/estado")
    Call<ApiResponseDto<Void>> cambiarEstadoSala(@Path("id") Long id, @Query("activa") Boolean activa);

    @DELETE("api/v1/salas/{id}")
    Call<ApiResponseDto<Void>> eliminarSala(@Path("id") Long id);

    // --- NUEVOS ENDPOINTS PARA ENTRENAMIENTOS ---
    @GET("api/v1/entrenamientos/usuario/{usuarioId}")
    Call<ApiResponseDto<List<EntrenamientoResponseDto>>> getEntrenamientosUsuario(@Path("usuarioId") Long usuarioId);

    @POST("api/v1/entrenamientos")
    Call<ApiResponseDto<EntrenamientoResponseDto>> crearEntrenamiento(@Body EntrenamientoRequestDto request);

    // --- NUEVOS ENDPOINTS PARA DIETAS (CATÁLOGO) ---
    @GET("api/v1/dietas")
    Call<ApiResponseDto<List<DietaResponseDto>>> listarDietas();

    @GET("api/v1/dietas/{id}")
    Call<ApiResponseDto<DietaResponseDto>> obtenerDietaPorId(@Path("id") Long id);

    // --- NUEVOS ENDPOINTS PARA NUTRICIÓN (PLANES PERSONALIZADOS) ---
    @POST("api/v1/nutricion/generar-plan")
    Call<ApiResponseDto<NutricionResponseDto>> generarPlanNutricional(@Body PerfilFisicoRequest request);

    @GET("api/v1/nutricion/ultimo-plan/{usuarioId}")
    Call<ApiResponseDto<NutricionResponseDto>> obtenerUltimoPlan(@Path("usuarioId") Long usuarioId);

    @GET("api/v1/nutricion/historial/{usuarioId}")
    Call<ApiResponseDto<List<NutricionResponseDto>>> obtenerHistorialNutricional(@Path("usuarioId") Long usuarioId);

    // --- NUEVOS ENDPOINTS PARA BÚSQUEDA DE ALIMENTOS (FATSECRET) ---
    @GET("api/v1/nutricion/buscar")
    Call<ApiResponseDto<String>> buscarAlimentos(@Query("query") String query);
    @PATCH("api/v1/usuarios/{id}/asignar-entrenador")
    Call<ApiResponseDto<Void>> asignarRolEntrenador(@Path("id") Long id);

    // --- NUEVO ENDPOINT PARA HISTORIAL DE CHAT ---
    @GET("api/v1/chat/historial")
    Call<List<com.example.gymcrush.ui.social.SocialFragment.ChatMessageDto>> getHistorialChat();
}
