package com.restaurant.management.repository;
import com.restaurant.management.entity.InventoryMovement;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface InventoryMovementRepository extends JpaRepository<InventoryMovement, Long> { List<InventoryMovement> findTop100ByOrderByOccurredAtDesc(); }
