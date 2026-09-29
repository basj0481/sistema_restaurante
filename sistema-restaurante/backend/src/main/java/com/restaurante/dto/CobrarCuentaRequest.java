package com.restaurante.dto;

import com.restaurante.model.MetodoPago;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/** CU08 Cobrar Cuenta. montoRecibido solo aplica para EFECTIVO (FA01). */
public record CobrarCuentaRequest(
        @NotNull MetodoPago metodoPago,
        BigDecimal montoRecibido
) {}
