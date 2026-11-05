package com.restaurant.management.controller;

import com.restaurant.management.dto.ApiDtos.*;
import com.restaurant.management.security.UserPrincipal;
import com.restaurant.management.service.RestaurantService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController @RequestMapping("/api/orders")
public class OrderController {
    private final RestaurantService service;
    public OrderController(RestaurantService service) { this.service = service; }
    @GetMapping @PreAuthorize("hasAnyRole('ADMINISTRADOR','MESERO')") public List<OrderResponse> all() { return service.orders(); }
    @PostMapping @PreAuthorize("hasAnyRole('ADMINISTRADOR','MESERO')")
    public OrderResponse create(@Valid @RequestBody OrderRequest request, @AuthenticationPrincipal UserPrincipal user) { return service.createOrder(request, user.username()); }
    @PatchMapping("/{id}/status") @PreAuthorize("hasAnyRole('ADMINISTRADOR','MESERO','COCINA')")
    public OrderResponse updateStatus(@PathVariable Long id, @Valid @RequestBody OrderStatusRequest request,
            @AuthenticationPrincipal UserPrincipal user) { return service.updateOrderStatus(id, request.status(), user.username()); }
    @GetMapping("/kitchen") @PreAuthorize("hasAnyRole('ADMINISTRADOR','COCINA')") public List<OrderResponse> kitchen() { return service.kitchenOrders(); }
}
