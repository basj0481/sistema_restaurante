package com.restaurante.dto;

import com.restaurante.model.LlamadoMesero;

import java.time.LocalDateTime;

public record LlamadoResponse(
        Long id,
        Integer numeroMesa,
        String estado,
        LocalDateTime fechaCreacion
) {
    public static LlamadoResponse de(LlamadoMesero l) {
        return new LlamadoResponse(l.getId(), l.getMesa().getNumero(), l.getEstado().name(), l.getFechaCreacion());
    }
}
