package com.restaurant.management.repository;
import com.restaurant.management.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.restaurant.management.entity.OrderStatus;
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {
    List<OrderDetail> findByOrderId(Long orderId);
    void deleteByOrderId(Long orderId);
    @Query("select d.product.name, sum(d.quantity) from OrderDetail d join d.order o where o.status = :status group by d.product.name order by sum(d.quantity) desc")
    List<Object[]> topProducts(@Param("status") OrderStatus status);
}
