package com.restaurante.dto;

import jakarta.validation.constraints.NotBlank;

public record CancelarPedidoRequest(@NotBlank String motivo) {}
