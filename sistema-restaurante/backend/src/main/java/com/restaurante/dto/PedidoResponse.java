package com.restaurante.dto;

import com.restaurante.model.Pedido;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record PedidoResponse(
        Long id,
        String tipo,
        Integer numeroMesa,
        String clienteNombre,
        String mesero,
        String estado,
        BigDecimal subtotal,
        BigDecimal total,
        LocalDateTime fechaCreacion,
        LocalDateTime fechaRecibido,
        LocalDateTime fechaListo,
        List<PedidoItemResponse> items
) {
    public static PedidoResponse de(Pedido p) {
        return new PedidoResponse(
                p.getId(), p.getTipo().name(),
                p.getMesa() != null ? p.getMesa().getNumero() : null,
                p.getClienteNombre(),
                p.getMesero().getNombreCompleto(),
                p.getEstado().name(), p.getSubtotal(), p.getTotal(),
                p.getFechaCreacion(), p.getFechaRecibido(), p.getFechaListo(),
                p.getItems().stream().map(PedidoItemResponse::de).toList()
        );
    }
}
