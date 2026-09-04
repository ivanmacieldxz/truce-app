# Especificación de Endpoints - Módulo de Usuarios

Todos los endpoints detallados a continuación están protegidos y requieren autenticación mediante JWT. Deben incluir el siguiente encabezado en la petición:
`Authorization: Bearer <tu_jwt_token>`

Prefijo base: `/api/v1`

---

## 1. Obtener mi perfil

Obtiene la información detallada del perfil del usuario autenticado.

**Signatura**
`GET /users/me`

**Parámetros**
Ninguno.

**Cuerpo (Body)**
Ninguno.

**Respuestas Posibles**

- **200 OK**
  Devuelve el objeto `UserDto` completo.
  ```json
  {
    "id": "uuid-del-usuario",
    "email": "usuario@ejemplo.com",
    "username": "usuario123",
    "fcmToken": "token-de-firebase-opcional",
    "createdAt": "2026-07-26T12:00:00Z",
    "updatedAt": "2026-07-26T12:00:00Z"
  }
  ```
- **401 Unauthorized**
  Si el token no se provee, expiró o es inválido.

---

## 2. Actualizar FCM Token

Permite actualizar de forma aislada el token de Firebase Cloud Messaging (`fcmToken`) desde el cliente móvil.

**Signatura**
`PATCH /users/me/fcm-token`

**Parámetros**
Ninguno.

**Cuerpo (Body)**
Se espera el DTO `UpdateFcmTokenDto`.
```json
{
  "fcmToken": "nuevo-fcm-token"       // Opcional
}
```

**Respuestas Posibles**

- **200 OK**
  Devuelve el objeto `UserDto` actualizado.
- **400 Bad Request**
  Si los datos enviados no pasan las reglas de validación.
- **401 Unauthorized**
  Si el token es inválido o no existe.

---

## 3. Buscar usuarios

Busca perfiles de usuarios de manera paginada. Se filtra por `username` ignorando mayúsculas y minúsculas (búsqueda parcial). Útil para enviar nuevas solicitudes de amistad.

**Signatura**
`GET /users`

**Parámetros (Query Params)**
- `q` (string, **requerido**): Término de búsqueda para el nombre de usuario.
- `page` (number, opcional, por defecto: `1`): Número de página para paginación.
- `limit` (number, opcional, por defecto: `20`, máximo: `100`): Límite de resultados por página.

*Ejemplo de URL: `/api/v1/users?q=juan&page=1&limit=15`*

**Cuerpo (Body)**
Ninguno.

**Respuestas Posibles**

- **200 OK**
  Devuelve un arreglo de objetos `UserSummaryDto`. Nótese que, por privacidad, este objeto omite información sensible (como `email` y `fcmToken`).
  ```json
  [
    {
      "id": "uuid-del-usuario",
      "username": "juanperez"
    },
    {
      "id": "uuid-de-otro-usuario",
      "username": "juan123"
    }
  ]
  ```
- **400 Bad Request**
  Si no se provee el parámetro de consulta obligatorio `q`, o si `page`/`limit` no son números válidos.
- **401 Unauthorized**
  Si el token es inválido o no existe.

---

## 4. Verificar disponibilidad de Nombre de Usuario

Endpoint público útil durante el registro para validar si un nombre de usuario ya está en uso antes de enviar el formulario.

**Signatura**
`GET /users/check-username`

**Parámetros (Query Params)**
- `username` (string, **requerido**): El nombre de usuario a verificar.

**Respuestas Posibles**

- **200 OK**
  ```json
  {
    "available": true // o false
  }
  ```

---

## 5. Verificar disponibilidad de Correo Electrónico

Endpoint público útil durante el registro para validar si un correo ya está en uso.

**Signatura**
`GET /users/check-email`

**Parámetros (Query Params)**
- `email` (string, **requerido**): El email a verificar.

**Respuestas Posibles**

- **200 OK**
  ```json
  {
    "available": true // o false
  }
  ```

---

## 6. Cambiar Nombre de Usuario

Permite cambiar el nombre de usuario asegurando validaciones estrictas.

**Signatura**
`PATCH /users/me/username`

**Cuerpo (Body)**
```json
{
  "username": "nuevo_nombre"
}
```

**Respuestas Posibles**

- **200 OK**: Devuelve el perfil del usuario actualizado.
- **400 Bad Request**: Validación fallida (ej. muy corto, contiene símbolos no permitidos).
- **409 Conflict**: Nombre de usuario ya en uso por otra persona.

---

## 7. Cambiar Correo Electrónico

Actualiza el correo electrónico del usuario. Operación crítica que primero actualiza el email en el proveedor de autenticación (Supabase Auth) y, si tiene éxito, lo actualiza en la base de datos de la aplicación.

**Signatura**
`PATCH /users/me/email`

**Cuerpo (Body)**
```json
{
  "email": "nuevo_correo@ejemplo.com"
}
```

**Respuestas Posibles**

- **200 OK**: Devuelve el perfil del usuario actualizado.
- **400 Bad Request**: Validación fallida (no es un correo electrónico válido).
- **409 Conflict**: El correo ya está registrado en la base de datos o en Supabase Auth.
- **500 Internal Server Error**: Fallo al contactar con el proveedor de autenticación.

---

## 8. Eliminar Cuenta Definitivamente

Borra el perfil de la base de datos local y purga completamente el usuario de Supabase Auth, revocando todo acceso a la cuenta.

**Signatura**
`DELETE /users/me`

**Parámetros/Cuerpo**
Ninguno.

**Respuestas Posibles**

- **204 No Content**: La cuenta se eliminó exitosamente.
- **401 Unauthorized**: Falta de autenticación.
- **500 Internal Server Error**: Si la cuenta se borró de la BD pero hubo un fallo borrándola de Supabase Auth.