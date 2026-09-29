package com.restaurante.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

/** CU11 FA02: crear un nuevo insumo. */
public record CrearInsumoRequest(
        @NotBlank String nombre,
        @NotBlank String unidadMedida,
        @NotNull @PositiveOrZero BigDecimal stockInicial,
        @NotNull @PositiveOrZero BigDecimal stockMinimo
) {}
