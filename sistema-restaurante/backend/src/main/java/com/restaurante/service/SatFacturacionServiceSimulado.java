package com.restaurante.service;

import com.restaurante.model.Pedido;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Implementacion SIMULADA de la certificacion fiscal (CU08). Genera un numero de
 * autorizacion con formato UUID, igual al que devuelve un certificador FEL real en
 * Guatemala, para que el resto del sistema (Pago, comprobante, reportes) ya trabaje
 * con esa forma de dato.
 *
 * IMPORTANTE: esto NO llama a la SAT de verdad. Para produccion, sustituir esta clase
 * por una que llame a un certificador autorizado (p. ej. Digifact, Megaprint, Infile),
 * firmando y enviando el XML del DTE segun el estandar FEL, y devolviendo la serie y el
 * numero de autorizacion reales que la SAT certifique.
 */
@Service
@Slf4j
public class SatFacturacionServiceSimulado implements SatFacturacionService {

    private static final String SERIE = "A1";

    @Override
    public EmisionSat emitir(Pedido pedido, BigDecimal total) {
        String numeroAutorizacion = UUID.randomUUID().toString();
        log.info("[SAT-SIMULADO] Emitiendo comprobante para pedido #{} por Q{} -> autorizacion {}",
                pedido.getId(), total, numeroAutorizacion);
        return new EmisionSat(SERIE, numeroAutorizacion);
    }
}
