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

## 2. Actualizar mi perfil

Permite actualizar de forma parcial los datos del usuario. Es comúnmente utilizado para registrar o refrescar el token de Firebase Cloud Messaging (`fcmToken`) desde el cliente móvil.

**Signatura**
`PATCH /users/me`

**Parámetros**
Ninguno.

**Cuerpo (Body)**
Se espera el DTO `UpdateUserDto`. Todos los campos son opcionales, pero los que se envíen serán validados estrictamente.
```json
{
  "email": "nuevo_email@ejemplo.com", // Opcional, debe ser un email válido
  "username": "nuevo_usuario",        // Opcional, mínimo 3 caracteres
  "fcmToken": "nuevo-fcm-token"       // Opcional
}
```

**Respuestas Posibles**

- **200 OK**
  Devuelve el objeto `UserDto` actualizado.
- **400 Bad Request**
  Si los datos enviados no pasan las reglas de validación (ej. el email no tiene un formato válido o el username es muy corto).
- **401 Unauthorized**
  Si el token es inválido o no existe.
- **409 Conflict**
  Si se intenta actualizar el `email` o `username` a uno que ya pertenece a otro usuario en la plataforma.

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
