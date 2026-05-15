añadido a usuario.java 29/04/2026

 @ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "plan_activo_id")
private PlanEntrenamiento planActivo;

en Entrenamiento añadido relacion 1-N

@OneToMany(mappedBy = "entrenamiento", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Tutorial> tutoriales;

 @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id")
    private CategoriaTutorial categoria;

añadido a detalleplan

private Integer series;
    private Integer repeticiones;


añadido a rutina.java

@Column(nullable = false)
    @Builder.Default
    private Integer series = 0;
    
    @Column(nullable = false)
    @Builder.Default
    private Integer repeticiones = 0;

    @Column(nullable = false)
    @Builder.Default
    private Double peso = 0.0;



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



Url ModeloER draw.io:


https://viewer.diagrams.net/?tags=%7B%7D&lightbox=1&highlight=0000ff&edit=_blank&layers=1&nav=1&title=Diagrama%20sin%20t%C3%ADtulo.drawio&dark=auto#R%3Cmxfile%3E%3Cdiagram%20name%3D%22P%C3%A1gina-1%22%20id%3D%22kKPAJ71lcyTEgVdh1u9e%22%3E7V1bd9pIEv41Pmf2YfeoL7o9EsIknHXsrLEzk33xkaEDygqJI0Qcz6%2FfxkiAGtzYqERXmM5LUFsIqK%2Bqur6vbxesO%2F35IY9mk0%2FZSCQX1Bn9vGDvLyilIQ3lf8uWp1WL6%2FirhnEej1ZNZNMwiP8SZaNTti7ikZjXbiyyLCniWb1xmKWpGBa1tijPs8f6bd%2BypP6ps2gsdhoGwyjZbf0jHhWTVWtA%2FU37RxGPJ9UnE6%2F8wdOourn8JfNJNMoet5pY74J18ywrVq%2BmP7siWRqvssvqfb%2B%2F8Nf1F8tFWux5w91c5NcP35c2oU4SPUhcnm%2B6oG4aTYX8b%2FWA54vnv9wN7jo3%2FevyuUk0FBNpLZHXLVh9%2Fn9%2BRF3n492n%2B%2FzrpXgMh%2Bx26P6T%2Bsxd%2F7T1V97%2BjuVT5sVTZWFpl9nyZS6%2FapSOl83vvsVJ0s2SLH%2B%2BxV1eytZ5kWf%2FE1vtq4bl%2FVlabNqZ8%2FyvbB%2BUH0Xk9eMkLsRgJn%2BabHiUHivbJsU0Kf%2BcZ4t0JJY%2FcPnm1Tf9IfJC%2FNz68uVv%2ByCyqSjyJ3nLZMsBWIn245azlE1VTPhO2VCGBFvfEZWmHq%2BfvcFTvigNWl1uIXwk4p97N7%2F3LxsC7lnAFcB9BXCX1QGnnmsI8Jvry15DuH0LtwK3p8Adum4dbh66ZuC%2BvS9z%2BuB%2BifygIfQBVugdU9ATV8He8xTsPW4o1Lud296H65t%2BpyHoIVbQjcV7qGDO%2FTrmZN3BnxrzTve2%2F6X%2FvvO%2BGebcsZgfxFzp0h3XUI4fdC4bhjgnFm4FbiWtk1CFm1FDcH%2B8vmlO0ji1iB8gaeuIN474TW%2FQu%2FnSNMaZRVyPOJPFnIq4sW682xs0DXFuAT9AywlRQjz0DAF%2B2%2B%2F%2Bu3fbEHArvB0AnPi8DjhjpoS3q%2Bvb%2Fu%2F9bqfbv75qCLuV33ZIOVGrN1fBnfiG%2BvLux07TMLf6m4q3or8xpuR1Rk3JrZKb3TXl4mhFNzR4ryu5Cm%2BTmtv9%2B37vtmmxbjW3A%2FqLJORBDXNOmCHMAfB2rd52IMZJGLJ6jIfEVOl2d3vTb163uVZ0e2uMM4%2BZKtuuP%2FXfd2Rq7zQeT3Gt9LZTr1MFeO6pHbox4GWHfnt3uxxGazhNwrUC3CGeRoOwrsBxb8VuT4977%2Br2pnfV%2BdSXLxoKca4V4naAZwrwnjIjinNTvTtQsFstTsVcVdsdugO5oRz%2FpT%2B461z2%2FwsgxrlWjDuU5Jmrdu7cVOf%2B%2BbJzdV9m%2Boa4W1HuUFHHHFV8D0NTjF0S9svL3v0S%2F4a4W3HuYLwzZViVB46pgfS7237jSLfSnIq4OqzKnTrgLDQlx97GIpXtXiKf8O4hl6%2FGy1e%2FSVt0yD%2BaOYJ3vH4zyaYPi3lLbnCaqbB81w3UmbCuku9lBWhqOsU8HqfRKLvv7PGFK%2BkLn5r6wvGaznn6gjpbMtzxBVP8rivNs8wK93fzxQve0DgzHK%2F0nKc3EEel%2B%2BrcOvPucJMlbbnD8frP39UduClm0E2iefwtHkYvFA1XTX3heF3oPH1hp3YMlKUUxoqGz3k2zqNpa55wvFJ0np5wcP61Y2oSTyd5EPm4NUc4Xjo6T0dQV1M6yuwec9Oy%2B1MJTPESoWzsCMdrSX8PR2DKIBH1TS2r%2FSArxry1hHC8wvS38APm7KzMMTVydCOiJP6rLUfwj58V9vdwBErrxaK5hHAjxrG0YWueYLVGPW3YmQHsmJoN2nnI2yoQfKsyXmgVZ6pss2JueU%2BrBYJv9UVtv0DKpXDby31MbbcjhvFDa%2FnACosHCIO6DsiYH%2FTm971pPM%2FytlzB6opaNYkRpqQEZmrEQbqCzApiVrTnDFZavNCNRjJC8fQP7fIGqy1qeQMjiqTEXFO8oe2RJ9%2Bqi1pXIGGgLCh0jC0iLcek2%2FIEqy%2B%2BzROYb4pGDuLxoi03CKy6qFcTiLKrn8FFaNl0lrWWDwIrLmq5A%2FG9HUc40yIhsAqjtmugvrpoydxOE227ghUZ9dRhZ%2F2axwytVe2nw2Tx1Fr3YFVGvcoYKHObza1dbXc4OrAao94R1M6BBcZmrA2HYtRaQrDy4oGE4GPxgy%2FxfNFqSrD6or5IYOrGg9wUdejN76U3FNl9L23LGazCeKEddnAVQcHcjgfdXLSWEqy6qJeVAnWqkm9qOnPLOnNoBcYDnYMyXYmbm7SmWyAL4gtWY9T2DdTb3STBVKEgb14UbXUPoVUY9eSBq3OWfFNJ4V00j0ZRe%2FViaBVGfU5g6m541JTY%2FDmJ0vvOsIh%2FZC3tphFalVFPHhw1L3jBScnD0cAerRouoXp%2BKb9CO%2BAun7O%2BfP4UDPvnBArSITdFEKpYX1ShLrEum%2BTz160NHeRoOVHeG8%2Fm4peO%2FD3uoO6N6apz1Jip3REW8h2rPzUC%2FGjR8EwBV1e%2Be%2BoyFtfU%2FCMxjeKkIdpHq4JnivbOgbEK82PE1PDhLJrPH7N81BDwowXAMwVcnUOijhdv1jOfY%2F9e6hjWH17s37m6itnY4fCpZFJ5s949cI5W%2Bs4UblXgcRVWT11Thw6KUdQs1wfO0WremYKtdu5c3ffQGNjfsiJrCPbRet2Zgs3VyFaWHS9F%2FHPu2I%2BW7M7UH5SOna1PhN9sa2hovB%2BkYz9ayDtTuNWOPdyB2xSRO034W91OX9d7yrIR4psa0gUJf6va6cOfK7uSEWOq3WnC3%2Bp6%2BvD3FVpPjB0uDxL%2BVtXTh7%2BvjNLQdfV3arhHYj7M49kwztJmmBOr3F3oB2oU6Z4EZ63kEivtaVM%2BCZSNA6ixc%2BchUj6x4p425e%2BsC6eMGkr5w0iaIm4s5xKr8GkTPgm8%2BiHk9Kyn5gTEKnz6Gl%2Bdobm54%2BRFXxwNxDRKo4aIW5FPX%2FIRZUEfNbZh2CTLo34ayzK%2FIeRWxzsAuXJerbkgP03St0KfNukzqo7pUlM6v5gX0ahp%2BFsdT1vlM65mfGPHz5wm%2FK3Qp6%2F5iDI9nwamtoX7JoaTqJcWeTRqWPZRq%2FRd6FbfkFCZkk2NifknSQLUKn16pc9XakJmbGpHNF%2BkTWd2Uav06ZU%2Bd7U8cQtuUxuGgw3uUKv16bU%2BlyiYGztM5jQp32p9%2BpQfKvM7mLHzSIu4WCRNU74V%2Bi50M7dJoJ4h5JhK%2BacJf6sC6lUfpkznYYQbqvimIp1H3xsO7lKr8l3oVumxnX1YyHkzPisD6uNfVX3Nbds2E%2FOmnb%2FV%2BLSdP2NcOQHE2LKtKCkWeUN1j1l17wDcOyd9%2BIb69pPkembVvQNUT1H3OP2VV24wq%2B5p1T26c5KDMbhPE%2F5W%2BdOH%2F87%2B7aGpAX6Q8LfCnj781QFdc3CfJvyt8qcNf5n8sRT%2FIOFvhT1t%2BJOQqHCb0vVPE%2F5W%2BNP3%2Fq6jkkFTi7qm8mMaj%2B0zK%2Bxd6IRewtRTm4wd83ma%2BLfan777D1k9%2FrmxDdghun9utT999R%2Bq5N8963FebrU%2Fffgz5Xgec6c5goS%2F1f704c8DJdsbW891mvC32t%2BB3l%2Bp%2FjkzFf4Qs7y41f4utCN%2FTqj2%2Fmct%2FXOr%2FWnDn%2Fmrbc62z%2Bs0ubhneU5j86ne3EqAddSJo8DuKjt5sPNe4sutBqhPA0SpCln4K08A4FYC1JKAzdTuqgowdgTHacLfSoD68GfKTr48MLX0Zw6wp4trJcAL%2FVRvNfwDx1DRd5Lwd60EqNcAqLq4NzR17uYwm84S0XyTD9fKgHXI1aNWd87YA4J8be1DAJX7yonRWNQg2YdStsiHQvc439sFVKSjTp5nj%2FIqzVJRN7KK4gat1V%2F%2BKC1H1xAUUT4WhfZLVMz5R5Qs1vltH1a5SKIi%2FlH%2F4W%2Bw%2B6uNzGGNTK2R9xjZhTUyw2DkAJuRPVgjc2vkPUb2YY3sYjByiM3IAayRPQRGrkoHPEYOYY3sYzAyQWbkijRAGTlAYGT46uL5rfJXRE9bN8yyOC3mW0%2F%2BvGzYKs89tTwnZU2%2FgWz1zA2A6y%2FXCFMCi2mIAFNOsQUOBTVyJeacWeA0NTIswfQtwdxnZFiC6VuCuc%2FIsATTx0AwOTojwxJMHwXBRGdkWILpoyCY6IwMSzB9DAQTn5FhCaaPgWCiM7IPSzB9DASTY9OjfFjG52NgfC42PaoqbIGMHFjGt8%2FIsIwvwMD4XGyinw%2FL%2BAIMjK8ai8ZjZFjGF2BgfPiMDMv4AgyMrxrwx2NkWMYXWMa3z8iwjC%2BwjG%2BfkWEZX2AZ3x4jB7CML8DA%2BFx0RoZlfAEGxofPk2EZX4iB8VXzg%2FAYGZbxhSgYH7YSLoBlfCEGxocvXcAyvhAF40PnybCML0TB%2BNDlZFjGF1oyss%2FIsIwvtGRkn5FhGV%2BIgYygM3IIy%2FhCS0b2GRmU8QUOBjLCPGxGBmV8gYOBjOAzMijjCxwUZASdkUEZX%2BBgICP4jAzK%2BAIHAxlh2MhICMr41rskWCPXjAzK%2BAIHBePDNk0rBGV8gYOC8aEzMijjCxwMjA%2Fb2sz1xmJQRsbA%2BPAZGZbxEQyMD5%2BRYRkfwcD4sK0yDh1YxkcwMD58RoZlfAQD48NnZFjGRzAwPmxrq0MHlvERDIwPn5FhGR%2FBwPjwGRmW8REMjI8jmwQeOrCMj6BgfNiMTGAZH0HB%2BJBNbgkr9gBkZIqC8aEzMizjW99qt2LYNjIs46MoGB86I8MyPoqC8aEzMizjoygYH7KB1JDAMj6KgvGhMzIs46MoGB%2BygdT1udxQRkbB%2BNAZGZbxURSMD9lOwusjZqGMjILxoTMyLONjKBgfOiPDMj6GgvEhm3cRUljGx1AwPnRGhmV8DAPjw7bRU0hhGR%2FDwPjwGRmW8TEMjA%2FbRk8hhWV8DAPjw2dkWMbHMDA%2Bl2IzMizjYxgYHzojM1jGxzAwPmxbloUVewAyMsfA%2BPAZGZbxVdPQ7FYMNSPDMr5q8o41cs3IsIyPo2B82Mb4GCzj4ygYHzojwzI%2BjoLxYRvjY7CMj6NgfOiMDMv4OArGh22Mj8EyPo6C8WEzModlfBwF48M2%2FMRhGZ%2BLgvGhMzIs43NRMD5sw08clvFVApg1cs3IwAe%2BY%2FBkbDu3hBx2r04Pgydj21Qk5MAHvmPQLrDtCxdy4APfMWgX%2BDwZ%2BMB3DNoFuq0YOCjj8z0M2gW6Faku8IHvGLQLfEYGZXy%2Bh0G7wGdkUMaH5Jh7bAOpLvAJ7Bi0C3xGBj6BHQPjQ7fs14VlfCiOuUe3WNIFPoEdA%2BNDt47PBT6BHQPjw2dk4BPYUTA%2BbLTahWV8KI65R2dkD%2FgEdhSMD9vwkwfL%2BFAcc49uzUilsp%2FVMffYljN4wCewY2B8%2BIwMfAI7BsaHbjmDB8v4cBxzj41We8AnsGNgfOimznrAJ7BjYHz4jAx8AjsGxocvXcAyPhTH3KPzZB%2F4BHYMjA%2FdJPBKZT%2BrY%2B7RGRmW8aE45h7dJHAf%2BAR2FIwPm0DkA5%2FAjoLxoTMy8AnsKBgftvnJPvAJ7CgYHzojA5%2FAjoHxoZs6GwDvOrsnXcgnD8rLLC8m2ThLo6S3aX2XZ4t0JJbfc2ndzT2XWTYrwfguiuJpEP%2B1fEi0KLI6VOJnXPy5fPu%2F3PLqa%2Fmw5ev3P7cvnqqLVBrwz%2B2Lr9sXmzc9X1Xvei3QQVu43s1Ffv3wXQwL%2Bb4kehBJ5UVeIr%2FRu1H8Q74cF8%2FfddX0kKst8plb930S6Tz6Lu67k6ho8JjVT0mjafnbuh87t6UrJdFQTLJkJPJ57XdrfZPR1cSrujvvc8uNr80n0Wz5Mpf2idLxs3d9i5NkE8Xu8nJPdLurhuX9WVrsi%2Fple%2BXHS6d7nMSFGMyi54h4zKNZ3SfrTl1GvcgL8XPry%2B%2F6x0TE40n5%2B6q9aB7LTCOvqsKyesh6qOKpfAutFN83%2BJS83HIrrZtJxBSYi7hYJFlDoPmxQMt749m8LZgdUzC7KszVYZNrmKudaduB%2BZsYTqJe%2BiPOWoHbtXDr4Q6UqGa8VbiH0ggijUftoO1ZtHVoU4epwU1A0H5dCSgBgtSjq2TeTp29KdpWld5ry7aqQKyKxa%2B1YnF%2FgfjKUq8qUyBr%2BvKtn7M4LbbSgrvTC7iKo6zgKd%2B38ZU9j6pkgupRXO1QVj9851HPbrf%2BTcczD1moQCr0VZppye328guidZ9jXRWf23mB6nbOsW7nBYrbMf%2Fkbgc5ZlHVMqfNduSV2e4tdBih21HVV9Ru8fVux9RHBVBu91JJtSyeoz1s%2BjcJXufqH6%2Bj0B9EKvJol4CvH9KkLvO2cXsbu55k04fF%2FJeuy%2FhuXeYpdRnxFK8B4tavzlSwY1Jtbg%2B%2BzlRkO09tpa2mnV017bXtrEOZq3R2JDwy68gq%2FtCj2u%2FsIEfcNihgcqFNZ0fCWnXPeVW6vVCgyYvPIo%2BllUWOtReknCtOxI4tvuja%2FV58FHgv2JV5PZad2P6OkHZxdIRHy1Fn2hFWM06qh6z3tHvRcVruCCHHqDax2ypTfLPQ0HomoUwdKqgmvby5nl6fiwafSV7tE5CDw5vKrmUa5x%2BlWsmuLaj503IbqJa6tjYWVL%2FgkI46dsWcox3yYC95tEPKyzzLiu3bZRqefMpGy06u938%3D%3C%2Fdiagram%3E%3C%2Fmxfile%3E