package com.restaurant.management.controller;

import com.restaurant.management.dto.ApiDtos.*;
import com.restaurant.management.entity.Supply;
import com.restaurant.management.security.UserPrincipal;
import com.restaurant.management.service.RestaurantService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController @RequestMapping("/api/inventory")
public class InventoryController {
    private final RestaurantService service;
    public InventoryController(RestaurantService service) { this.service = service; }
    @GetMapping("/supplies") @PreAuthorize("hasAnyRole('ADMINISTRADOR','COCINA')") public List<Supply> supplies() { return service.supplies(); }
    @PostMapping("/supplies") @PreAuthorize("hasRole('ADMINISTRADOR')") public Supply create(@Valid @RequestBody SupplyRequest request, @AuthenticationPrincipal UserPrincipal user) { return service.saveSupply(null, request, user.username()); }
    @PutMapping("/supplies/{id}") @PreAuthorize("hasRole('ADMINISTRADOR')") public Supply update(@PathVariable Long id, @Valid @RequestBody SupplyRequest request, @AuthenticationPrincipal UserPrincipal user) { return service.saveSupply(id, request, user.username()); }
    @GetMapping("/movements") @PreAuthorize("hasAnyRole('ADMINISTRADOR','COCINA')") public List<MovementResponse> movements() { return service.movements(); }
    @PostMapping("/movements") @PreAuthorize("hasAnyRole('ADMINISTRADOR','COCINA')")
    public MovementResponse move(@Valid @RequestBody MovementRequest request, @AuthenticationPrincipal UserPrincipal user) {
        var movement = service.moveInventory(request, user.username());
        return new MovementResponse(movement.getId(), movement.getSupply().getId(), movement.getSupply().getName(),
                movement.getUser().getId(), movement.getUser().getFullName(), movement.getMovementType().name(),
                movement.getQuantity(), movement.getStockBefore(), movement.getStockAfter(), movement.getReason(),
                movement.getOccurredAt() == null ? java.time.Instant.now() : movement.getOccurredAt());
    }
}
