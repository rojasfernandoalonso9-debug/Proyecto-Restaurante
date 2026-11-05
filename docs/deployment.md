# Preparación de despliegue

Este documento describe preparación, no un despliegue ejecutado.

1. Configurar MySQL administrado y aplicar esquema y migraciones de forma controlada. No cargar `seed.sql` en producción.
2. Crear credenciales de base de datos con privilegios mínimos y cargar `DB_URL`, `DB_USERNAME` y `DB_PASSWORD` desde un gestor de secretos.
3. Generar un `JWT_SECRET` aleatorio de más de 32 bytes y configurar expiración breve apropiada para el entorno.
4. Reemplazar las cuentas de demostración y validar políticas de contraseñas y recuperación antes de exponer el servicio.
5. Compilar backend con JDK 21/Maven y frontend con Node/npm; guardar artefactos y resultados de pruebas.
6. Publicar backend y frontend detrás de TLS. Configurar un proxy de `/api`, límites de petición y orígenes CORS exactos.
7. Configurar health checks, logs sin tokens/secretos, backups y monitorización.
8. Ejecutar pruebas de autorización por rol y flujos de pedido, stock y pago en un entorno de staging antes de abrir tráfico.

La base de datos de demostración y el secreto de muestra no son aptos para despliegue.
