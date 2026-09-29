package com.restaurante.service;

import com.restaurante.dto.*;
import com.restaurante.exception.BusinessException;
import com.restaurante.exception.ResourceNotFoundException;
import com.restaurante.model.*;
import com.restaurante.repository.*;
import com.restaurante.security.UsuarioPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * CU07 Generar Pedido (Mesero)
 * CU09 Recibir Pedido (Cocina - boton "Recibir")
 * CU10 Marcar Pedido Listo (Cocina - boton "Listo")
 * RN08 - Descuento automatico de inventario segun la receta de cada platillo.
 */
@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final PlatilloRepository platilloRepository;
    private final MesaRepository mesaRepository;
    private final RecetaItemRepository recetaItemRepository;
    private final InventarioService inventarioService;
    private final BitacoraService bitacoraService;

    private static final List<EstadoPedido> ACTIVOS =
            List.of(EstadoPedido.ENVIADO, EstadoPedido.EN_PREPARACION, EstadoPedido.LISTO, EstadoPedido.ENTREGADO);

    // ---------- CU07 Generar Pedido ----------

    @Transactional
    public PedidoResponse crear(CrearPedidoRequest request, Authentication auth) {
        Usuario mesero = usuarioActual(auth);

        Mesa mesa = null;
        if (request.tipo() == TipoPedido.MESA) {
            if (request.numeroMesa() == null) {
                throw new BusinessException("Debe indicar el numero de mesa.");
            }
            mesa = mesaRepository.findByNumero(request.numeroMesa())
                    .orElseThrow(() -> new BusinessException("La mesa indicada no existe."));
        }

        Pedido pedido = Pedido.builder()
                .tipo(request.tipo())
                .mesa(mesa)
                .clienteNombre(request.clienteNombre())
                .clienteTelefono(request.clienteTelefono())
                .mesero(mesero)
                .estado(EstadoPedido.ENVIADO)
                .subtotal(BigDecimal.ZERO)
                .total(BigDecimal.ZERO)
                .build();

        BigDecimal total = BigDecimal.ZERO;

        for (CrearPedidoItemRequest itemReq : request.items()) {
            Platillo platillo = platilloRepository.findById(itemReq.platilloId())
                    .orElseThrow(() -> new ResourceNotFoundException("No existen registros."));

            if (platillo.getEstado() == EstadoPlatillo.AGOTADO) {
                throw new BusinessException("El platillo \"" + platillo.getNombre() + "\" se encuentra agotado.");
            }

            // FA03 - validar disponibilidad de insumos segun receta (RN08)
            List<RecetaItem> receta = recetaItemRepository.findByPlatilloId(platillo.getId());
            for (RecetaItem ri : receta) {
                BigDecimal necesario = ri.getCantidad().multiply(BigDecimal.valueOf(itemReq.cantidad()));
                if (ri.getInsumo().getStockActual().compareTo(necesario) < 0) {
                    throw new BusinessException(
                            "No hay stock suficiente de \"" + ri.getInsumo().getNombre() +
                            "\" para preparar \"" + platillo.getNombre() + "\".");
                }
            }

            PedidoItem item = PedidoItem.builder()
                    .pedido(pedido)
                    .platillo(platillo)
                    .cantidad(itemReq.cantidad())
                    .precioUnitario(platillo.getPrecio())
                    .notas(itemReq.notas())
                    .build();
            pedido.getItems().add(item);

            total = total.add(platillo.getPrecio().multiply(BigDecimal.valueOf(itemReq.cantidad())));
        }

        pedido.setSubtotal(total);
        pedido.setTotal(total);
        pedido = pedidoRepository.save(pedido);

        // Descuento automatico de inventario (RN08)
        for (PedidoItem item : pedido.getItems()) {
            List<RecetaItem> receta = recetaItemRepository.findByPlatilloId(item.getPlatillo().getId());
            for (RecetaItem ri : receta) {
                BigDecimal cantidadADescontar = ri.getCantidad().multiply(BigDecimal.valueOf(item.getCantidad()));
                inventarioService.ajustarStock(ri.getInsumo(), cantidadADescontar.negate(),
                        TipoMovimientoInventario.DESCUENTO_PEDIDO, mesero, pedido,
                        "Descuento automatico por pedido #" + pedido.getId() + ".");
            }
        }

        bitacoraService.registrarTransaccion(mesero, "GENERAR_PEDIDO",
                "Pedido #" + pedido.getId() + " enviado a cocina. Total: Q" + pedido.getTotal() + ".");

        return PedidoResponse.de(pedido);
    }

    @Transactional
    public void cancelar(Long pedidoId, CancelarPedidoRequest request, Authentication auth) {
        Pedido pedido = obtener(pedidoId);
        if (pedido.getEstado() == EstadoPedido.COBRADO || pedido.getEstado() == EstadoPedido.CANCELADO) {
            throw new BusinessException("El pedido no se puede cancelar en su estado actual.");
        }

        Usuario usuario = usuarioActual(auth);

        // Restituir inventario descontado (RN08)
        for (PedidoItem item : pedido.getItems()) {
            List<RecetaItem> receta = recetaItemRepository.findByPlatilloId(item.getPlatillo().getId());
            for (RecetaItem ri : receta) {
                BigDecimal cantidad = ri.getCantidad().multiply(BigDecimal.valueOf(item.getCantidad()));
                inventarioService.ajustarStock(ri.getInsumo(), cantidad,
                        TipoMovimientoInventario.RESTITUCION_CANCELACION, usuario, pedido,
                        "Restitucion por cancelacion del pedido #" + pedido.getId() + ".");
            }
        }

        pedido.setEstado(EstadoPedido.CANCELADO);
        pedido.setMotivoCancelacion(request.motivo());
        pedidoRepository.save(pedido);

        bitacoraService.registrarTransaccion(usuario, "CANCELAR_PEDIDO",
                "Pedido #" + pedido.getId() + " cancelado. Motivo: " + request.motivo());
    }

    public List<PedidoResponse> listarDelMesero(Authentication auth) {
        Usuario mesero = usuarioActual(auth);
        return pedidoRepository.findByMeseroIdAndEstadoInOrderByFechaCreacionDesc(mesero.getId(), ACTIVOS)
                .stream().map(PedidoResponse::de).toList();
    }

    public PedidoResponse obtenerResponse(Long id) {
        return PedidoResponse.de(obtener(id));
    }

    public Pedido obtener(Long id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existen registros."));
    }

    // ---------- CU09 Recibir Pedido / CU10 Marcar Pedido Listo ----------

    public List<PedidoResponse> colaDeCocina() {
        return pedidoRepository
                .findByEstadoInOrderByFechaCreacionAsc(List.of(EstadoPedido.ENVIADO, EstadoPedido.EN_PREPARACION))
                .stream().map(PedidoResponse::de).toList();
    }

    @Transactional
    public PedidoResponse recibir(Long pedidoId, Authentication auth) {
        Pedido pedido = obtener(pedidoId);
        if (pedido.getEstado() != EstadoPedido.ENVIADO) {
            throw new BusinessException("El pedido ya fue recibido o no esta disponible.");
        }
        pedido.setEstado(EstadoPedido.EN_PREPARACION);
        pedido.setFechaRecibido(java.time.LocalDateTime.now());
        pedidoRepository.save(pedido);

        Usuario cocina = usuarioActual(auth);
        bitacoraService.registrarTransaccion(cocina, "RECIBIR_PEDIDO",
                "Pedido #" + pedido.getId() + " recibido en cocina.");

        return PedidoResponse.de(pedido);
    }

    @Transactional
    public PedidoResponse marcarListo(Long pedidoId, Authentication auth) {
        Pedido pedido = obtener(pedidoId);
        if (pedido.getEstado() != EstadoPedido.EN_PREPARACION) {
            throw new BusinessException("El pedido debe estar en preparacion para marcarlo como listo.");
        }
        pedido.setEstado(EstadoPedido.LISTO);
        pedido.setFechaListo(java.time.LocalDateTime.now());
        pedidoRepository.save(pedido);

        Usuario cocina = usuarioActual(auth);
        bitacoraService.registrarTransaccion(cocina, "PEDIDO_LISTO",
                "Pedido #" + pedido.getId() + " marcado como listo.");

        return PedidoResponse.de(pedido);
    }

    private Usuario usuarioActual(Authentication auth) {
        return ((UsuarioPrincipal) auth.getPrincipal()).getUsuario();
    }
}
