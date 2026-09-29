package com.restaurante.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CrearPedidoItemRequest(
        @NotNull Long platilloId,
        @NotNull @Positive Integer cantidad,
        String notas
) {}
