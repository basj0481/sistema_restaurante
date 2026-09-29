package com.restaurante.controller;

import com.restaurante.dto.CancelarPedidoRequest;
import com.restaurante.dto.CrearPedidoRequest;
import com.restaurante.dto.PedidoResponse;
import com.restaurante.service.PedidoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** CU07 Generar Pedido (Mesero, ver SecurityConfig). */
@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
public class PedidoController {

    private final PedidoService pedidoService;

    @PostMapping
    public ResponseEntity<PedidoResponse> crear(@Valid @RequestBody CrearPedidoRequest request, Authentication auth) {
        return ResponseEntity.ok(pedidoService.crear(request, auth));
    }

    @GetMapping("/mios")
    public ResponseEntity<List<PedidoResponse>> misPedidos(Authentication auth) {
        return ResponseEntity.ok(pedidoService.listarDelMesero(auth));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PedidoResponse> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.obtenerResponse(id));
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<Void> cancelar(@PathVariable Long id, @Valid @RequestBody CancelarPedidoRequest request,
                                          Authentication auth) {
        pedidoService.cancelar(id, request, auth);
        return ResponseEntity.noContent().build();
    }
}
