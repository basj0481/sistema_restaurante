package com.restaurante.dto;

import com.restaurante.model.MovimientoInventario;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record MovimientoInventarioResponse(
        Long id,
        String tipo,
        BigDecimal cantidad,
        String usuario,
        String motivo,
        LocalDateTime fecha
) {
    public static MovimientoInventarioResponse de(MovimientoInventario m) {
        return new MovimientoInventarioResponse(m.getId(), m.getTipo().name(), m.getCantidad(),
                m.getUsuario() != null ? m.getUsuario().getNombreCompleto() : "Sistema",
                m.getMotivo(), m.getFecha());
    }
}
