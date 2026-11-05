# La Mesa · Restaurant OS

Aplicación web para organizar el servicio de un restaurante: salón, pedidos, cocina, carta, inventario, ventas y reportes. Este proyecto de portafolio usa un backend Java/Spring Boot, React/Vite y MySQL.

## Funcionalidades

- Inicio/cierre de sesión con JWT y roles `ADMINISTRADOR`, `MESERO` y `COCINA`.
- Dashboard conectado a pagos, pedidos recientes, mesas y productos vendidos.
- Gestión de mesas, categorías y productos.
- Pedidos asociados a mesa y mesero, cálculo de importes y flujo de cocina.
- Panel de cocina con tiempo transcurrido y cambios de estado.
- Insumos, alertas por stock mínimo y movimientos auditados.
- Registro simulado de pagos en efectivo, tarjeta o transferencia; no procesa pagos reales.
- Reportes por día, semana y mes, rendimiento de meseros y descarga CSV compatible con Excel.
- Contraseñas BCrypt, validación de solicitudes, DTOs, límites por rol y claves foráneas.

## Tecnologías

- Java 21, Spring Boot 3, Maven, Spring Web, Data JPA, Security, Validation y JJWT.
- React, JavaScript, Vite, React Router, Axios, Tailwind CSS, Lucide React y Recharts.
- MySQL 8.0.16+ e InnoDB.

## Arquitectura y estructura

```text
backend/src/main/java/com/restaurant/management/
  config/ controller/ dto/ entity/ exception/ repository/ security/ service/
frontend/src/
  assets/ components/ context/ hooks/ layouts/ pages/ routes/ services/ utils/
database/       schema.sql, seed.sql
docs/           requisitos, diseño, arquitectura y contrato REST
```

La API aplica la separación controller → service → repository. El frontend usa rutas protegidas, contexto de autenticación y un cliente Axios compartido.

## Requisitos

- JDK 21 y Maven 3.9 o posterior.
- Node.js 20 o posterior con npm.
- MySQL 8.0.16 o posterior.

## Instalación local

1. Crear las tablas y cargar los datos ficticios desde la raíz del repositorio:

   ```sh
   mysql -u root -p < database/schema.sql
   mysql -u root -p restaurant_management < database/seed.sql
   ```

   En MySQL, crea después un usuario exclusivo de la aplicación y dale permisos DML sobre la base. Sustituye la clave por una local:

   ```sql
   CREATE USER 'restaurant_app'@'localhost' IDENTIFIED BY 'cambia_esta_clave';
   GRANT SELECT, INSERT, UPDATE, DELETE ON restaurant_management.* TO 'restaurant_app'@'localhost';
   ```

2. Copiar `.env.example` a `.env` como referencia y configurar las variables en el entorno del proceso. Spring Boot no carga automáticamente archivos `.env`.

   ```text
   DB_URL=jdbc:mysql://localhost:3306/restaurant_management?serverTimezone=UTC
   DB_USERNAME=restaurant_app
   DB_PASSWORD=tu_clave_local
   JWT_SECRET=una_clave_local_aleatoria_de_al_menos_32_caracteres
   JWT_EXPIRATION_MS=3600000
   ```

   Usa un usuario MySQL con permisos limitados sobre `restaurant_management`; no uses la cuenta `root` desde la aplicación. `JWT_SECRET` debe contener al menos 32 bytes. No guardes `.env` en Git.

3. Iniciar la API:

   ```sh
   cd backend
   mvn spring-boot:run
   ```

   La API queda en `http://localhost:8080`; el endpoint público de estado es `/api/health`.

4. Instalar y ejecutar el frontend:

   ```sh
   cd frontend
   npm install
   npm run dev
   ```

   Abre la URL que indique Vite. El proxy de desarrollo envía `/api` a `http://localhost:8080`.

## Acceso de demostración

El seed carga `admin`, `sofia`, `mateo` y `camila`. La contraseña de demostración es `password` para las cuatro cuentas. Son cuentas ficticias solo para una base local desechable: cámbialas antes de cualquier despliegue.

## Verificación

```sh
cd backend && mvn test
cd frontend && npm run build
```

Estado de verificación en este entorno:

- La API y las fuentes de prueba compilan con Java 24 usando `mvn test` hasta la fase de ejecución de pruebas.
- La ejecución de pruebas con Surefire queda pendiente porque falta en la caché local `surefire-junit-platform:3.5.3` y este entorno no tiene acceso a la red.
- No se pudo compilar ni iniciar el frontend: Node.js/npm no están instalados aquí.
- No se pudo iniciar la aplicación completa: MySQL no está instalado/configurado aquí.

Para ejecutarla localmente, instala las versiones indicadas en **Requisitos**, configura MySQL y las variables de entorno, y sigue **Instalación local**. Desde la raíz, abre dos terminales: una para `cd backend; mvn spring-boot:run` y otra para `cd frontend; npm install; npm run dev`.
## Documentación

- [Requisitos](docs/requirements.md)
- [Diseño de base de datos](docs/database-design.md)
- [Arquitectura](docs/architecture.md)
- [API REST](docs/api-documentation.md)
- [Preparación de despliegue](docs/deployment.md)

## Capturas

Pendientes de incorporar cuando se ejecute la aplicación con una base de datos local.
