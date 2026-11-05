# Arquitectura

## Vista general

```text
Navegador (React / Vite)
  ├── React Router y protección de vistas por rol
  ├── Contexto AuthContext (perfil y token JWT)
  └── Axios → /api
                 ↓
Spring Boot REST API
  controller → service → repository → entidades JPA
                 ↓
              MySQL
```

En desarrollo, Vite reenvía `/api` a Spring Boot en el puerto 8080. En despliegue, el proxy inverso debe servir la aplicación y reenviar `/api` al backend bajo el mismo origen o configurar CORS para los orígenes permitidos.

## Backend

- `controller`: rutas HTTP, validación de entrada y autorización declarativa.
- `service`: reglas de transición, importes, existencias, pagos y reportes; operaciones de escritura usan transacciones.
- `repository`: interfaces Spring Data JPA y consultas agregadas.
- `entity`: entidades que corresponden al esquema SQL.
- `dto`: solicitudes y respuestas REST; evita serializar entidades sensibles.
- `security`: `UserDetailsService`, BCrypt, filtro JWT y servicio de tokens.
- `config`: cadena de seguridad y configuración de infraestructura.
- `exception`: excepciones de dominio y respuesta global de errores.

## Frontend

- `context`: sesión y perfil del usuario.
- `routes`: control de acceso de vistas según el rol.
- `layouts`: navegación de escritorio y móvil.
- `pages`: dashboard, salón, cocina, carta, inventario, ventas, usuarios y reportes.
- `services`: Axios, token Bearer y traducción común de errores.
- `components`: badges, formularios, estados y diálogos reutilizables.

La seguridad real depende de las reglas del backend; ocultar una ruta del cliente solo mejora la experiencia.

## Seguridad y operaciones

- JWT firmado con HS256; configura un secreto aleatorio de al menos 32 bytes mediante `JWT_SECRET`.
- Sesiones sin estado; al cerrar sesión el cliente elimina el token. Para invalidación inmediata de tokens se requeriría una lista de revocación o rotación de credenciales.
- Nunca incluyas secretos ni `.env` en Git.
- Ejecuta `schema.sql` y `seed.sql` explícitamente. Hibernate usa `ddl-auto: validate` y no modifica el esquema.
- Para producción: TLS, usuario MySQL de privilegios mínimos, secreto administrado fuera del artefacto, backups y una política de expiración/rotación de tokens.
