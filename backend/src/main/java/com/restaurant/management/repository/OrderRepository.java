package com.restaurant.management.repository;
import com.restaurant.management.entity.Order;
import com.restaurant.management.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.time.Instant;
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findAllByOrderByOpenedAtDesc();
    List<Order> findByStatusInOrderByOpenedAtAsc(List<OrderStatus> statuses);
    long countByStatus(OrderStatus status);
    long countByStatusAndClosedAtGreaterThanEqualAndClosedAtLessThan(OrderStatus status, Instant from, Instant to);
}
