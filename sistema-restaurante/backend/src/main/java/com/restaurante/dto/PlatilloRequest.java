package com.restaurante.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.List;

public record PlatilloRequest(
        @NotBlank String nombre,
        @NotNull Long categoriaId,
        @NotNull @Positive BigDecimal precio,
        String descripcion,
        String fotoUrl,
        List<RecetaItemRequest> receta
) {}
