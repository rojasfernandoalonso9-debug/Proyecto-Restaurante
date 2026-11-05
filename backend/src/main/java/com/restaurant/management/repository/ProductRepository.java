package com.restaurant.management.repository;
import com.restaurant.management.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("select p from Product p where p.available = true and p.category.active = true order by p.name")
    List<Product> findAvailableForOrder();
}
