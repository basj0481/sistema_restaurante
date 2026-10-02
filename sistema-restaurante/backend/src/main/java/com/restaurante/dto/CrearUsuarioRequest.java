package com.restaurante.dto;

import com.restaurante.model.Rol;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.time.LocalTime;

/**
 * CU02 Registrar Usuario. Campos g. Horario de trabajo (horaInicioTrabajo/horaFinTrabajo,
 * seleccion por hora) y h. Salario (se guarda en la tabla "planilla", Ver Planilla.java).
 */
public record CrearUsuarioRequest(
        @NotBlank String nombreCompleto,
        @NotBlank @Email String correo,
        String telefono,
        @NotNull Rol rol,
        @NotBlank String passwordTemporal,
        @NotNull LocalTime horaInicioTrabajo,
        @NotNull LocalTime horaFinTrabajo,
        @NotNull @PositiveOrZero BigDecimal salario
) {}
