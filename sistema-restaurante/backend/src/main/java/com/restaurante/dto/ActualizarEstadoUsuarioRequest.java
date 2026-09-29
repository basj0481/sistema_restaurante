package com.restaurante.dto;

import jakarta.validation.constraints.NotNull;

public record ActualizarEstadoUsuarioRequest(@NotNull Boolean activo) {}
