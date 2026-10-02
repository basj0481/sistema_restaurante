package com.restaurante.dto;

import com.restaurante.model.Usuario;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record UsuarioResponse(
        Long id,
        String nombreCompleto,
        String correo,
        String telefono,
        String rol,
        boolean activo,
        LocalDateTime fechaCreacion,
        LocalTime horaInicioTrabajo,
        LocalTime horaFinTrabajo,
        BigDecimal salario
) {
    public static UsuarioResponse de(Usuario u, BigDecimal salario) {
        return new UsuarioResponse(u.getId(), u.getNombreCompleto(), u.getCorreo(), u.getTelefono(),
                u.getRol().name(), u.getActivo(), u.getFechaCreacion(),
                u.getHoraInicioTrabajo(), u.getHoraFinTrabajo(), salario);
    }
}
