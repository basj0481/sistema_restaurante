package com.restaurante.dto;

import java.math.BigDecimal;
import java.util.List;

public record ReporteVentasResponse(
        BigDecimal totalVentas,
        long cantidadPedidos,
        BigDecimal totalEfectivo,
        BigDecimal totalTarjeta,
        List<PlatilloVendidoResponse> platillosMasVendidos,
        List<VentaPorMeseroResponse> ventasPorMesero
) {}
