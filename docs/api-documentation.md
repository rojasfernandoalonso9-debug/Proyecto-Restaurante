# API REST

Base local: `http://localhost:8080/api`. Las rutas privadas requieren `Authorization: Bearer <token>`. JSON usa enums en inglés (por ejemplo `IN_PREPARATION`) y el cliente presenta etiquetas en español.

## Sesión

| Método | Ruta | Acceso | Descripción |
| --- | --- | --- | --- |
| `POST` | `/auth/login` | Público | `{ "username": "admin", "password": "password" }`; responde token y perfil. |
| `GET` | `/auth/me` | Autenticado | Perfil actual. |
| `GET` | `/health` | Público | Estado del proceso. |

El cierre de sesión es del lado del cliente: descarta el token. Los JWT emitidos siguen siendo válidos hasta expirar.

## Operación

| Método | Ruta | Roles | Descripción |
| --- | --- | --- | --- |
| `GET` | `/dashboard` | Admin, mesero | Métricas, siete días de ventas, productos y actividad. |
| `GET/POST/PUT/DELETE` | `/tables[/{id}]` | Consulta autenticada; escritura admin | CRUD de mesas. El borrado respeta el historial. |
| `GET/POST/PUT` | `/categories[/{id}]` | Consulta autenticada; escritura admin | Categorías. |
| `GET/POST/PUT/DELETE` | `/products[/{id}]` | Consulta autenticada; escritura admin | `GET /products?availableOnly=true`; DELETE desactiva. |
| `GET/POST` | `/users` | Admin | Consultar y crear usuarios. |
| `PATCH` | `/users/{id}/active` | Admin | Activar/desactivar; no permite desactivar la cuenta propia. |
| `GET/POST/PATCH` | `/orders[/{id}/status]` | Admin/mesero | Listar, crear y avanzar/cancelar pedidos propios. |
| `GET` | `/orders/kitchen` | Admin/cocina | Pedidos pendientes o en preparación. |
| `GET/POST/PUT` | `/inventory/supplies[/{id}]` | Lectura admin/cocina; escritura admin | Catálogo de insumos y mínimo. El stock inicial genera entrada; el saldo existente se cambia solo mediante movimientos. |
| `GET/POST` | `/inventory/movements` | Admin/cocina | Historial y movimiento `IN`, `OUT` o `ADJUSTMENT`; respuesta incluye saldos antes y después. En ajuste, `quantity` de entrada es el nuevo saldo. |
| `GET` | `/sales` | Admin | Últimas 100 ventas con sus líneas. |
| `POST` | `/orders/{id}/payment` | Admin/mesero | `{ "paymentMethod": "CARD" }`; solo pedidos entregados, un pago por pedido. |
| `GET` | `/reports?period=DAY\|WEEK\|MONTH` | Admin | Totales, ventas diarias, top productos y meseros. |
| `GET` | `/reports/export?period=MONTH` | Admin | CSV UTF-8 compatible con Excel. |

## Crear un pedido

```json
{
  "tableId": 2,
  "notes": "Sin hielo",
  "items": [
    { "productId": 19, "quantity": 2, "notes": "" },
    { "productId": 5, "quantity": 1, "notes": "Punto medio" }
  ]
}
```

El servidor verifica disponibilidad, copia precios vigentes a las líneas, calcula subtotal/total y marca la mesa ocupada dentro de una transacción.

## Errores

Los errores de validación devuelven HTTP 400 con `fields`; un recurso inexistente devuelve 404; conflictos de estado o integridad devuelven 409; credenciales incorrectas devuelven 401. Un token válido sin el rol requerido recibe 403.
