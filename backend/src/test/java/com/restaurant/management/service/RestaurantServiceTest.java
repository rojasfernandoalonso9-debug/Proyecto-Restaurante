package com.restaurant.management.service;

import com.restaurant.management.entity.*;
import com.restaurant.management.exception.ApiException;
import com.restaurant.management.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RestaurantServiceTest {
    @Mock RoleRepository roles;
    @Mock UserRepository users;
    @Mock RestaurantTableRepository tables;
    @Mock CategoryRepository categories;
    @Mock ProductRepository products;
    @Mock OrderRepository orders;
    @Mock OrderDetailRepository orderDetails;
    @Mock SupplyRepository supplies;
    @Mock InventoryMovementRepository movements;
    @Mock SaleRepository sales;
    @Mock SaleDetailRepository saleDetails;
    @Mock PasswordEncoder passwordEncoder;

    RestaurantService service;

    @BeforeEach void setUp() {
        service = new RestaurantService(roles, users, tables, categories, products, orders, orderDetails,
                supplies, movements, sales, saleDetails, passwordEncoder);
    }

    @Test void kitchenCanStartPreparation() {
        Order order = order(OrderStatus.PENDING, waiter(10L, "MESERO"));
        when(orders.findById(1L)).thenReturn(Optional.of(order));
        when(users.findByUsernameAndActiveTrue("cook")).thenReturn(Optional.of(waiter(20L, "COCINA")));
        when(orderDetails.findByOrderId(1L)).thenReturn(List.of());

        var result = service.updateOrderStatus(1L, "IN_PREPARATION", "cook");

        assertEquals("IN_PREPARATION", result.status());
        verify(orders).findById(1L);
    }

    @Test void kitchenCannotSkipThePreparationQueue() {
        Order order = order(OrderStatus.PENDING, waiter(10L, "MESERO"));
        when(orders.findById(1L)).thenReturn(Optional.of(order));
        when(users.findByUsernameAndActiveTrue("cook")).thenReturn(Optional.of(waiter(20L, "COCINA")));

        ApiException error = assertThrows(ApiException.class,
                () -> service.updateOrderStatus(1L, "READY", "cook"));

        assertEquals(409, error.getStatus().value());
        assertEquals(OrderStatus.PENDING, order.getStatus());
    }

    @Test void meseroCannotUpdateAnotherWaitersOrder() {
        Order order = order(OrderStatus.READY, waiter(10L, "MESERO"));
        when(orders.findById(1L)).thenReturn(Optional.of(order));
        when(users.findByUsernameAndActiveTrue("other")).thenReturn(Optional.of(waiter(11L, "MESERO")));

        ApiException error = assertThrows(ApiException.class,
                () -> service.updateOrderStatus(1L, "DELIVERED", "other"));

        assertEquals(403, error.getStatus().value());
        assertEquals(OrderStatus.READY, order.getStatus());
    }

    private User waiter(Long id, String roleName) {
        Role role = new Role(); role.setId(1L); role.setName(roleName);
        User user = new User(); user.setId(id); user.setUsername(roleName.toLowerCase());
        user.setFullName("Demo user"); user.setRole(role); user.setActive(true);
        return user;
    }

    private Order order(OrderStatus status, User waiter) {
        RestaurantTable table = new RestaurantTable(); table.setId(2L); table.setTableNumber(2);
        table.setCapacity(4); table.setStatus(RestaurantTable.Status.OCCUPIED);
        Order order = new Order(); order.setId(1L); order.setOrderNumber("ORD-TEST-0001");
        order.setTable(table); order.setWaiter(waiter); order.setStatus(status);
        return order;
    }
}
