package com.restaurant.management.controller;

import com.restaurant.management.dto.ApiDtos.*;
import com.restaurant.management.security.UserPrincipal;
import com.restaurant.management.service.RestaurantService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import java.util.List;

@RestController @RequestMapping("/api")
public class SalesController {
    private final RestaurantService service;
    public SalesController(RestaurantService service) { this.service = service; }
    @GetMapping("/sales") @PreAuthorize("hasRole('ADMINISTRADOR')") public List<SaleResponse> sales() { return service.sales(); }
    @GetMapping("/reports") @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ReportResponse report(@RequestParam(defaultValue="WEEK") String period) { return service.report(period); }
    @GetMapping(value="/reports/export", produces="text/csv") @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<byte[]> export(@RequestParam(defaultValue="WEEK") String period) {
        byte[] content = service.reportCsv(period).getBytes(java.nio.charset.StandardCharsets.UTF_8);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=restaurant-report-" + period.toLowerCase() + ".csv")
                .contentType(MediaType.parseMediaType("text/csv;charset=UTF-8")).body(content);
    }
    @PostMapping("/orders/{orderId}/payment") @PreAuthorize("hasAnyRole('ADMINISTRADOR','MESERO')")
    public SaleResponse pay(@PathVariable Long orderId, @Valid @RequestBody PaymentRequest request,
            @AuthenticationPrincipal UserPrincipal user) { return service.payOrder(orderId, request, user.username()); }
}
