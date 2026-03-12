package org.kariioke.socialmediaapi.controller;

import lombok.RequiredArgsConstructor;
import org.kariioke.socialmediaapi.dto.request.AuthRequest;
import org.kariioke.socialmediaapi.dto.response.ApiResponse;
import org.kariioke.socialmediaapi.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse.AuthResponse> register (@RequestBody AuthRequest.Register request) {
        ApiResponse.AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/login")
    public ResponseEntity<ApiResponse.AuthResponse> login (
            @RequestBody AuthRequest.Login request) {
        ApiResponse.AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}
