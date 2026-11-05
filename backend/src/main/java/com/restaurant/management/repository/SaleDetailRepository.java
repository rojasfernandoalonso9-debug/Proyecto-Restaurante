package com.restaurant.management.repository;
import com.restaurant.management.entity.SaleDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.time.Instant;
import com.restaurant.management.entity.Sale;
public interface SaleDetailRepository extends JpaRepository<SaleDetail, Long> {
    List<SaleDetail> findBySaleId(Long saleId);
    @Query("select d.productName, sum(d.quantity) from SaleDetail d join d.sale s where s.status = :status and s.paidAt >= :from and s.paidAt < :to group by d.productName order by sum(d.quantity) desc")
    List<Object[]> topProductsBetween(@Param("status") Sale.Status status, @Param("from") Instant from, @Param("to") Instant to);
}
