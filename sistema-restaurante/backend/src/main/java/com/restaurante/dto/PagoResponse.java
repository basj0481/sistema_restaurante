package com.restaurante.dto;

import com.restaurante.model.Pago;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PagoResponse(
        Long id,
        Long pedidoId,
        String metodoPago,
        BigDecimal total,
        BigDecimal montoRecibido,
        BigDecimal cambio,
        String numeroComprobante,
        String serieSat,
        String numeroAutorizacionSat,
        LocalDateTime fecha
) {
    public static PagoResponse de(Pago p) {
        return new PagoResponse(p.getId(), p.getPedido().getId(), p.getMetodoPago().name(),
                p.getTotal(), p.getMontoRecibido(), p.getCambio(), p.getNumeroComprobante(),
                p.getSerieSat(), p.getNumeroAutorizacionSat(), p.getFecha());
    }
}
