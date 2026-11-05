# Diseño de base de datos

Base relacional MySQL 8.0.16+ con InnoDB, `utf8mb4` e integridad referencial. El script ejecutable es [`../database/schema.sql`](../database/schema.sql); los datos locales están en [`../database/seed.sql`](../database/seed.sql).

## Entidades y relaciones

- `roles` 1:N `users`: cada usuario tiene un rol principal.
- `users` 1:N `orders`: el mesero responsable queda asociado al pedido.
- `restaurant_tables` 1:N `orders`: una mesa conserva su historial de pedidos.
- `categories` 1:N `products`.
- `orders` 1:N `order_details` y `products` 1:N `order_details`: resuelve la relación N:M entre pedidos y productos. El precio unitario se copia al detalle para conservar el precio acordado aunque cambie el catálogo.
- `supplies` 1:N `inventory_movements`; cada movimiento registra el usuario que lo hizo.
- `orders` 1:0..1 `sales`: la clave única de `sales.order_id` impide cobrar dos veces el mismo pedido.
- `sales` 1:N `sale_details`: el nombre y precio son una instantánea histórica; `product_id` puede quedar nulo si luego se elimina el producto.

## Reglas e índices

Las claves foráneas restringen borrados que romperían historial. Los catálogos se desactivan en vez de eliminarse. Los importes usan `DECIMAL`, no punto flotante; cantidades de inventario admiten tres decimales. Las restricciones `CHECK` y los índices cubren importes, cantidades, estado y consultas habituales por fecha, usuario y disponibilidad.

`orders.subtotal` y `orders.total` son acumulados mantenidos por la capa de servicio dentro de una transacción junto con sus detalles. La columna `supplies.stock_quantity` es el saldo actual; toda modificación debe registrar simultáneamente un movimiento, también dentro de una transacción. Cada movimiento conserva cantidad y saldos anterior/posterior; para un ajuste, la cantidad es la diferencia y el saldo posterior es el nuevo valor. La unicidad de detalle por pedido y producto prohíbe duplicados; el API rechaza líneas de producto repetidas para que el cliente envíe una sola cantidad por producto.

Los estados de mesa son operativos y los estados de pedido son el flujo de cocina y entrega. La capa de servicio debe mantenerlos consistentes al abrir, cancelar y cerrar pedidos. El script no modela integración de pagos ni proveedores externos.

## Datos de demostración

`seed.sql` agrega roles, un administrador, dos meseros, una cuenta de cocina, diez mesas, cinco categorías, veinte productos, ocho insumos y pedidos/movimientos de ejemplo. Los nombres y correos usan datos ficticios. La contraseña indicada en el seed es solo para un entorno local desechable; sustituirla antes de desplegar y no reutilizarla.
