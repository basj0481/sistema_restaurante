package com.restaurante.service;

import com.restaurante.dto.CobrarCuentaRequest;
import com.restaurante.dto.PagoResponse;
import com.restaurante.exception.BusinessException;
import com.restaurante.model.*;
import com.restaurante.repository.PagoRepository;
import com.restaurante.repository.PedidoRepository;
import com.restaurante.security.UsuarioPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/** CU08 Cobrar Cuenta (Mesero) - Efectivo o Tarjeta. */
@Service
@RequiredArgsConstructor
public class CobroService {

    private final PedidoRepository pedidoRepository;
    private final PagoRepository pagoRepository;
    private final BitacoraService bitacoraService;

    @Transactional
    public PagoResponse cobrar(Long pedidoId, CobrarCuentaRequest request, Authentication auth) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new BusinessException("No existen registros."));

        if (pedido.getEstado() != EstadoPedido.LISTO && pedido.getEstado() != EstadoPedido.ENTREGADO) {
            throw new BusinessException("El pedido debe estar Listo o Entregado para poder cobrarse.");
        }

        BigDecimal cambio = BigDecimal.ZERO;
        BigDecimal montoRecibido = request.montoRecibido();

        if (request.metodoPago() == MetodoPago.EFECTIVO) {
            // FA01 - el monto recibido no puede ser menor al total
            if (montoRecibido == null || montoRecibido.compareTo(pedido.getTotal()) < 0) {
                throw new BusinessException("El monto ingresado no coincide con el total a pagar.");
            }
            cambio = montoRecibido.subtract(pedido.getTotal());
        }
        // FA02 (Tarjeta): en este alcance se asume aprobacion inmediata de la terminal simulada.

        Usuario mesero = usuarioActual(auth);
        String numeroComprobante = "CMP-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + "-" + pedido.getId();

        Pago pago = Pago.builder()
                .pedido(pedido)
                .mesero(mesero)
                .metodoPago(request.metodoPago())
                .total(pedido.getTotal())
                .montoRecibido(montoRecibido)
                .cambio(cambio)
                .numeroComprobante(numeroComprobante)
                .build();
        pago = pagoRepository.save(pago);

        pedido.setEstado(EstadoPedido.COBRADO);
        pedido.setFechaCobrado(LocalDateTime.now());
        pedidoRepository.save(pedido);

        bitacoraService.registrarTransaccion(mesero, "COBRAR_CUENTA",
                "Pedido #" + pedido.getId() + " cobrado (" + request.metodoPago() + "). Total: Q" + pedido.getTotal() + ".");

        return PagoResponse.de(pago);
    }

    private Usuario usuarioActual(Authentication auth) {
        return ((UsuarioPrincipal) auth.getPrincipal()).getUsuario();
    }
}
