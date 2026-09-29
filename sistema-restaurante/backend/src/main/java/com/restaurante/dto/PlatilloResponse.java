package com.restaurante.dto;

import com.restaurante.model.Platillo;

import java.math.BigDecimal;

public record PlatilloResponse(
        Long id,
        String nombre,
        Long categoriaId,
        String categoriaNombre,
        BigDecimal precio,
        String descripcion,
        String fotoUrl,
        String estado
) {
    public static PlatilloResponse de(Platillo p) {
        return new PlatilloResponse(p.getId(), p.getNombre(), p.getCategoria().getId(),
                p.getCategoria().getNombre(), p.getPrecio(), p.getDescripcion(), p.getFotoUrl(),
                p.getEstado().name());
    }
}
