package com.restaurante.dto;

import com.restaurante.model.Rol;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CrearUsuarioRequest(
        @NotBlank String nombreCompleto,
        @NotBlank @Email String correo,
        String telefono,
        @NotNull Rol rol,
        @NotBlank String passwordTemporal
) {}
