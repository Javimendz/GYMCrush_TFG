## CONTROL DE ACCESO (QR DINÁMICO)

Gestión de entradas y salidas mediante tokens temporales para control de aforo y seguridad.

---

### Registro de Salida (Escáner / Torno)

- **Método:** `POST`
- **Endpoint:** `/api/v1/accesos/validar-salida`
- **Descripción:** El dispositivo escáner envía el QR leído al finalizar el entrenamiento para cerrar la sesión activa del socio.
- **Headers:**
  - `Content-Type: application/json`
  - `Authorization: Bearer <TOKEN_ADMIN_ESCÁNER>`

**Ejemplo de cuerpo (JSON):**

```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbl9neW0iLCJ1c2VySWQiOjEsInB1cnBvc2UiOiJRUl9BQ0NFU1MiLCJpYXQiOjE3MTExMDU2MDAsImV4cCI6MTcxMTEwNTYzMH0..."
}
```

---

### Consulta de Historial (Socio / Admin)

- **Método:** `GET`
- **Endpoint:** `/api/v1/accesos/historial`
- **Descripción:** Obtiene la lista cronológica de todas las sesiones de entrenamiento (entradas y salidas) del usuario autenticado.
- **Headers:**
  - `Authorization: Bearer <TOKEN_DE_SESIÓN_USUARIO>`

**Ejemplo de petición:**

`GET http://localhost:8080/api/v1/accesos/historial`

**Ejemplo de respuesta (body):**

```json
{
  "mensaje": "Historial recuperado",
  "success": true,
  "datos": [
    {
      "id": 10,
      "username": "usuario_gym",
      "fechaHoraEntrada": "2026-03-24T18:00:00",
      "fechaHoraSalida": "2026-03-24T19:30:00",
      "tipo": "SALIDA"
    },
    {
      "id": 11,
      "username": "usuario_gym",
      "fechaHoraEntrada": "2026-03-25T10:15:00",
      "fechaHoraSalida": null,
      "tipo": "ENTRADA"
    }
  ]
}
```

---

### Consulta de Aforo en Tiempo Real

- **Método:** `GET`
- **Endpoint:** `/api/v1/accesos/aforo`
- **Descripción:** Calcula y devuelve el número total de socios que se encuentran actualmente dentro de las instalaciones (sesiones sin marcar salida).
- **Headers:**
  - `Authorization: Bearer <TOKEN_DE_SESIÓN_USUARIO>`

**Ejemplo de petición:**

`GET http://localhost:8080/api/v1/accesos/aforo`

**Ejemplo de respuesta (body):**

```json
{
  "mensaje": "Aforo en tiempo real",
  "success": true,
  "datos": 42
}
```

---

Si quieres, puedo añadir también las secciones detalladas para `generar-qr` y `validar-entrada` con ejemplos.