package com.restaurante.controller;

import com.restaurante.dto.LlamadoResponse;
import com.restaurante.service.LlamadoService;
import com.restaurante.service.MenuPublicoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * CU12 Ver Menu (vía Codigo QR) y CU13 Llamar al Mesero (vía Codigo QR).
 * Endpoints publicos, sin autenticacion (ver SecurityConfig: /api/publico/**).
 */
@RestController
@RequestMapping("/api/publico")
@RequiredArgsConstructor
public class PublicoController {

    private final MenuPublicoService menuPublicoService;
    private final LlamadoService llamadoService;

    /** CU12 - el Cliente escanea el QR de su mesa y consulta el menu. */
    @GetMapping("/mesas/{codigoQr}/menu")
    public ResponseEntity<Map<String, Object>> verMenu(@PathVariable String codigoQr) {
        return ResponseEntity.ok(menuPublicoService.obtenerMenuPorQr(codigoQr));
    }

    /** CU13 - el Cliente solicita la atencion de un Mesero desde el menu digital de su mesa. */
    @PostMapping("/mesas/{codigoQr}/llamar-mesero")
    public ResponseEntity<LlamadoResponse> llamarMesero(@PathVariable String codigoQr) {
        return ResponseEntity.ok(llamadoService.crearLlamado(codigoQr));
    }
}
