package com.restaurant.management.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;

@Entity @Table(name = "supplies") @Getter @Setter @NoArgsConstructor
public class Supply {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false, unique = true, length = 40) private String sku;
    @Column(nullable = false, length = 120) private String name;
    @Column(nullable = false, length = 24) private String unit;
    @Column(name = "stock_quantity", nullable = false, precision = 12, scale = 3) private BigDecimal stockQuantity;
    @Column(name = "minimum_stock", nullable = false, precision = 12, scale = 3) private BigDecimal minimumStock;
    @Column(nullable = false) private boolean active = true;
}
