package com.restaurant.management.repository;
import com.restaurant.management.entity.Supply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;
public interface SupplyRepository extends JpaRepository<Supply, Long> {
    List<Supply> findByActiveTrueOrderByNameAsc();
    @Lock(LockModeType.PESSIMISTIC_WRITE) @Query("select s from Supply s where s.id = :id")
    Optional<Supply> findByIdForUpdate(@Param("id") Long id);
}
