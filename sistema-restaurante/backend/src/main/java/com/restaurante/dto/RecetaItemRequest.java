package com.restaurante.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record RecetaItemRequest(
        @NotNull Long insumoId,
        @NotNull @Positive BigDecimal cantidad
) {}
