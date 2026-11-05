package com.restaurant.management.controller;

import com.restaurant.management.dto.ApiDtos.*;
import com.restaurant.management.entity.*;
import com.restaurant.management.service.RestaurantService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.restaurant.management.security.UserPrincipal;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ManagementController {
    private final RestaurantService service;
    public ManagementController(RestaurantService service) { this.service = service; }

    @GetMapping("/dashboard") @PreAuthorize("hasAnyRole('ADMINISTRADOR','MESERO')")
    public DashboardResponse dashboard() { return service.dashboard(); }
    @GetMapping("/tables") public List<RestaurantTable> tables() { return service.tables(); }
    @PostMapping("/tables") @PreAuthorize("hasRole('ADMINISTRADOR')")
    public RestaurantTable createTable(@Valid @RequestBody TableRequest request) { return service.saveTable(null, request); }
    @PutMapping("/tables/{id}") @PreAuthorize("hasRole('ADMINISTRADOR')")
    public RestaurantTable updateTable(@PathVariable Long id, @Valid @RequestBody TableRequest request) { return service.saveTable(id, request); }
    @DeleteMapping("/tables/{id}") @PreAuthorize("hasRole('ADMINISTRADOR')")
    public void deleteTable(@PathVariable Long id) { service.deleteTable(id); }

    @GetMapping("/categories") public List<Category> categories() { return service.categories(); }
    @PostMapping("/categories") @PreAuthorize("hasRole('ADMINISTRADOR')")
    public Category createCategory(@Valid @RequestBody CategoryRequest request) { return service.saveCategory(null, request); }
    @PutMapping("/categories/{id}") @PreAuthorize("hasRole('ADMINISTRADOR')")
    public Category updateCategory(@PathVariable Long id, @Valid @RequestBody CategoryRequest request) { return service.saveCategory(id, request); }

    @GetMapping("/products") public List<Product> products(@RequestParam(defaultValue="false") boolean availableOnly) { return service.products(availableOnly); }
    @PostMapping("/products") @PreAuthorize("hasRole('ADMINISTRADOR')")
    public Product createProduct(@Valid @RequestBody ProductRequest request) { return service.saveProduct(null, request); }
    @PutMapping("/products/{id}") @PreAuthorize("hasRole('ADMINISTRADOR')")
    public Product updateProduct(@PathVariable Long id, @Valid @RequestBody ProductRequest request) { return service.saveProduct(id, request); }
    @DeleteMapping("/products/{id}") @PreAuthorize("hasRole('ADMINISTRADOR')")
    public void deactivateProduct(@PathVariable Long id) { service.deactivateProduct(id); }

    @GetMapping("/users") @PreAuthorize("hasRole('ADMINISTRADOR')") public List<UserResponse> users() { return service.users(); }
    @PostMapping("/users") @PreAuthorize("hasRole('ADMINISTRADOR')")
    public UserResponse createUser(@Valid @RequestBody UserRequest request) { return service.createUser(request); }
    @PatchMapping("/users/{id}/active") @PreAuthorize("hasRole('ADMINISTRADOR')")
    public UserResponse updateUserActive(@PathVariable Long id, @Valid @RequestBody UserActiveRequest request,
            @AuthenticationPrincipal UserPrincipal actor) { return service.updateUserActive(id, request.active(), actor.username()); }
}
