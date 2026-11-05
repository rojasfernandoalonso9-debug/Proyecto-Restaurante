package com.restaurant.management.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity @Table(name = "restaurant_tables") @Getter @Setter @NoArgsConstructor
public class RestaurantTable {
    public enum Status { AVAILABLE, OCCUPIED, RESERVED }
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(name = "table_number", nullable = false, unique = true) private int tableNumber;
    @Column(nullable = false) private int capacity;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 16) private Status status = Status.AVAILABLE;
}
