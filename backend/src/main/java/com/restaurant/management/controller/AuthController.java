package com.restaurant.management.controller;

import com.restaurant.management.dto.ApiDtos.AuthResponse;
import com.restaurant.management.dto.ApiDtos.LoginRequest;
import com.restaurant.management.dto.ApiDtos.UserResponse;
import com.restaurant.management.security.JwtService;
import com.restaurant.management.security.UserPrincipal;
import jakarta.validation.Valid;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwt;
    public AuthController(AuthenticationManager authenticationManager, JwtService jwt) { this.authenticationManager = authenticationManager; this.jwt = jwt; }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password()));
        UserPrincipal user = (UserPrincipal) authentication.getPrincipal();
        return new AuthResponse(jwt.generate(user), user.username(), user.fullName(), user.role());
    }

    @GetMapping("/me")
    public UserResponse me(@AuthenticationPrincipal UserPrincipal user) {
        return new UserResponse(user.id(), user.username(), user.fullName(), user.email(), user.role(), user.active());
    }
}
