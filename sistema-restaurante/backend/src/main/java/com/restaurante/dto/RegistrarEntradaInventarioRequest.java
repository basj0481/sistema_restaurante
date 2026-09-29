package com.restaurante.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

/** CU11 FA01: registrar entrada de un insumo EXISTENTE. */
public record RegistrarEntradaInventarioRequest(
        @NotNull Long insumoId,
        @NotNull @Positive BigDecimal cantidad,
        String motivo
) {}
