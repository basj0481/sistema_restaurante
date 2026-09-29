package com.restaurante.dto;

import com.restaurante.model.Usuario;

import java.time.LocalDateTime;

public record UsuarioResponse(
        Long id,
        String nombreCompleto,
        String correo,
        String telefono,
        String rol,
        boolean activo,
        LocalDateTime fechaCreacion
) {
    public static UsuarioResponse de(Usuario u) {
        return new UsuarioResponse(u.getId(), u.getNombreCompleto(), u.getCorreo(), u.getTelefono(),
                u.getRol().name(), u.getActivo(), u.getFechaCreacion());
    }
}
