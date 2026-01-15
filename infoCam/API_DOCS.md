# Documentación de la API Rest - Tráfico Euskadi

Este documento detalla todos los **Endpoints** disponibles en la API actual.
La URL base para todas las peticiones en local es: `http://localhost:8080`

> **Nota Importante:** Esta API no soporta "Incidencias Favoritas" debido a restricciones del modelo de datos.

---

## 1. Autenticación (`/api/auth`)

| Método | Endpoint | Descripción | Cuerpo JSON Ejemplo |
| :--- | :--- | :--- | :--- |
| **POST** | `/api/auth/registro` | Registrar un nuevo usuario | `{"username": "usu1", "password": "123", "nombre": "User", "email": "u@mail.com"}` |
| **POST** | `/api/auth/login` | Iniciar sesión | `{"username": "usu1", "password": "123"}` |

---

## 2. Usuarios (`/api/usuarios`)

### Operaciones Públicas / Usuario Propio
| Método | Endpoint | Descripción |
| :--- | :--- | :--- |
| **GET** | `/api/usuarios/{id}` | Ver perfil de usuario. |
| **PUT** | `/api/usuarios/{id}` | Actualizar perfil (nombre, email, etc). |
| **GET** | `/api/usuarios/{id}/favoritos/camaras` | Listar cámaras favoritas. |

### Operaciones de Administración (Admin Only)
| Método | Endpoint | Descripción |
| :--- | :--- | :--- |
| **GET** | `/api/usuarios` | Listar **todos** los usuarios del sistema. |
| **POST** | `/api/usuarios` | Crear usuario manualmente. |
| **DELETE** | `/api/usuarios/{id}` | Eliminar un usuario. |

---

## 3. Cámaras (`/api/camaras`)

| Método | Endpoint | Descripción | Parámetros (Query) |
| :--- | :--- | :--- | :--- |
| **GET** | `/api/camaras` | Listar todas las cámaras. | - |
| **GET** | `/api/camaras/cercanas` | Buscar por cercanía. | `?lat=43.2&lon=-2.9&distancia=0.05` |
| **GET** | `/api/camaras/{id}` | Ver detalle cámara. | - |
| **POST** | `/api/camaras/{id}/favorita`| Añadir/Quitar favoritos. | `?usuarioId=1` |

### Administración
- **POST** `/api/camaras`: Crear cámara.
- **PUT** `/api/camaras/{id}`: Editar cámara.
- **DELETE** `/api/camaras/{id}`: Borrar cámara.

---

## 4. Incidencias (`/api/incidencias`)

| Método | Endpoint | Descripción | Parámetros / Notas |
| :--- | :--- | :--- | :--- |
| **GET** | `/api/incidencias` | Listar todas las incidencias. | - |
| **GET** | `/api/incidencias/cercanas`| Buscar por cercanía. | `?lat=43.2&lon=-2.9&distancia=0.05` |
| **POST** | `/api/incidencias` | Crear incidencia (Reporte). | JSON body requerido. |

### Administración
- **PUT** `/api/incidencias/{id}`: Editar incidencia.
- **DELETE** `/api/incidencias/{id}`: Borrar incidencia.

---

## 5. Sincronización (`/api/sync`)
| Método | Endpoint | Descripción |
| :--- | :--- | :--- |
| **GET** | `/api/sync` | Ejecutar sincronización manual (simulación). |
