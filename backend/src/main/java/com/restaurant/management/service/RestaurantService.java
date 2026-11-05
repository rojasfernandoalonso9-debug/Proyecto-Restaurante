package com.restaurant.management.service;

import com.restaurant.management.dto.ApiDtos.*;
import com.restaurant.management.entity.*;
import com.restaurant.management.exception.ApiException;
import com.restaurant.management.repository.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class RestaurantService {
    private final RoleRepository roles;
    private final UserRepository users;
    private final RestaurantTableRepository tables;
    private final CategoryRepository categories;
    private final ProductRepository products;
    private final OrderRepository orders;
    private final OrderDetailRepository orderDetails;
    private final SupplyRepository supplies;
    private final InventoryMovementRepository movements;
    private final SaleRepository sales;
    private final SaleDetailRepository saleDetails;
    private final PasswordEncoder passwordEncoder;
    private static final ZoneId BUSINESS_ZONE = ZoneId.of("America/Lima");

    public RestaurantService(RoleRepository roles, UserRepository users, RestaurantTableRepository tables,
            CategoryRepository categories, ProductRepository products, OrderRepository orders,
            OrderDetailRepository orderDetails, SupplyRepository supplies, InventoryMovementRepository movements,
            SaleRepository sales, SaleDetailRepository saleDetails, PasswordEncoder passwordEncoder) {
        this.roles = roles; this.users = users; this.tables = tables; this.categories = categories;
        this.products = products; this.orders = orders; this.orderDetails = orderDetails; this.supplies = supplies;
        this.movements = movements; this.sales = sales; this.saleDetails = saleDetails; this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true) public List<RestaurantTable> tables() { return tables.findAll(); }
    @Transactional public RestaurantTable saveTable(Long id, TableRequest request) {
        RestaurantTable table = id == null ? new RestaurantTable() : tables.findById(id).orElseThrow(() -> ApiException.notFound("Mesa no encontrada."));
        table.setTableNumber(request.tableNumber()); table.setCapacity(request.capacity());
        if (request.status() != null && !request.status().isBlank()) table.setStatus(enumValue(RestaurantTable.Status.class, request.status(), "Estado de mesa inválido."));
        return tables.save(table);
    }
    @Transactional public void deleteTable(Long id) { tables.delete(tables.findById(id).orElseThrow(() -> ApiException.notFound("Mesa no encontrada."))); }

    @Transactional(readOnly = true) public List<Category> categories() { return categories.findAll(); }
    @Transactional public Category saveCategory(Long id, CategoryRequest request) {
        Category category = id == null ? new Category() : categories.findById(id).orElseThrow(() -> ApiException.notFound("Categoría no encontrada."));
        category.setName(request.name().trim()); category.setDescription(request.description());
        if (request.active() != null) category.setActive(request.active());
        return categories.save(category);
    }
    @Transactional(readOnly = true) public List<Product> products(boolean availableOnly) { return availableOnly ? products.findAvailableForOrder() : products.findAll(); }
    @Transactional public Product saveProduct(Long id, ProductRequest request) {
        Product product = id == null ? new Product() : products.findById(id).orElseThrow(() -> ApiException.notFound("Producto no encontrado."));
        product.setCategory(categories.findById(request.categoryId()).orElseThrow(() -> ApiException.notFound("Categoría no encontrada.")));
        product.setSku(request.sku().trim()); product.setName(request.name().trim()); product.setDescription(request.description());
        product.setPrice(request.price().setScale(2, RoundingMode.HALF_UP)); product.setImageUrl(request.imageUrl());
        if (request.available() != null) product.setAvailable(request.available());
        return products.save(product);
    }
    @Transactional public void deactivateProduct(Long id) {
        Product product = products.findById(id).orElseThrow(() -> ApiException.notFound("Producto no encontrado."));
        product.setAvailable(false);
    }

    @Transactional(readOnly = true) public List<UserResponse> users() {
        return users.findAll().stream().map(this::userResponse).toList();
    }
    @Transactional public UserResponse createUser(UserRequest request) {
        if (users.existsByUsername(request.username()) || users.existsByEmail(request.email())) throw ApiException.conflict("El usuario o correo ya existe.");
        User user = new User(); user.setFullName(request.fullName().trim()); user.setUsername(request.username().trim());
        user.setEmail(request.email().trim().toLowerCase(Locale.ROOT)); user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setRole(roles.findByName(request.role().trim().toUpperCase(Locale.ROOT)).orElseThrow(() -> ApiException.badRequest("Rol inválido.")));
        return userResponse(users.save(user));
    }
    @Transactional public UserResponse updateUserActive(Long id, boolean active, String actorUsername) {
        User target = users.findById(id).orElseThrow(() -> ApiException.notFound("Usuario no encontrado."));
        if (!active && target.getUsername().equals(actorUsername)) throw ApiException.conflict("No puedes desactivar tu propia cuenta.");
        target.setActive(active);
        return userResponse(target);
    }
    private UserResponse userResponse(User user) { return new UserResponse(user.getId(), user.getUsername(), user.getFullName(), user.getEmail(), user.getRole().getName(), user.isActive()); }

    @Transactional(readOnly = true) public List<OrderResponse> orders() { return orders.findAllByOrderByOpenedAtDesc().stream().map(this::orderResponse).toList(); }
    @Transactional(readOnly = true) public List<OrderResponse> kitchenOrders() {
        return orders.findByStatusInOrderByOpenedAtAsc(List.of(OrderStatus.PENDING, OrderStatus.IN_PREPARATION)).stream().map(this::orderResponse).toList();
    }
    @Transactional public OrderResponse createOrder(OrderRequest request, String username) {
        RestaurantTable table = tables.findByIdForUpdate(request.tableId()).orElseThrow(() -> ApiException.notFound("Mesa no encontrada."));
        if (table.getStatus() != RestaurantTable.Status.AVAILABLE) throw ApiException.conflict("La mesa no está disponible.");
        if (request.items().stream().map(OrderItemRequest::productId).distinct().count() != request.items().size())
            throw ApiException.badRequest("Incluye cada producto una sola vez y ajusta su cantidad.");
        User waiter = users.findByUsernameAndActiveTrue(username).orElseThrow(() -> ApiException.notFound("Usuario no encontrado."));
        if (!waiter.getRole().getName().equals("MESERO") && !waiter.getRole().getName().equals("ADMINISTRADOR")) throw ApiException.badRequest("El usuario no puede crear pedidos.");
        Order order = new Order(); order.setOrderNumber("ORD-" + Year.now(BUSINESS_ZONE) + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(Locale.ROOT));
        order.setTable(table); order.setWaiter(waiter); order.setNotes(request.notes()); order.setStatus(OrderStatus.PENDING);
        order = orders.save(order);
        BigDecimal total = BigDecimal.ZERO;
        for (OrderItemRequest item : request.items()) {
            Product product = products.findById(item.productId()).orElseThrow(() -> ApiException.notFound("Producto no encontrado: " + item.productId()));
            if (!product.isAvailable() || !product.getCategory().isActive()) throw ApiException.conflict("El producto " + product.getName() + " no está disponible.");
            OrderDetail detail = new OrderDetail(); detail.setOrder(order); detail.setProduct(product); detail.setQuantity(item.quantity());
            detail.setUnitPrice(product.getPrice()); detail.setNotes(item.notes()); orderDetails.save(detail);
            total = total.add(product.getPrice().multiply(BigDecimal.valueOf(item.quantity())));
        }
        order.setSubtotal(total); order.setTotal(total); table.setStatus(RestaurantTable.Status.OCCUPIED);
        return orderResponse(order);
    }
    @Transactional public OrderResponse updateOrderStatus(Long id, String rawStatus, String username) {
        Order order = orders.findById(id).orElseThrow(() -> ApiException.notFound("Pedido no encontrado."));
        User actor = users.findByUsernameAndActiveTrue(username).orElseThrow(() -> ApiException.notFound("Usuario no encontrado."));
        OrderStatus next = enumValue(OrderStatus.class, rawStatus, "Estado de pedido inválido.");
        String role = actor.getRole().getName();
        if (role.equals("COCINA") && next != OrderStatus.IN_PREPARATION && next != OrderStatus.READY)
            throw ApiException.forbidden("Cocina solo puede actualizar la preparación del pedido.");
        if (role.equals("MESERO") && !order.getWaiter().getId().equals(actor.getId()))
            throw ApiException.forbidden("Solo puedes actualizar tus propios pedidos.");
        if (role.equals("MESERO") && next != OrderStatus.IN_PREPARATION && next != OrderStatus.DELIVERED && next != OrderStatus.CANCELLED)
            throw ApiException.forbidden("El mesero no puede modificar la preparación de cocina.");
        boolean valid = switch (order.getStatus()) {
            case PENDING -> next == OrderStatus.IN_PREPARATION || next == OrderStatus.CANCELLED;
            case IN_PREPARATION -> next == OrderStatus.READY || next == OrderStatus.CANCELLED;
            case READY -> next == OrderStatus.DELIVERED || next == OrderStatus.CANCELLED;
            default -> false;
        };
        if (!valid) throw ApiException.conflict("Transición de estado no permitida.");
        order.setStatus(next);
        if (next == OrderStatus.DELIVERED || next == OrderStatus.CANCELLED) {
            order.setClosedAt(Instant.now()); order.getTable().setStatus(RestaurantTable.Status.AVAILABLE);
        }
        return orderResponse(order);
    }
    private OrderResponse orderResponse(Order order) {
        List<OrderLineResponse> lines = orderDetails.findByOrderId(order.getId()).stream().map(line -> new OrderLineResponse(
                line.getProduct().getId(), line.getProduct().getName(), line.getQuantity(), line.getUnitPrice(),
                line.getUnitPrice().multiply(BigDecimal.valueOf(line.getQuantity())), line.getNotes())).toList();
        return new OrderResponse(order.getId(), order.getOrderNumber(), order.getTable().getId(), order.getTable().getTableNumber(),
                order.getWaiter().getId(), order.getWaiter().getFullName(), order.getStatus().name(), order.getNotes(),
                order.getSubtotal(), order.getTotal(), order.getOpenedAt() == null ? Instant.now() : order.getOpenedAt(), lines);
    }

    @Transactional(readOnly = true) public List<Supply> supplies() { return supplies.findByActiveTrueOrderByNameAsc(); }
    @Transactional public Supply saveSupply(Long id, SupplyRequest request, String username) {
        boolean creating = id == null;
        Supply supply = creating ? new Supply() : supplies.findById(id).orElseThrow(() -> ApiException.notFound("Insumo no encontrado."));
        supply.setSku(request.sku().trim()); supply.setName(request.name().trim()); supply.setUnit(request.unit().trim());
        supply.setMinimumStock(request.minimumStock());
        if (creating) {
            User actor = users.findByUsernameAndActiveTrue(username).orElseThrow(() -> ApiException.notFound("Usuario no encontrado."));
            supply.setStockQuantity(BigDecimal.ZERO);
            supply = supplies.save(supply);
            if (request.stockQuantity().signum() > 0) {
                supply.setStockQuantity(request.stockQuantity());
                InventoryMovement initial = new InventoryMovement(); initial.setSupply(supply); initial.setUser(actor);
                initial.setMovementType(InventoryMovement.Type.IN); initial.setQuantity(request.stockQuantity());
                initial.setStockBefore(BigDecimal.ZERO); initial.setStockAfter(request.stockQuantity());
                initial.setReason("Carga inicial del insumo"); movements.save(initial);
            }
        }
        return supplies.save(supply);
    }
    @Transactional(readOnly = true) public List<MovementResponse> movements() {
        return movements.findTop100ByOrderByOccurredAtDesc().stream().map(m -> new MovementResponse(m.getId(),
                m.getSupply().getId(), m.getSupply().getName(), m.getUser().getId(), m.getUser().getFullName(),
                m.getMovementType().name(), m.getQuantity(), m.getStockBefore(), m.getStockAfter(), m.getReason(), m.getOccurredAt() == null ? Instant.now() : m.getOccurredAt())).toList();
    }
    @Transactional public InventoryMovement moveInventory(MovementRequest request, String username) {
        Supply supply = supplies.findByIdForUpdate(request.supplyId()).orElseThrow(() -> ApiException.notFound("Insumo no encontrado."));
        User user = users.findByUsernameAndActiveTrue(username).orElseThrow(() -> ApiException.notFound("Usuario no encontrado."));
        InventoryMovement.Type type = enumValue(InventoryMovement.Type.class, request.movementType(), "Tipo de movimiento inválido.");
        BigDecimal quantity = request.quantity();
        if (type != InventoryMovement.Type.ADJUSTMENT && quantity.signum() <= 0)
            throw ApiException.badRequest("La cantidad del movimiento debe ser mayor que cero.");
        BigDecimal stockBefore = supply.getStockQuantity();
        if (type == InventoryMovement.Type.IN) supply.setStockQuantity(supply.getStockQuantity().add(quantity));
        else if (type == InventoryMovement.Type.OUT) {
            if (supply.getStockQuantity().compareTo(quantity) < 0) throw ApiException.conflict("No hay existencias suficientes.");
            supply.setStockQuantity(supply.getStockQuantity().subtract(quantity));
        } else {
            BigDecimal old = supply.getStockQuantity();
            quantity = old.subtract(quantity).abs();
            supply.setStockQuantity(request.quantity());
            if (quantity.signum() == 0) throw ApiException.badRequest("El ajuste no cambia las existencias.");
        }
        InventoryMovement movement = new InventoryMovement(); movement.setSupply(supply); movement.setUser(user);
        movement.setMovementType(type); movement.setQuantity(quantity); movement.setStockBefore(stockBefore);
        movement.setStockAfter(supply.getStockQuantity()); movement.setReason(request.reason().trim());
        return movements.save(movement);
    }

    @Transactional(readOnly = true) public List<SaleResponse> sales() { return sales.findTop100ByOrderByPaidAtDesc().stream().map(this::saleResponse).toList(); }
    @Transactional public SaleResponse payOrder(Long orderId, PaymentRequest request, String username) {
        Order order = orders.findById(orderId).orElseThrow(() -> ApiException.notFound("Pedido no encontrado."));
        if (order.getStatus() != OrderStatus.DELIVERED) throw ApiException.conflict("Solo se pueden cobrar pedidos entregados.");
        if (sales.existsByOrderId(orderId)) throw ApiException.conflict("El pedido ya tiene un pago registrado.");
        User cashier = users.findByUsernameAndActiveTrue(username).orElseThrow(() -> ApiException.notFound("Usuario no encontrado."));
        if (cashier.getRole().getName().equals("MESERO") && !order.getWaiter().getId().equals(cashier.getId()))
            throw ApiException.forbidden("Solo puedes cobrar tus propios pedidos.");
        Sale.PaymentMethod method = enumValue(Sale.PaymentMethod.class, request.paymentMethod(), "Método de pago inválido.");
        Sale sale = new Sale(); sale.setOrder(order); sale.setCashier(cashier); sale.setPaymentMethod(method); sale.setAmount(order.getTotal()); sale = sales.save(sale);
        for (OrderDetail line : orderDetails.findByOrderId(orderId)) {
            SaleDetail detail = new SaleDetail(); detail.setSale(sale); detail.setProduct(line.getProduct());
            detail.setProductName(line.getProduct().getName()); detail.setQuantity(line.getQuantity()); detail.setUnitPrice(line.getUnitPrice()); saleDetails.save(detail);
        }
        return saleResponse(sale);
    }
    private SaleResponse saleResponse(Sale sale) {
        List<SaleLineResponse> lines = saleDetails.findBySaleId(sale.getId()).stream().map(line -> new SaleLineResponse(
                line.getProduct() == null ? null : line.getProduct().getId(), line.getProductName(), line.getQuantity(), line.getUnitPrice())).toList();
        return new SaleResponse(sale.getId(), sale.getOrder().getId(), sale.getOrder().getOrderNumber(), sale.getCashier().getId(),
                sale.getCashier().getFullName(), sale.getPaymentMethod().name(), sale.getAmount(), sale.getStatus().name(),
                sale.getPaidAt() == null ? Instant.now() : sale.getPaidAt(), lines);
    }

    @Transactional(readOnly = true) public DashboardResponse dashboard() {
        LocalDate today = LocalDate.now(BUSINESS_ZONE);
        BigDecimal todaySales = sales.totalPaidBetween(Sale.Status.PAID, today.atStartOfDay(BUSINESS_ZONE).toInstant(), today.plusDays(1).atStartOfDay(BUSINESS_ZONE).toInstant());
        List<SalePoint> chart = new ArrayList<>();
        for (int back = 6; back >= 0; back--) {
            LocalDate date = today.minusDays(back);
            chart.add(new SalePoint(date.format(DateTimeFormatter.ofPattern("dd/MM")), sales.totalPaidBetween(Sale.Status.PAID,
                    date.atStartOfDay(BUSINESS_ZONE).toInstant(), date.plusDays(1).atStartOfDay(BUSINESS_ZONE).toInstant())));
        }
        List<OrderResponse> recent = orders.findAllByOrderByOpenedAtDesc().stream().limit(6).map(this::orderResponse).toList();
        List<TopProduct> topProducts = orderDetails.topProducts(OrderStatus.DELIVERED).stream().limit(5)
                .map(row -> new TopProduct((String) row[0], ((Number) row[1]).longValue())).toList();
        return new DashboardResponse(todaySales, orders.countByStatus(OrderStatus.PENDING) + orders.countByStatus(OrderStatus.IN_PREPARATION),
                orders.countByStatus(OrderStatus.DELIVERED), tables.countByStatus(RestaurantTable.Status.OCCUPIED),
                tables.countByStatus(RestaurantTable.Status.AVAILABLE), chart, topProducts, recent);
    }

    @Transactional(readOnly = true) public ReportResponse report(String rawPeriod) {
        PeriodBounds bounds = periodBounds(rawPeriod);
        List<Sale> periodSales = sales.findByStatusAndPaidAtGreaterThanEqualAndPaidAtLessThanOrderByPaidAtAsc(
                Sale.Status.PAID, bounds.from().toInstant(), bounds.to().toInstant());
        BigDecimal total = periodSales.stream().map(Sale::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        Map<LocalDate, BigDecimal> dayTotals = new LinkedHashMap<>();
        Map<Long, BigDecimal> waiterTotals = new LinkedHashMap<>();
        Map<Long, String> waiterNames = new LinkedHashMap<>();
        for (LocalDate day = bounds.from().toLocalDate(); day.isBefore(bounds.to().toLocalDate()); day = day.plusDays(1)) dayTotals.put(day, BigDecimal.ZERO);
        for (Sale sale : periodSales) {
            LocalDate paidDay = sale.getPaidAt().atZone(BUSINESS_ZONE).toLocalDate();
            dayTotals.computeIfPresent(paidDay, (key, value) -> value.add(sale.getAmount()));
            User waiter = sale.getOrder().getWaiter();
            waiterTotals.merge(waiter.getId(), sale.getAmount(), BigDecimal::add);
            waiterNames.put(waiter.getId(), waiter.getFullName());
        }
        List<SalePoint> daily = dayTotals.entrySet().stream().map(e -> new SalePoint(e.getKey().format(DateTimeFormatter.ofPattern("dd/MM")), e.getValue())).toList();
        List<TopProduct> top = saleDetails.topProductsBetween(Sale.Status.PAID, bounds.from().toInstant(), bounds.to().toInstant()).stream().limit(10)
                .map(row -> new TopProduct((String) row[0], ((Number) row[1]).longValue())).toList();
        List<WaiterPerformance> waiters = waiterTotals.entrySet().stream()
                .map(e -> new WaiterPerformance(e.getKey(), waiterNames.get(e.getKey()), periodSales.stream().filter(s -> s.getOrder().getWaiter().getId().equals(e.getKey())).count(), e.getValue()))
                .sorted(Comparator.comparing(WaiterPerformance::sales).reversed()).toList();
        long delivered = orders.countByStatusAndClosedAtGreaterThanEqualAndClosedAtLessThan(OrderStatus.DELIVERED,
                bounds.from().toInstant(), bounds.to().toInstant());
        return new ReportResponse(bounds.label(), total, periodSales.size(), delivered, daily, top, waiters);
    }

    @Transactional(readOnly = true) public String reportCsv(String rawPeriod) {
        PeriodBounds bounds = periodBounds(rawPeriod);
        List<Sale> rows = sales.findByStatusAndPaidAtGreaterThanEqualAndPaidAtLessThanOrderByPaidAtAsc(
                Sale.Status.PAID, bounds.from().toInstant(), bounds.to().toInstant());
        StringBuilder csv = new StringBuilder("\uFEFFFecha,Venta,Pedido,Mesero,Cajero,Método,Importe\r\n");
        for (Sale sale : rows) {
            csv.append(csvCell(sale.getPaidAt().atZone(BUSINESS_ZONE).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))).append(',')
                    .append(csvCell("V-" + sale.getId())).append(',').append(csvCell(sale.getOrder().getOrderNumber())).append(',')
                    .append(csvCell(sale.getOrder().getWaiter().getFullName())).append(',').append(csvCell(sale.getCashier().getFullName())).append(',')
                    .append(csvCell(sale.getPaymentMethod().name())).append(',').append(sale.getAmount()).append("\r\n");
        }
        return csv.toString();
    }

    private PeriodBounds periodBounds(String rawPeriod) {
        LocalDate today = LocalDate.now(BUSINESS_ZONE);
        String period = rawPeriod == null ? "WEEK" : rawPeriod.trim().toUpperCase(Locale.ROOT);
        LocalDate from = switch (period) {
            case "DAY" -> today;
            case "WEEK" -> today.with(java.time.DayOfWeek.MONDAY);
            case "MONTH" -> today.withDayOfMonth(1);
            default -> throw ApiException.badRequest("Período inválido. Usa DAY, WEEK o MONTH.");
        };
        LocalDate toDate = period.equals("DAY") ? from.plusDays(1) : period.equals("WEEK") ? from.plusDays(7) : from.plusMonths(1);
        return new PeriodBounds(period, from.atStartOfDay(BUSINESS_ZONE), toDate.atStartOfDay(BUSINESS_ZONE));
    }
    private String csvCell(String value) {
        if (!value.isEmpty() && "=+-@\t\r".indexOf(value.charAt(0)) >= 0) value = "'" + value;
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }
    private record PeriodBounds(String label, ZonedDateTime from, ZonedDateTime to) { }

    private <E extends Enum<E>> E enumValue(Class<E> type, String value, String message) {
        try { return Enum.valueOf(type, value.trim().toUpperCase(Locale.ROOT)); }
        catch (RuntimeException ex) { throw ApiException.badRequest(message); }
    }
}
