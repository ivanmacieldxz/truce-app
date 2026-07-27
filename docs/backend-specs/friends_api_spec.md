# Especificación de Endpoints - Módulo de Amistades (Friends)

Todos los endpoints están protegidos por autenticación JWT. Incluir en las peticiones el encabezado:
`Authorization: Bearer <token>`

Prefijo base: `/api/v1`

---

## 1. Listar Amigos (Aprobados)

Obtiene la lista de amigos con los que el usuario tiene una relación con estado `ACCEPTED`.

**Signatura**
`GET /friends`

**Parámetros (Query Params)**
- `page` (number, opcional, por defecto: `1`)
- `limit` (number, opcional, por defecto: `20`)

**Cuerpo (Body)**
Ninguno.

**Respuestas Posibles**
- **200 OK**
  Devuelve una lista de `FriendDto`.
  ```json
  [
    {
      "id": "uuid-de-la-relacion-friendship",
      "friendId": "uuid-del-usuario-amigo",
      "username": "juanperez",
      "createdAt": "2026-07-26T12:00:00Z"
    }
  ]
  ```

---

## 2. Listar Solicitudes de Amistad

Obtiene la lista de solicitudes de amistad en estado `PENDING`.

**Signatura**
`GET /friends/requests`

**Parámetros (Query Params)**
- `type` (string, opcional): Filtrar por tipo. Valores: `"incoming"` (entrantes a aceptar) u `"outgoing"` (enviadas esperando respuesta).
- `page` (number, opcional, por defecto: `1`)
- `limit` (number, opcional, por defecto: `20`)

**Respuestas Posibles**
- **200 OK**
  Devuelve una lista de `FriendshipRequestDto`.
  ```json
  [
    {
      "id": "uuid-de-la-solicitud-friendship",
      "userId": "uuid-del-otro-usuario",
      "username": "maria123",
      "type": "INCOMING", 
      "createdAt": "2026-07-26T13:00:00Z"
    }
  ]
  ```

---

## 3. Enviar Solicitud de Amistad

Envía una solicitud a un usuario. Si la solicitud había sido rechazada antes, se vuelve a abrir pasándola a `PENDING`.

**Signatura**
`POST /friends/requests`

**Cuerpo (Body)**
Se espera el `CreateFriendshipDto`.
```json
{
  "targetUserId": "uuid-del-usuario-a-agregar"
}
```

**Respuestas Posibles**
- **201 Created**: Solicitud creada exitosamente. Devuelve el objeto de la base de datos.
- **400 Bad Request**: Si intentas agregarte a ti mismo.
- **409 Conflict**: Si ya son amigos o si ya existe una solicitud pendiente.

---

## 4. Responder Solicitud de Amistad

Acepta o rechaza una solicitud de amistad entrante. **Solo el usuario receptor puede realizar esta acción.**

**Signatura**
`PATCH /friends/requests/:id`

**Parámetros (URL Params)**
- `id` (string, UUID): El ID de la relación (`Friendship.id`), no del usuario.

**Cuerpo (Body)**
Se espera el `UpdateFriendshipDto`.
```json
{
  "status": "ACCEPTED" // Valores permitidos: "ACCEPTED" o "REJECTED"
}
```

**Respuestas Posibles**
- **200 OK**: Solicitud actualizada.
- **400 Bad Request**: Si la solicitud ya no está pendiente o si intentas responder una solicitud que tú enviaste.
- **404 Not Found**: Si la solicitud no existe.

---

## 5. Eliminar un Amigo

Elimina a un amigo de la lista de contactos. La relación debe estar previamente aceptada.

**Signatura**
`DELETE /friends/:friendId`

**Parámetros (URL Params)**
- `friendId` (string, UUID): El ID del **usuario amigo** (`User.id`), no el ID de la relación.

**Respuestas Posibles**
- **204 No Content**: Amigo eliminado exitosamente (no devuelve body).
- **404 Not Found**: Si la amistad no existe o no estaba aceptada.
