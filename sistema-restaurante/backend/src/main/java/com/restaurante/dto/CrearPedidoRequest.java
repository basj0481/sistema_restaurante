package com.restaurante.dto;

import com.restaurante.model.TipoPedido;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/** CU07 Generar Pedido. Si tipo=MESA -> numeroMesa obligatorio. Si PARA_LLEVAR -> datos de cliente. */
public record CrearPedidoRequest(
        @NotNull TipoPedido tipo,
        Integer numeroMesa,
        String clienteNombre,
        String clienteTelefono,
        @NotEmpty @Valid List<CrearPedidoItemRequest> items
) {}
