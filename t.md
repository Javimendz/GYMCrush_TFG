añadido a usuario.java 29/04/2026

 @ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "plan_activo_id")
private PlanEntrenamiento planActivo;

Relaciones 

// USUARIO Y PERFIL
Usuario 1 ─── 1 Perfil

// ACTIVIDAD Y SALA
Categoria 1 ──── N Actividad
Actividad 1 ──── N Horario
Sala      1 ──── N Horario

//ACCESO 
Usuario 1 ──── N Acceso 

// HORARIO Y RESERVAS
Horario   1 ──── N Reserva
Usuario   1 ──── N Reserva


// ROLES Y PERMISOS
Usuario N ──── N Rol

// SALUD Y ENTRENAMIENTO
Usuario   1 ──── N Entrenamiento
Usuario   1 ──── N Salud
Entrenamiento 1 ──── N Rutina
Usuario   1 ──── N Rutina

// DIETA
Usuario 1 ──── N SugerenciaDieta
Dieta 1 ──── N SugerenciaDieta

// TUTORIAL
CategoriaTutorial 1 ──── N Tutorial
Tutorial  N ──── N Usuario  a través de  Visualizacion

// CHAT 
Usuario   1 ──── N Chat  


// SOPORTE Y NOTIFICACIONES
Usuario   1 ──── N Ticket
Usuario   1 ──── N Notificacion



Modelo relacional 
Tablas 

TRoles(id PK, name)

TUsuarios_Roles(usuarioId FK, rolId FK)

TAccesos(id PK, fecha_hora_entrada, fecha_hora_salida, tipo, qr_token,usuarioId FK)

TUsuarios(id PK, username, correo, password, qr_token,token_expira_en, fecha_registro, created_at, updated_at)

TPerfiles(id PK, nombre, apellidos, telefono, dni, fecha_nacimiento,genero, foto_url, bio, direccion, ciudad, pais, codigo_postal,usuarioId FK)

TCategorias(id PK, nombre, descripcion)

TSalas(id PK, nombre, capacidad_max, descripcion,ubicacion, equipamiento, activa)

THorarios(id PK, dia_semana, hora_inicio, hora_fin, aforo_max, salaId FK, actividadId FK)

TActividades(id PK, nombre, tipo, duracion_min, intensidad, precio, categoriaId FK)

TReservas(id PK, fecha_reserva, estado, confirmado, usuarioId FK, horarioId FK)

TEntrenamientos(id PK, fecha, hora_inicio, duracion_min, intensidad, notas,usuarioId FK)

TRutinas(id PK, fecha_asignacion, orden, completado,usuarioId FK ,entrenamientoId FK )

TSalud(id PK, peso_kg, estatura_cm, nivel_actividad, comentario, fecha_medicion,usuarioId FK)

TDietas(id PK, nombre, tipo_dieta, objetivo_calorico, proteinas_g, carbohidratos_g, grasas_g, categoria)

TSugerenciasDieta(id PK, fecha_generacion, observaciones,usuarioId FK ,dietaId FK )

TCategoriasTutorial(id PK, nombre, descripcion)

TTutoriales(id PK, titulo, descripcion, url_video, duracion_min,categoriaId FK)

TVisualizaciones(id PK, fecha_visualizacion, completado, progreso_segundos,usuarioId FK,tutorialId FK)

TNotificaciones(id PK, titulo, mensaje, tipo, leido, fecha_hora_creacion, url_accion,usuarioId FK)

TTickets(id PK, asunto, mensaje, estado, fecha_creacion, fecha_resolucion,usuarioId FK)