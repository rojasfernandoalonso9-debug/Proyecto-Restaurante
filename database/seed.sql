-- Datos ficticios para desarrollo local. Cambiar las contraseñas al iniciar el backend.
USE restaurant_management;
SET NAMES utf8mb4;
START TRANSACTION;

INSERT INTO roles (id, name, description) VALUES
  (1, 'ADMINISTRADOR', 'Administración completa del sistema'),
  (2, 'MESERO', 'Atención de mesas y gestión de pedidos'),
  (3, 'COCINA', 'Preparación y actualización de pedidos')
ON DUPLICATE KEY UPDATE description = VALUES(description);

-- Contraseña de demostración para todas las cuentas: password
-- Hash BCrypt válido de ejemplo; solo para base local desechable.
INSERT INTO users (id, role_id, full_name, username, email, password_hash, active) VALUES
  (1, 1, 'Alex Rivera', 'admin', 'admin@example.test', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', TRUE),
  (2, 2, 'Sofía Torres', 'sofia', 'sofia@example.test', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', TRUE),
  (3, 2, 'Mateo Cruz', 'mateo', 'mateo@example.test', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', TRUE),
  (4, 3, 'Camila Vega', 'camila', 'camila@example.test', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', TRUE)
ON DUPLICATE KEY UPDATE full_name = VALUES(full_name), role_id = VALUES(role_id), active = VALUES(active);

INSERT INTO restaurant_tables (id, table_number, capacity, status) VALUES
  (1, 1, 2, 'OCCUPIED'), (2, 2, 2, 'AVAILABLE'), (3, 3, 4, 'OCCUPIED'),
  (4, 4, 4, 'AVAILABLE'), (5, 5, 4, 'RESERVED'), (6, 6, 6, 'AVAILABLE'),
  (7, 7, 6, 'AVAILABLE'), (8, 8, 2, 'AVAILABLE'), (9, 9, 8, 'AVAILABLE'),
  (10, 10, 4, 'AVAILABLE')
ON DUPLICATE KEY UPDATE capacity = VALUES(capacity), status = VALUES(status);

INSERT INTO categories (id, name, description, active) VALUES
  (1, 'Entradas', 'Para compartir y abrir el apetito', TRUE),
  (2, 'Platos principales', 'Platos preparados al momento', TRUE),
  (3, 'Pizzas', 'Pizzas artesanales', TRUE),
  (4, 'Postres', 'Postres de la casa', TRUE),
  (5, 'Bebidas', 'Bebidas frías y calientes', TRUE)
ON DUPLICATE KEY UPDATE description = VALUES(description), active = VALUES(active);

INSERT INTO products (id, category_id, sku, name, description, price, available) VALUES
  (1, 1, 'ENT-001', 'Bruschetta clásica', 'Pan tostado, tomate y albahaca', 18.00, TRUE),
  (2, 1, 'ENT-002', 'Tequeños de queso', 'Seis unidades con salsa de la casa', 22.00, TRUE),
  (3, 1, 'ENT-003', 'Ensalada caprese', 'Tomate, mozzarella y pesto', 25.00, TRUE),
  (4, 1, 'ENT-004', 'Sopa del día', 'Preparación fresca del día', 16.00, TRUE),
  (5, 2, 'PLA-001', 'Lomo saltado', 'Lomo, cebolla, tomate y papas', 42.00, TRUE),
  (6, 2, 'PLA-002', 'Pollo a la parrilla', 'Pechuga con guarnición', 36.00, TRUE),
  (7, 2, 'PLA-003', 'Risotto de hongos', 'Arroz cremoso con hongos', 39.00, TRUE),
  (8, 2, 'PLA-004', 'Hamburguesa de la casa', 'Carne, queso y papas', 34.00, TRUE),
  (9, 2, 'PLA-005', 'Pasta al pesto', 'Pasta fresca con pesto', 32.00, TRUE),
  (10, 2, 'PLA-006', 'Ají de gallina', 'Receta tradicional con arroz', 35.00, TRUE),
  (11, 3, 'PIZ-001', 'Pizza margarita', 'Tomate, mozzarella y albahaca', 32.00, TRUE),
  (12, 3, 'PIZ-002', 'Pizza pepperoni', 'Mozzarella y pepperoni', 38.00, TRUE),
  (13, 3, 'PIZ-003', 'Pizza cuatro quesos', 'Selección de cuatro quesos', 42.00, TRUE),
  (14, 3, 'PIZ-004', 'Pizza vegetariana', 'Verduras de temporada', 36.00, TRUE),
  (15, 4, 'POS-001', 'Tiramisú', 'Postre italiano de café', 18.00, TRUE),
  (16, 4, 'POS-002', 'Cheesecake', 'Cheesecake con frutos rojos', 17.00, TRUE),
  (17, 4, 'POS-003', 'Brownie con helado', 'Brownie tibio y helado', 19.00, TRUE),
  (18, 4, 'POS-004', 'Crema volteada', 'Postre clásico de caramelo', 14.00, TRUE),
  (19, 5, 'BEB-001', 'Limonada', 'Limonada fresca de la casa', 12.00, TRUE),
  (20, 5, 'BEB-002', 'Chicha morada', 'Bebida tradicional', 10.00, TRUE)
ON DUPLICATE KEY UPDATE category_id = VALUES(category_id), name = VALUES(name),
  description = VALUES(description), price = VALUES(price), available = VALUES(available);

INSERT INTO supplies (id, sku, name, unit, stock_quantity, minimum_stock, active) VALUES
  (1, 'INS-001', 'Tomate', 'kg', 10.000, 3.000, TRUE),
  (2, 'INS-002', 'Mozzarella', 'kg', 6.000, 2.000, TRUE),
  (3, 'INS-003', 'Harina', 'kg', 15.000, 4.000, TRUE),
  (4, 'INS-004', 'Pechuga de pollo', 'kg', 8.000, 2.000, TRUE),
  (5, 'INS-005', 'Lomo de res', 'kg', 1.500, 2.000, TRUE),
  (6, 'INS-006', 'Limón', 'kg', 5.000, 1.500, TRUE),
  (7, 'INS-007', 'Café', 'kg', 2.500, 0.500, TRUE),
  (8, 'INS-008', 'Aceite vegetal', 'L', 4.000, 1.000, TRUE)
ON DUPLICATE KEY UPDATE name = VALUES(name), unit = VALUES(unit),
  stock_quantity = VALUES(stock_quantity), minimum_stock = VALUES(minimum_stock), active = VALUES(active);

INSERT INTO orders (id, order_number, table_id, waiter_id, status, notes, subtotal, total, opened_at, closed_at) VALUES
  (1, 'ORD-2026-0001', 1, 2, 'IN_PREPARATION', 'Sin cebolla en el plato principal', 54.00, 54.00, CURRENT_TIMESTAMP - INTERVAL 18 MINUTE, NULL),
  (2, 'ORD-2026-0002', 3, 3, 'PENDING', NULL, 44.00, 44.00, CURRENT_TIMESTAMP - INTERVAL 6 MINUTE, NULL),
  (3, 'ORD-2026-0003', 2, 2, 'DELIVERED', NULL, 60.00, 60.00, CURRENT_TIMESTAMP - INTERVAL 90 MINUTE, CURRENT_TIMESTAMP - INTERVAL 50 MINUTE)
ON DUPLICATE KEY UPDATE table_id = VALUES(table_id), waiter_id = VALUES(waiter_id),
  status = VALUES(status), notes = VALUES(notes), subtotal = VALUES(subtotal), total = VALUES(total), closed_at = VALUES(closed_at);

INSERT INTO order_details (id, order_id, product_id, quantity, unit_price, notes) VALUES
  (1, 1, 5, 1, 42.00, 'Sin cebolla'), (2, 1, 19, 1, 12.00, NULL),
  (3, 2, 2, 2, 22.00, NULL),
  (4, 3, 6, 1, 36.00, NULL), (5, 3, 20, 1, 10.00, NULL), (6, 3, 18, 1, 14.00, NULL)
ON DUPLICATE KEY UPDATE quantity = VALUES(quantity), unit_price = VALUES(unit_price), notes = VALUES(notes);

INSERT INTO sales (id, order_id, cashier_id, payment_method, amount, status, paid_at) VALUES
  (1, 3, 1, 'CARD', 60.00, 'PAID', CURRENT_TIMESTAMP - INTERVAL 50 MINUTE)
ON DUPLICATE KEY UPDATE cashier_id = VALUES(cashier_id), payment_method = VALUES(payment_method), amount = VALUES(amount), status = VALUES(status);

INSERT INTO sale_details (id, sale_id, product_id, product_name, quantity, unit_price) VALUES
  (1, 1, 6, 'Pollo a la parrilla', 1, 36.00),
  (2, 1, 20, 'Chicha morada', 1, 10.00),
  (3, 1, 18, 'Crema volteada', 1, 14.00)
ON DUPLICATE KEY UPDATE product_id = VALUES(product_id), product_name = VALUES(product_name),
  quantity = VALUES(quantity), unit_price = VALUES(unit_price);

INSERT INTO inventory_movements (id, supply_id, user_id, movement_type, quantity, stock_before, stock_after, reason) VALUES
  (1, 1, 1, 'IN', 10.000, 0.000, 10.000, 'Carga inicial de inventario'),
  (2, 2, 1, 'IN', 6.000, 0.000, 6.000, 'Carga inicial de inventario'),
  (3, 3, 1, 'IN', 15.000, 0.000, 15.000, 'Carga inicial de inventario'),
  (4, 4, 1, 'IN', 8.000, 0.000, 8.000, 'Carga inicial de inventario'),
  (5, 5, 1, 'IN', 5.000, 0.000, 5.000, 'Carga inicial de inventario'),
  (6, 5, 1, 'OUT', 3.500, 5.000, 1.500, 'Consumo de cocina'),
  (7, 6, 1, 'IN', 5.000, 0.000, 5.000, 'Carga inicial de inventario'),
  (8, 7, 1, 'IN', 2.500, 0.000, 2.500, 'Carga inicial de inventario'),
  (9, 8, 1, 'IN', 4.000, 0.000, 4.000, 'Carga inicial de inventario')
ON DUPLICATE KEY UPDATE reason = VALUES(reason);

COMMIT;
