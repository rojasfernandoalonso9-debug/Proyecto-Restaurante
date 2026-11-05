package com.restaurant.management.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.Instant;

@Entity @Table(name = "orders") @Getter @Setter @NoArgsConstructor
public class Order {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(name = "order_number", nullable = false, unique = true, length = 24) private String orderNumber;
    @ManyToOne(fetch = FetchType.EAGER, optional = false) @JoinColumn(name = "table_id") private RestaurantTable table;
    @ManyToOne(fetch = FetchType.EAGER, optional = false) @JoinColumn(name = "waiter_id") private User waiter;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20) private OrderStatus status = OrderStatus.PENDING;
    @Column(length = 500) private String notes;
    @Column(nullable = false, precision = 12, scale = 2) private BigDecimal subtotal = BigDecimal.ZERO;
    @Column(nullable = false, precision = 12, scale = 2) private BigDecimal total = BigDecimal.ZERO;
    @Column(name = "opened_at", insertable = false, updatable = false) private Instant openedAt;
    @Column(name = "closed_at") private Instant closedAt;
}
