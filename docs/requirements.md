# Requisitos del sistema

## Objetivo

Administrar las tareas diarias de un restaurante desde una aplicación web con control de acceso por responsabilidades y datos persistidos en MySQL.

## Roles

| Rol | Responsabilidades principales |
| --- | --- |
| `ADMINISTRADOR` | Catálogos, usuarios, mesas, inventario, ventas, dashboard y reportes. |
| `MESERO` | Consultar mesas, crear pedidos, enviarlos a cocina, entregarlos y registrar pagos de sus pedidos. |
| `COCINA` | Consultar comandas e insumos, avanzar preparación y registrar movimientos de inventario. |

## Requisitos funcionales

1. Autenticar usuarios con JWT y almacenar contraseñas con BCrypt.
2. Consultar indicadores del día, actividad reciente y productos vendidos.
3. Registrar, editar y consultar mesas; impedir su borrado si ya tienen historial.
4. Administrar categorías y productos con precio, imagen opcional y disponibilidad.
5. Crear pedidos con una mesa disponible, cantidades, notas y responsable; conservar precio histórico.
6. Mantener el flujo `PENDING → IN_PREPARATION → READY → DELIVERED` y permitir cancelación antes de completar.
7. Consultar comandas abiertas desde cocina y medir tiempo desde la apertura.
8. Administrar insumos, entradas, salidas y ajustes; impedir salidas por encima del saldo.
9. Registrar un único pago simulado por pedido entregado, con efectivo, tarjeta o transferencia.
10. Consultar ventas diarias, semanales y mensuales; agrupar por producto y mesero y exportar CSV.

## Requisitos de calidad

- Validar solicitudes en el servidor y devolver errores HTTP coherentes.
- Mantener integridad referencial con claves foráneas y transacciones en pedidos, pagos y movimientos.
- Evitar exponer hashes de contraseñas en respuestas REST.
- Mantener secretos fuera del repositorio y ofrecer una interfaz responsive.
- La interfaz consume la API; no usa arreglos locales como fuente de datos de negocio.

## Límites conocidos

- No se integra con bancos ni pasarelas de pago.
- El reporte exportable es CSV compatible con Excel; no se genera PDF.
- El esquema de inventario registra insumos y movimientos, pero todavía no define recetas o consumo automático de insumos por producto.
- El acceso de demostración usa una clave sencilla y no debe publicarse.
