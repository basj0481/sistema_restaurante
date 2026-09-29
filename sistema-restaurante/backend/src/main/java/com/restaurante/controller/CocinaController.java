package com.restaurante.controller;

import com.restaurante.dto.PedidoResponse;
import com.restaurante.service.PedidoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * CU09 Recibir Pedido (boton "Recibir") y CU10 Marcar Pedido Listo (boton "Listo").
 * Rol Cocina (ver SecurityConfig).
 */
@RestController
@RequestMapping("/api/cocina")
@RequiredArgsConstructor
public class CocinaController {

    private final PedidoService pedidoService;

    /** Cola de pedidos entrantes, ordenados por hora de llegada (CU09 paso 1-2). */
    @GetMapping("/pedidos")
    public ResponseEntity<List<PedidoResponse>> cola() {
        return ResponseEntity.ok(pedidoService.colaDeCocina());
    }

    /** CU09 - boton "Recibir". */
    @PatchMapping("/pedidos/{id}/recibir")
    public ResponseEntity<PedidoResponse> recibir(@PathVariable Long id, Authentication auth) {
        return ResponseEntity.ok(pedidoService.recibir(id, auth));
    }

    /** CU10 - boton "Listo". */
    @PatchMapping("/pedidos/{id}/listo")
    public ResponseEntity<PedidoResponse> marcarListo(@PathVariable Long id, Authentication auth) {
        return ResponseEntity.ok(pedidoService.marcarListo(id, auth));
    }
}
