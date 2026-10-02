package com.restaurante.service;

import com.restaurante.model.Pedido;

/**
 * CU08 Cobrar Cuenta: "El sistema muestra la opción de imprimir o enviar el comprobante
 * (asociada con la API de la SAT)." Abstrae la certificacion fiscal del comprobante
 * (Factura Electronica en Linea - FEL) ante la Superintendencia de Administracion
 * Tributaria de Guatemala, para poder sustituir la implementacion simulada por una
 * integracion real (p. ej. vía un certificador autorizado) sin tocar CobroService.
 */
public interface SatFacturacionService {
    EmisionSat emitir(Pedido pedido, java.math.BigDecimal total);

    record EmisionSat(String serie, String numeroAutorizacion) {}
}
