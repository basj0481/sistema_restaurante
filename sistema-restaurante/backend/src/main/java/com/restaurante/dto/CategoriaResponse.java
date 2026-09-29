package com.restaurante.dto;

import com.restaurante.model.CategoriaMenu;

public record CategoriaResponse(Long id, String nombre, Integer orden) {
    public static CategoriaResponse de(CategoriaMenu c) {
        return new CategoriaResponse(c.getId(), c.getNombre(), c.getOrden());
    }
}
