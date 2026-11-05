package com.restaurant.management.repository;
import com.restaurant.management.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface RoleRepository extends JpaRepository<Role, Long> { Optional<Role> findByName(String name); }
