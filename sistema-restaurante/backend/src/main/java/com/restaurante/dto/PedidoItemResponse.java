package com.restaurante.dto;

import com.restaurante.model.PedidoItem;

import java.math.BigDecimal;

public record PedidoItemResponse(
        Long id,
        Long platilloId,
        String platillo,
        Integer cantidad,
        BigDecimal precioUnitario,
        String notas,
        boolean cancelado
) {
    public static PedidoItemResponse de(PedidoItem it) {
        return new PedidoItemResponse(it.getId(), it.getPlatillo().getId(), it.getPlatillo().getNombre(),
                it.getCantidad(), it.getPrecioUnitario(), it.getNotas(), it.getCancelado());
    }
}
