package com.restaurant.management.repository;
import com.restaurant.management.entity.Sale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
public interface SaleRepository extends JpaRepository<Sale, Long> {
    List<Sale> findTop100ByOrderByPaidAtDesc();
    List<Sale> findByStatusAndPaidAtGreaterThanEqualAndPaidAtLessThanOrderByPaidAtAsc(Sale.Status status, Instant from, Instant to);
    boolean existsByOrderId(Long orderId);
    @Query("select coalesce(sum(s.amount), 0) from Sale s where s.status = :status and s.paidAt >= :from and s.paidAt < :to")
    BigDecimal totalPaidBetween(@Param("status") Sale.Status status, @Param("from") Instant from, @Param("to") Instant to);
}
