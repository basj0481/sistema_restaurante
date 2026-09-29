package com.restaurante.dto;

import com.restaurante.model.Bitacora;

import java.time.LocalDateTime;

public record BitacoraResponse(
        Long id,
        String tipo,
        String usuario,
        String accion,
        String detalle,
        LocalDateTime fecha
) {
    public static BitacoraResponse de(Bitacora b) {
        return new BitacoraResponse(b.getId(), b.getTipo().name(),
                b.getUsuario() != null ? b.getUsuario().getNombreCompleto() : "Sistema",
                b.getAccion(), b.getDetalle(), b.getFecha());
    }
}
