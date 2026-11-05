package com.restaurant.management.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;

@Entity @Table(name = "sale_details") @Getter @Setter @NoArgsConstructor
public class SaleDetail {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "sale_id") private Sale sale;
    @ManyToOne(fetch = FetchType.EAGER) @JoinColumn(name = "product_id") private Product product;
    @Column(name = "product_name", nullable = false, length = 120) private String productName;
    @Column(nullable = false) private int quantity;
    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2) private BigDecimal unitPrice;
}
