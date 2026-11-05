package com.restaurant.management.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.Instant;

@Entity @Table(name = "sales") @Getter @Setter @NoArgsConstructor
public class Sale {
    public enum PaymentMethod { CASH, CARD, TRANSFER }
    public enum Status { PAID, REFUNDED }
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @OneToOne(fetch = FetchType.EAGER, optional = false) @JoinColumn(name = "order_id", unique = true) private Order order;
    @ManyToOne(fetch = FetchType.EAGER, optional = false) @JoinColumn(name = "cashier_id") private User cashier;
    @Enumerated(EnumType.STRING) @Column(name = "payment_method", nullable = false, length = 16) private PaymentMethod paymentMethod;
    @Column(nullable = false, precision = 12, scale = 2) private BigDecimal amount;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 16) private Status status = Status.PAID;
    @Column(name = "paid_at", insertable = false, updatable = false) private Instant paidAt;
}
