package com.restaurant.management.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public final class ApiDtos {
    private ApiDtos() { }
    public record LoginRequest(@NotBlank String username, @NotBlank String password) { }
    public record AuthResponse(String token, String username, String fullName, String role) { }
    public record UserResponse(Long id, String username, String fullName, String email, String role, boolean active) { }
    public record UserRequest(@NotBlank @Size(max=120) String fullName, @NotBlank @Size(max=50) String username,
            @NotBlank @Email @Size(max=160) String email, @NotBlank @Size(min=8) String password,
            @NotBlank String role) { }
    public record UserActiveRequest(@NotNull Boolean active) { }
    public record TableRequest(@Min(1) int tableNumber, @Min(1) @Max(32) int capacity, String status) { }
    public record CategoryRequest(@NotBlank @Size(max=80) String name, @Size(max=255) String description, Boolean active) { }
    public record ProductRequest(@NotNull Long categoryId, @NotBlank @Size(max=40) String sku,
            @NotBlank @Size(max=120) String name, @Size(max=500) String description,
            @NotNull @Digits(integer=8, fraction=2) @DecimalMin("0.00") BigDecimal price, @Size(max=500) String imageUrl, Boolean available) { }
    public record OrderItemRequest(@NotNull Long productId, @Min(1) @Max(999) int quantity, @Size(max=255) String notes) { }
    public record OrderRequest(@NotNull Long tableId, @Size(max=500) String notes,
            @NotEmpty List<@Valid OrderItemRequest> items) { }
    public record OrderStatusRequest(@NotBlank String status) { }
    public record SupplyRequest(@NotBlank @Size(max=40) String sku, @NotBlank @Size(max=120) String name,
            @NotBlank @Size(max=24) String unit, @NotNull @Digits(integer=9, fraction=3) @DecimalMin("0.000") BigDecimal stockQuantity,
            @NotNull @Digits(integer=9, fraction=3) @DecimalMin("0.000") BigDecimal minimumStock) { }
    public record MovementRequest(@NotNull Long supplyId, @NotBlank String movementType,
            @NotNull @Digits(integer=9, fraction=3) @DecimalMin(value="0.000") BigDecimal quantity, @NotBlank @Size(max=255) String reason) { }
    public record PaymentRequest(@NotBlank String paymentMethod) { }
    public record OrderLineResponse(Long productId, String productName, int quantity, BigDecimal unitPrice, BigDecimal lineTotal, String notes) { }
    public record OrderResponse(Long id, String orderNumber, Long tableId, int tableNumber, Long waiterId,
            String waiterName, String status, String notes, BigDecimal subtotal, BigDecimal total,
            Instant openedAt, List<OrderLineResponse> items) { }
    public record DashboardResponse(BigDecimal todaySales, long pendingOrders, long completedOrders,
            long occupiedTables, long availableTables, List<SalePoint> sales, List<TopProduct> topProducts,
            List<OrderResponse> recentOrders) { }
    public record SalePoint(String date, BigDecimal total) { }
    public record TopProduct(String name, long quantity) { }
    public record MovementResponse(Long id, Long supplyId, String supplyName, Long userId, String userName,
            String movementType, BigDecimal quantity, BigDecimal stockBefore, BigDecimal stockAfter,
            String reason, Instant occurredAt) { }
    public record SaleLineResponse(Long productId, String productName, int quantity, BigDecimal unitPrice) { }
    public record SaleResponse(Long id, Long orderId, String orderNumber, Long cashierId, String cashierName,
            String paymentMethod, BigDecimal amount, String status, Instant paidAt, List<SaleLineResponse> items) { }
    public record WaiterPerformance(Long userId, String name, long orders, BigDecimal sales) { }
    public record ReportResponse(String period, BigDecimal totalSales, long paidTransactions, long deliveredOrders,
            List<SalePoint> salesByDay, List<TopProduct> topProducts, List<WaiterPerformance> waiterPerformance) { }
}
