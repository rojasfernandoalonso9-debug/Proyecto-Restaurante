package com.restaurant.management.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.Instant;

@Entity @Table(name = "inventory_movements") @Getter @Setter @NoArgsConstructor
public class InventoryMovement {
    public enum Type { IN, OUT, ADJUSTMENT }
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.EAGER, optional = false) @JoinColumn(name = "supply_id") private Supply supply;
    @ManyToOne(fetch = FetchType.EAGER, optional = false) @JoinColumn(name = "user_id") private User user;
    @Enumerated(EnumType.STRING) @Column(name = "movement_type", nullable = false, length = 16) private Type movementType;
    @Column(nullable = false, precision = 12, scale = 3) private BigDecimal quantity;
    @Column(name = "stock_before", nullable = false, precision = 12, scale = 3) private BigDecimal stockBefore;
    @Column(name = "stock_after", nullable = false, precision = 12, scale = 3) private BigDecimal stockAfter;
    @Column(nullable = false, length = 255) private String reason;
    @Column(name = "occurred_at", insertable = false, updatable = false) private Instant occurredAt;
}
