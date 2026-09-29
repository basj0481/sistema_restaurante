package com.restaurante.service;

import com.restaurante.dto.PlatilloVendidoResponse;
import com.restaurante.dto.ReporteVentasResponse;
import com.restaurante.dto.VentaPorMeseroResponse;
import com.restaurante.exception.BusinessException;
import com.restaurante.model.EstadoPedido;
import com.restaurante.model.MetodoPago;
import com.restaurante.model.Pago;
import com.restaurante.model.Pedido;
import com.restaurante.repository.PagoRepository;
import com.restaurante.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** CU06 Generar Reporte de Ventas (Administrador). Solo considera pedidos COBRADOS (Ver CU08). */
@Service
@RequiredArgsConstructor
public class ReporteService {

    private final PedidoRepository pedidoRepository;
    private final PagoRepository pagoRepository;

    public ReporteVentasResponse generar(LocalDateTime desde, LocalDateTime hasta) {
        if (desde == null || hasta == null || desde.isAfter(hasta)) {
            // RN AN02 No.13
            throw new BusinessException("El rango de fechas seleccionado para el reporte no es valido.");
        }

        List<Pedido> pedidos = pedidoRepository.findByEstadoAndFechaCobradoBetween(EstadoPedido.COBRADO, desde, hasta);

        BigDecimal totalVentas = pedidos.stream().map(Pedido::getTotal).reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalEfectivo = BigDecimal.ZERO;
        BigDecimal totalTarjeta = BigDecimal.ZERO;
        for (Pedido p : pedidos) {
            for (Pago pago : pagoRepository.findAll()) {
                if (pago.getPedido().getId().equals(p.getId())) {
                    if (pago.getMetodoPago() == MetodoPago.EFECTIVO) totalEfectivo = totalEfectivo.add(pago.getTotal());
                    else totalTarjeta = totalTarjeta.add(pago.getTotal());
                }
            }
        }

        Map<String, long[]> conteoPorPlatillo = new LinkedHashMap<>();
        Map<String, BigDecimal> totalPorPlatillo = new LinkedHashMap<>();
        Map<String, Long> conteoPorMesero = new LinkedHashMap<>();
        Map<String, BigDecimal> totalPorMesero = new LinkedHashMap<>();

        for (Pedido p : pedidos) {
            String mesero = p.getMesero().getNombreCompleto();
            conteoPorMesero.merge(mesero, 1L, Long::sum);
            totalPorMesero.merge(mesero, p.getTotal(), BigDecimal::add);

            p.getItems().forEach(item -> {
                if (!item.getCancelado()) {
                    String nombre = item.getPlatillo().getNombre();
                    conteoPorPlatillo.merge(nombre, new long[]{item.getCantidad()},
                            (a, b) -> new long[]{a[0] + b[0]});
                    BigDecimal totalItem = item.getPrecioUnitario().multiply(BigDecimal.valueOf(item.getCantidad()));
                    totalPorPlatillo.merge(nombre, totalItem, BigDecimal::add);
                }
            });
        }

        List<PlatilloVendidoResponse> platillosMasVendidos = conteoPorPlatillo.entrySet().stream()
                .map(e -> new PlatilloVendidoResponse(e.getKey(), e.getValue()[0], totalPorPlatillo.get(e.getKey())))
                .sorted(Comparator.comparingLong(PlatilloVendidoResponse::cantidadVendida).reversed())
                .toList();

        List<VentaPorMeseroResponse> ventasPorMesero = conteoPorMesero.entrySet().stream()
                .map(e -> new VentaPorMeseroResponse(e.getKey(), e.getValue(), totalPorMesero.get(e.getKey())))
                .sorted(Comparator.comparing(VentaPorMeseroResponse::totalVendido).reversed())
                .toList();

        return new ReporteVentasResponse(totalVentas, pedidos.size(), totalEfectivo, totalTarjeta,
                platillosMasVendidos, ventasPorMesero);
    }
}
