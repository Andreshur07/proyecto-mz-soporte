# Sistema de Gestión Colaborativa de Solicitudes de Soporte

Backend para gestionar solicitudes de soporte con autenticación JWT y autorización por roles. Actualmente implementa únicamente el Sprint 1: autenticación, creación y consulta de solicitudes, priorización y trazabilidad del cambio de prioridad.

## Stack

- Java 17
- Spring Boot 3.5
- Maven
- Spring Web, Data JPA, Security y Validation
- PostgreSQL
- JWT con firma HMAC

## Requisitos y configuración

- JDK 17 o superior
- PostgreSQL en ejecución
- Base de datos `soporte_colaborativo`

Variables de entorno:

```powershell
$env:DB_URL = "jdbc:postgresql://localhost:5432/soporte_colaborativo"
$env:DB_USERNAME = "postgres"
$env:DB_PASSWORD = "contraseña_de_postgresql"
$env:JWT_SECRET = "secreto_base64_de_al_menos_256_bits"
$env:JWT_EXPIRATION = "3600000"
```

`JWT_EXPIRATION` se expresa en milisegundos. Existe una clave predeterminada exclusivamente para desarrollo; en cualquier entorno compartido o productivo se debe definir `JWT_SECRET` con un secreto Base64 propio.

## Iniciar el backend

```powershell
.\backend\mvnw.cmd -f backend\pom.xml spring-boot:run
```

El servicio se inicia en `http://localhost:8080`.

## Usuarios de desarrollo

La inicialización es idempotente, está habilitada mediante `DEV_SEED_ENABLED=true` y almacena las contraseñas con BCrypt. Para deshabilitarla, definir `DEV_SEED_ENABLED=false`.

Contraseña exclusiva de desarrollo para los cuatro usuarios: `Marz2026!`

| Correo | Rol |
|---|---|
| `solicitante@marz.local` | `SOLICITANTE` |
| `agente@marz.local` | `AGENTE` |
| `coordinador@marz.local` | `COORDINADOR` |
| `auditor@marz.local` | `AUDITOR` |

## Endpoints implementados

| Método | Ruta | Acceso |
|---|---|---|
| GET | `/api/health` | Público |
| POST | `/api/auth/login` | Público |
| POST | `/api/solicitudes` | `SOLICITANTE` |
| GET | `/api/solicitudes/mias` | `SOLICITANTE` |
| GET | `/api/solicitudes/{id}` | `SOLICITANTE`, solo propietario |
| GET | `/api/solicitudes` | `COORDINADOR` |
| PATCH | `/api/solicitudes/{id}/prioridad` | `COORDINADOR` |

El cierre de sesión es stateless: el frontend debe eliminar el JWT almacenado. El backend no mantiene sesiones ni tokens de sesión.

## Pruebas

```powershell
.\backend\mvnw.cmd -f backend\pom.xml test
```
