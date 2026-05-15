
Modelo Entidad-Relación
------------------------

Entidades Principales
------------------------

Usuario(id, username, email, password, qrToken, tokenExpiraEn, fechaRegistro, createdAt, updatedAt, planActivoId FK)
Perfil(id, nombre, apellidos, telefono, dni, fechaNacimiento, direccion, ciudad, pais, codigoPostal, bio, genero, foto, usuarioId FK)
Role(id, name)
Actividad(id, nombre, descripcion, duracion, sala, precio, categoriaId FK)
Categoria(id, nombre, descripcion)
Horario(id, diaSemana, horaInicio, horaFin, aforoMax, actividadId FK, entrenadorId FK, salaId FK)
Sala(id, nombre, capacidadMax, descripcion, ubicacion, equipamiento, activa)
Reserva(id, fecha, estado, confirmado, usuarioId FK, horarioId FK)
Acceso(id, fechaHoraEntrada, fechaHoraSalida, tipo, usuarioId FK)
Ticket(id, asunto, mensaje, estado, fechaResolucion, fechaCreacion, usuarioId FK)
Notificacion(id, titulo, mensaje, fechaCreacion, leido, tipo, usuarioId FK, ticketId FK)
Chat(id, mensaje, fechaHora, leido, emisorId FK, receptorId FK)
MensajeChat(id, ticketId, contenido, emisor, fechaEnvio, tipo, usuarioId FK)
Salud(id, peso, estatura, nivelActividad, comentario, fechaMedicion, imc, usuarioId FK)
CategoriaDieta(id, nombre, descripcion)
Dieta(id, nombre, tipo, objetivoCalorico, cantidadProteinas, cantidadCarbohidratos, cantidadGrasas, descripcion, categoriaDietaId FK)
Nutricion(id, tipoDieta, fechaGeneracion, nombreDieta, caloriasObjetivo, macronutrientesJson, usuarioId FK, dietaId FK)
ComidaDiaria(id, momento, nombreAlimento, calorias, proteina, carbohidratos, grasas, imagenUrl, fechaRegistro, planId FK)
CategoriaTutorial(id, nombre, descripcion)
Entrenamiento(id, nombre, urlImagen, descripcion, cantidadEjercicios, duracion, intensidad, esGlobal, categoriaId FK, usuarioId FK)
Tutorial(id, titulo, descripcion, musculoObjetivo, equipamiento, urlVideo, duracionMin, esGlobal, categoriaId FK, entrenamientoId FK, usuarioId FK)
Visualizacion(id, fechaCreacion, ultimaActualizacion, completado, progresoSegundos, contadorReproducciones, usuarioId FK, tutorialId FK)
PlanEntrenamiento(id, nombre, descripcion, objetivo, nivel, esGlobal, usuarioId FK)
DetallePlan(id, diaSemana, orden, series, repeticiones, planId FK, entrenamientoId FK)
Rutina(id, fechaAsignacion, orden, completado, series, repeticiones, peso, usuarioId FK, entrenamientoId FK)






Relaciones 
------------


v1: Usuario (1) --- (N) Perfil

Un usuario tiene un perfil (1:1 implementado como 1:N con unique)

v1: Usuario (N) --- (M) Role
v2: Tabla intermedia TUsuarios_Roles(usuarioId FK, roleId FK)

v1: Categoria (1) --- (N) Actividad

v1: Actividad (1) --- (N) Horario

v1: Usuario (1) --- (N) Horario (como entrenador)

v1: Sala (1) --- (N) Horario

v1: Horario (1) --- (N) Reserva

v1: Usuario (1) --- (N) Reserva

v1: Usuario (1) --- (N) Acceso

v1: Usuario (1) --- (N) Ticket

v1: Ticket (1) --- (N) Notificacion

v1: Usuario (1) --- (N) Notificacion

v1: Usuario (1) --- (N) Chat (como emisor)

v1: Usuario (1) --- (N) Chat (como receptor)

v1: Usuario (1) --- (N) Salud

v1: CategoriaDieta (1) --- (N) Dieta

v1: Dieta (1) --- (N) Nutricion

v1: Usuario (1) --- (N) Nutricion

v1: Nutricion (1) --- (N) ComidaDiaria

v1: CategoriaTutorial (1) --- (N) Tutorial

v1: CategoriaTutorial (1) --- (N) Entrenamiento

v1: Entrenamiento (1) --- (N) Tutorial

v1: Usuario (1) --- (N) Entrenamiento (entrenamientos personalizados)

v1: Usuario (1) --- (N) Tutorial (tutoriales personalizados)

v1: Usuario (N) --- (M) Tutorial (a través de Visualizacion)
v2: Tabla intermedia TVisualizaciones(id PK, usuarioId FK, tutorialId FK, fechaCreacion, ultimaActualizacion, completado, progresoSegundos, contadorReproducciones)

v1: PlanEntrenamiento (1) --- (N) DetallePlan

v1: Entrenamiento (1) --- (N) DetallePlan

v1: Usuario (1) --- (N) PlanEntrenamiento

v1: Usuario (1) --- (N) Rutina

v1: Entrenamiento (1) --- (N) Rutina

v1: PlanEntrenamiento (1) --- (N) Usuario (como plan activo)









Modelo Relacional
Tablas
TRoles(id PK, name)  
TUsuarios(id PK, username, email, password, qrToken, tokenExpiraEn, fechaRegistro, createdAt, updatedAt, planActivoId FK)  
TUsuarios_Roles(usuarioId FK, roleId FK)  
TPerfiles(id PK, nombre, apellidos, telefono UNIQUE, dni UNIQUE, fechaNacimiento, direccion, ciudad, pais, codigoPostal, bio, genero, foto, usuarioId FK UNIQUE)  
TCategorias(id PK, nombre UNIQUE, descripcion)  
TActividades(id PK, nombre, descripcion, duracion, sala, precio, categoriaId FK)  
TSalas(id PK, nombre UNIQUE, capacidadMax, descripcion, ubicacion, equipamiento, activa)  
THorarios(id PK, diaSemana, horaInicio, horaFin, aforoMax, actividadId FK, entrenadorId FK, salaId FK)  
TReservas(id PK, fecha, estado, confirmado, usuarioId FK, horarioId FK)  
TAccesos(id PK, fechaHoraEntrada, fechaHoraSalida, tipo, usuarioId FK)  
TTickets(id PK, asunto, mensaje, estado, fechaResolucion, fechaCreacion, usuarioId FK)  
TNotificaciones(id PK, titulo, mensaje, fechaCreacion, leido, tipo, usuarioId FK, ticketId FK)  
TChat(id PK, mensaje, fechaHora, leido, emisorId FK, receptorId FK)  
TMensajes_Chat(id PK, ticketId, contenido, emisor, fechaEnvio, tipo, usuarioId FK)  
TSalud(id PK, peso, estatura, nivelActividad, comentario, fechaMedicion, imc, usuarioId FK)  
TCategorias_Dieta(id PK, nombre UNIQUE, descripcion)  
TDietas(id PK, nombre, tipo, objetivoCalorico, cantidadProteinas, cantidadCarbohidratos, cantidadGrasas, descripcion, categoriaDietaId FK)  
TPlanes_Nutricionales(id PK, tipoDieta, fechaGeneracion, nombreDieta, caloriasObjetivo, macronutrientesJson, usuarioId FK, dietaId FK)  
TComidas_Diarias(id PK, momento, nombreAlimento, calorias, proteina, carbohidratos, grasas, imagenUrl, fechaRegistro, planId FK)  
TCategorias_Tutorial(id PK, nombre UNIQUE, descripcion)  
TEntrenamientos(id PK, nombre, urlImagen, descripcion, cantidadEjercicios, duracion, intensidad, esGlobal, categoriaId FK, usuarioId FK)  
TTutoriales(id PK, titulo, descripcion, musculoObjetivo, equipamiento, urlVideo, duracionMin, esGlobal, categoriaId FK, entrenamientoId FK, usuarioId FK)  
TVisualizaciones(id PK, fechaCreacion, ultimaActualizacion, completado, progresoSegundos, contadorReproducciones, usuarioId FK, tutorialId FK, UNIQUE(usuarioId, tutorialId))  
TPlanes_Entrenamiento(id PK, nombre, descripcion, objetivo, nivel, esGlobal, usuarioId FK)  
TDetalles_Plan(id PK, diaSemana, orden, series, repeticiones, planId FK, entrenamientoId FK)  
TRutinas(id PK, fechaAsignacion, orden, completado, series, repeticiones, peso, usuarioId FK, entrenamientoId FK)  

