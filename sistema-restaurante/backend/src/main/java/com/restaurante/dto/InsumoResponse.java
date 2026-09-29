package com.restaurante.dto;

import com.restaurante.model.Insumo;

import java.math.BigDecimal;

public record InsumoResponse(
        Long id,
        String nombre,
        String unidadMedida,
        BigDecimal stockActual,
        BigDecimal stockMinimo,
        boolean stockBajo
) {
    public static InsumoResponse de(Insumo i) {
        return new InsumoResponse(i.getId(), i.getNombre(), i.getUnidadMedida(),
                i.getStockActual(), i.getStockMinimo(), i.getStockBajo());
    }
}
