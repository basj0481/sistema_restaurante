package com.restaurante.dto;

public record LoginResponse(
        String token,
        Long id,
        String nombreCompleto,
        String correo,
        String rol,
        boolean debeCambiarPassword
) {}
