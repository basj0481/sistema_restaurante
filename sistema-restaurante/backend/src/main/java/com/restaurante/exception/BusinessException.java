package com.restaurante.exception;

/** Errores de regla de negocio (Ver Anexo AN02 de Reglas de Negocio). */
public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}
