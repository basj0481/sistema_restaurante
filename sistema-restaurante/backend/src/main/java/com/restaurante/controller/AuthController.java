package com.restaurante.controller;

import com.restaurante.dto.*;
import com.restaurante.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/** CU00 Portal de Acceso, CU01 Iniciar Sesion, CU03 Recuperar Contrasena. */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        authService.solicitarRecuperacion(request);
        return ResponseEntity.ok(Map.of("mensaje", "Correo de recuperacion de contrasena enviado exitosamente."));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.restablecerPassword(request);
        return ResponseEntity.ok(Map.of("mensaje", "Contrasena actualizada exitosamente."));
    }
}
