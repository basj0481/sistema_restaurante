package com.restaurante.controller;

import com.restaurante.dto.*;
import com.restaurante.service.InventarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * CU05 Consultar Inventario (Administrador y Cocina pueden LEER, ver SecurityConfig).
 * CU11 Agregar Productos al Inventario (solo Cocina puede escribir; validado en InventarioService).
 */
@RestController
@RequestMapping("/api/inventario")
@RequiredArgsConstructor
public class InventarioController {

    private final InventarioService inventarioService;

    @GetMapping
    public ResponseEntity<List<InsumoResponse>> listar() {
        return ResponseEntity.ok(inventarioService.listar());
    }

    @GetMapping("/{id}/movimientos")
    public ResponseEntity<List<MovimientoInventarioResponse>> historial(@PathVariable Long id) {
        return ResponseEntity.ok(inventarioService.historialDeInsumo(id));
    }

    @PostMapping("/entrada")
    public ResponseEntity<InsumoResponse> registrarEntrada(@Valid @RequestBody RegistrarEntradaInventarioRequest request,
                                                            Authentication auth) {
        return ResponseEntity.ok(inventarioService.registrarEntrada(request, auth));
    }

    @PostMapping
    public ResponseEntity<InsumoResponse> crearInsumo(@Valid @RequestBody CrearInsumoRequest request, Authentication auth) {
        return ResponseEntity.ok(inventarioService.crearInsumo(request, auth));
    }
}
